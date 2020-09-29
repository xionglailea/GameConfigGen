package generator;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import constdef.StringConst;
import define.AbsClassDefine;
import define.AllTypeDefine;
import define.BeanDefine;
import define.CfgDefine;
import define.ConstDefine;
import define.EnumDefine;
import define.ModuleDefine;
import define.column.BeanField;
import define.column.ConstField;
import define.column.EnumField;
import define.type.IList;
import define.type.IType;
import define.type.TypeUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.representer.Representer;


/**
 * 上下文
 * <p>
 * create by xiongjieqing on 2020-07-25 10:23
 */
@Getter
public class Context {

    private static Context ins;

    public static Context getIns() {
        if (ins == null) {
            ins = new Context();
        }
        return ins;
    }

    /**
     * 所有的bean
     */
    private Map<String, BeanDefine> beans = new HashMap<>();
    /**
     * 所有读出来的数据key-》表名，value-》值
     */
    private static Map<String, Object> allData = new HashMap<>();

    private Representer representer;
    private XmlMapper xmlMapper;
    private CfgDefine cfgDefine;
    private List<ModuleDefine> modules = new ArrayList<>();
    private Map<String, AbsClassDefine> constAndEnums = new HashMap<>();
    private AllTypeDefine allTypeDefine = new AllTypeDefine();

    /**
     * 表格配置，有excel配置的
     */
    private Map<String, BeanDefine> tables = new HashMap<>();

    private JavaGenerator javaGenerator = new JavaGenerator();

    /**
     * 默认所有的都输出
     */
    private String group = "all";

    private Context() {
        representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        xmlMapper = new XmlMapper();

    }

    /**
     * 解析配置
     */
    @SneakyThrows
    public void parseDefine(String cfg) {
        //        Yaml yaml = new Yaml(representer);
        File cfgFile = new File(cfg);
        //        cfgDefine = yaml.loadAs(FileUtil.readUtf8String(cfgFile), CfgDefine.class);

        cfgDefine = xmlMapper.readValue(FileUtil.readUtf8String(cfgFile), CfgDefine.class);

        String parentDir = cfgFile.getParent();
        for (String sub : cfgDefine.getIncludes()) {
            File moduleFile = new File(parentDir, sub);
            ModuleDefine moduleDefine = xmlMapper.readValue(FileUtil.readUtf8String(moduleFile), ModuleDefine.class);
            parseModule(moduleDefine);
        }
        allTypeDefine.setRootPackage(cfgDefine.getRootPackage());
        allTypeDefine.setExtensionFileName("Extensions");

    }

    private void parseModule(ModuleDefine moduleDefine) {
        modules.add(moduleDefine);
        //枚举
        for (var e : moduleDefine.getEnums()) {
            e.setPackageName(getRootPackage() + "." + moduleDefine.getPackageName());
            e.setFullName(e.getPackageName() + "." + e.getName());
            parseEnum(e);
            constAndEnums.put(e.getFullName(), e);
        }
        //常量
        for (var e : moduleDefine.getConsts()) {
            e.setPackageName(getRootPackage() + "." + moduleDefine.getPackageName());
            e.setFullName(e.getPackageName() + "." + e.getName());
            parseConst(e);
            constAndEnums.put(e.getFullName(), e);
        }
        //结构体
        for (var e : moduleDefine.getBeans()) {
            e.setPackageName(getRootPackage() + "." + moduleDefine.getPackageName());
            parseBean(e);
        }
        //配置
        for (var e : moduleDefine.getTables()) {
            e.setPackageName(getRootPackage() + "." + moduleDefine.getPackageName());
            e.setModuleName(moduleDefine.getPackageName());
            parseBean(e);
            tables.put(e.getName(), e);

        }

    }

    /**
     * 处理枚举
     */
    private void parseEnum(EnumDefine enumDefine) {
        for (EnumField field : enumDefine.getFields()) {
            field.resolve(enumDefine);
            field.setRunType(
                TypeUtil.create(Collections.singletonList("int"), enumDefine.getPackageName()));
        }
    }

    /**
     * 处理常量
     */
    private void parseConst(ConstDefine constDefine) {
        for (ConstField field : constDefine.getFields()) {
            field.resolve(constDefine);
            field.setRunType(TypeUtil.create(Arrays.asList(field.getType().split("[,;]")),
                constDefine.getPackageName()));
        }
    }

    /**
     * 处理结构体
     */
    private void parseBean(BeanDefine beanDefine) {
        beanDefine.parse();
    }

    private void parseBeanFields(BeanDefine beanDefine) {
        for (BeanField field : beanDefine.getFields()) {
            field.resolve(beanDefine);
            field.setRunType(TypeUtil
                .create(Arrays.asList(field.getType().split("[,;]")), beanDefine.getPackageName()));
            field.getRunType().addExtensionType(type -> allTypeDefine.addType(type));
        }
    }

    /**
     * 解析bean的字段，索引和结构
     */
    public void resolve() {
        //解析bean的字段
        for (var e : beans.values()) {
            parseBeanFields(e);
            e.collectAllFields();
        }

        //将读取数据的配置加到extension接口中
        for (BeanDefine tableBean : tables.values()) {
            //本身做为bean添加到结构中
            IType type = TypeUtil
                .create(Collections.singletonList(tableBean.getName()), tableBean.getPackageName());
            if (tableBean.isSingle()) {
                type.addExtensionType(i -> {
                    if (tableBean.canExport()) {
                        //只有定义导出的数据才导出
                        allTypeDefine.addType(i);
                    }
                });
                tableBean.setReadFileType(type);
            } else {
                IList listType = new IList(type);
                listType.addExtensionType(i -> {
                    if (tableBean.canExport()) {
                        allTypeDefine.addType(i);
                    }
                });
                tableBean.setReadFileType(listType);
            }
            //解析索引
            tableBean.resolveIndex();
        }

    }

    /**
     * 检查配置 同一bean内，字段名的合法性和唯一性检查 索引的合法性，索引名字，被索引的表，必须是一个map结构 子bean的id不能重复
     */
    public void check() {

    }


    /**
     * 检查完后，能生成正确的代码了
     */
    public void genCode() {
        FileUtil.del(".temp/" + cfgDefine.getRootPackage());
        for (var e : constAndEnums.values()) {
            e.genCode(javaGenerator);
        }
        for (var e : beans.values()) {
            e.genCode(javaGenerator);
        }
        allTypeDefine.genCode(javaGenerator);
        javaGenerator.createCfgMgr(cfgDefine.getRootPackage(), "CfgMgr", this);

    }

    /**
     * 加载数据
     */
    public void loadData() throws Exception {
        for (var table : tables.values()) {
            table.load();
        }
        //检查数据的正确性
        for (var table : tables.values()) {
            table.checkData();
        }
    }

    /**
     * 导出数据
     */
    public void exportData() {
        for (var table : tables.values()) {
            var data = table.getExportData();
            if (data != null) {
                FileUtil.writeBytes(data.copyRemainData(),
                    new File(StringConst.DATA_DIR + "/" + table.getName().toLowerCase() + ".data"));
            }
        }
    }

    public String getRootPackage() {
        return cfgDefine.getRootPackage();
    }

    public void putBean(BeanDefine beanDefine) {
        var old = beans.put(beanDefine.getFullName(), beanDefine);
        if (old != null) {
            throw new RuntimeException("bean define duplicate >>>" + beanDefine.getFullName());
        }
    }

    public BeanDefine getBean(String fullName) {
        return beans.get(fullName);
    }

    public EnumDefine getEnum(String fullName) {
        return (EnumDefine) constAndEnums.get(fullName);
    }

    /**是否应该输出
     *
     *
     * @param mark 标记
     */
    public boolean shouldOutPut(String mark) {
        if (mark == null || group.equals("all")) {
            return true;
        }
        return group.equals(mark);
    }

}