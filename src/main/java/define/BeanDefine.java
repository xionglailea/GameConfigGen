package define;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import constdef.StringConst;
import datastream.Octets;
import define.column.BeanField;
import define.data.DataCreator;
import define.data.type.IData;
import define.data.type.IDataBean;
import define.data.type.IDataList;
import define.type.IBean;
import define.type.IType;
import generator.Context;
import generator.language.AbsGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * 结构体定义
 * <p>
 * create by xiongjieqing on 2020/7/23 18:50
 */
@Getter
@Setter
@Slf4j
public class BeanDefine extends AbsClassDefine {

    private String name;
    private int id;
    private String comment;

    private boolean config;//是否是表
    //属于表格的配置
    private String inputFile; //多个输入文件用分号；隔离
    private String index;
    private boolean single = false; //标识map还是单个数据
    @JacksonXmlProperty(localName = "field")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<BeanField> fields = new ArrayList<>(); // 字段列表
    /**
     * 直接孩子
     */
    @JacksonXmlProperty(localName = "child")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<BeanDefine> children = new ArrayList<>();

    //定义的进一步解析
    @JsonIgnore
    private BeanDefine parent = null;
    private String moduleName; //模块名，定义在moduleDefine中
    private boolean dynamic = false;
    private boolean hasParent = false;

    private String[] inputFiles; //所有的输入文件
    @JsonIgnore
    private List<BeanDefine> leafChildren = new ArrayList<>();//叶子结点
    @JsonIgnore
    private List<BeanField> allFields = new ArrayList<>(); //所有的字段，包括父类中定义的
    //生成代码使用
    @JsonIgnore
    private IType readFileType; //生成读取代码时使用的类型
    @JsonIgnore
    private BeanField indexField; //索引的类型

    //数据 需要用这个注解，表示在jackson反序列化(从xml生成对应的java类)的时候可以忽略
    @JsonIgnore
    private List<IData> records = new ArrayList<>();
    @JsonIgnore
    private Map<IData, IData> recordsByIndex = new HashMap<>();


    public void parse() {
        if (inputFile != null) {
            //如果需要读取表格
            config = true;
            String[] temp = inputFile.split("[,;]");
            inputFiles = new String[temp.length];
            for (int i = 0; i < temp.length; i++) {
                inputFiles[i] = StringConst.INPUT_DIR + "/" + moduleName + "/" + temp[i].trim();
            }
        }

        if (!config && getGroup() != null) {
            throw new RuntimeException(String.format("%s 结构体不应该有group字段", name));
        }
        linkParent(parent, this, getPackageName());
        for (var leaf : leafChildren) {
            if (leaf.getId() == 0) {
                leaf.setId(Math.abs(leaf.getName().hashCode() % 65535));
            }
        }
        setFullName(getPackageName() + "." + name);
        Context.getIns().putBean(this);
    }


    /**
     * 将子bean的父子关系关联上
     */
    private void linkParent(BeanDefine parent, BeanDefine curBean, String packageName) {
        if (curBean.getChildren().isEmpty()) {
            if (parent != null) {
                parent.getLeafChildren().add(curBean);
            }
            return;
        }
        curBean.setDynamic(true);
        for (var e : curBean.getChildren()) {
            e.setPackageName(packageName);
            e.setFullName(packageName + "." + e.getName());
            e.setParent(curBean);
            e.setHasParent(true);
            Context.getIns().putBean(e);
            linkParent(curBean, e, packageName);
            curBean.getLeafChildren().addAll(e.getLeafChildren());
        }
    }

    /**
     * 收集所有的字段
     */
    public void collectAllFields() {
        List<BeanDefine> hierarchy = new ArrayList<>();
        hierarchy.add(this);
        var temp = this;
        while (temp.getParent() != null) {
            hierarchy.add(temp.getParent());
            temp = temp.getParent();
        }
        for (int i = hierarchy.size() - 1; i >= 0; i--) {
            var bean = hierarchy.get(i);
            allFields.addAll(bean.getFields());
        }
    }

    //解析索引
    public void resolveIndex() {
        //单个数据表 不用索引
        if (single) {
            return;
        }
        if (index == null) {
            //没有指定的话，找一个默认的 能做索引的
            for (BeanField field : fields) {
                if (field.getRunType().canBeIndex()) {
                    indexField = field;
                    return;
                }
            }
        } else {
            for (BeanField field : fields) {
                if (field.getName().equals(index)) {
                    indexField = field;
                    return;
                }
            }
        }
        if (indexField.canExport()) {
            //索引必须要能被导出
            return;
        }
        throw new RuntimeException(String.format("表格 %s 索引解析错误", name));
    }

    @Override
    public void genCode(AbsGenerator generator) {
        if (config && !canExport()) {
            return;
        }
        generator.createBean(getPackageName(), name, this);
    }


    //加载数据
    public void load() throws Exception {
        if (inputFiles == null) {
            return;
        }
        var dataSource = DataCreator.getDataSource(new IBean(this), inputFiles);
        for (var source : dataSource) {
            source.load();
        }
        for (var source : dataSource) {
            var data = source.getData();
            records.addAll(data);
        }
        if (single) {
            if (records.size() != 1) {
                throw new RuntimeException(String.format("%s 是一个单键数据表，只应该有一个数据", name));
            }
        } else {
            for (var record : records) {
                IDataBean dataBean = (IDataBean) record;
                var indexValue = dataBean.getIndexData(indexField.getName());
                if (recordsByIndex.put(indexValue, record) != null) {
                    log.error("table = {}, index = {} 重复", getName(), indexValue);
                }
            }
        }
    }

    public void checkData() {
        //检查数据
        for (var record : records) {
            IData.curValidateData = record;
            IData.curValidateTable = name;
            record.validate();
        }
    }

    //导出数据
    public Octets getExportData() {
        if (!canExport()) {
            return null;
        }
        var os = new Octets();
        if (isSingle()) {
            records.get(0).export(os);
        } else {
            new IDataList(records).export(os);
        }
        return os;
    }

}
