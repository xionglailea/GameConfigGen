package define.type;

import cn.hutool.core.lang.Pair;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import define.BeanDefine;
import define.column.BeanField;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataBean;
import generator.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * 结构体
 * <p>
 * create by xiongjieqing on 2020-07-24 16:52
 */
@Getter
@Slf4j
public class IBean implements IType {

    private BeanDefine beanDefine;

    public IBean(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
    }

    @Override
    public String getTypeName() {
        return beanDefine.getName();
    }

    @Override
    public String getJavaType() {
        return beanDefine.getFullName();
    }

    @Override
    public String getCsType() {
        return beanDefine.getFullName();
    }

    @Override
    public String toString() {
        return beanDefine.getFullName().replace(".", "_");
    }

    @Override
    public void addExtensionType(Consumer<IType> consumer) {
        consumer.accept(this);
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        var fieldData = new ArrayList<IData>();
        var actualType = beanDefine;
        if (beanDefine.isDynamic()) {
            String subTypeName = dataSource.getNextNotEmpty();
            actualType = getActualBeanDefine(subTypeName);
        }
        for (var field : actualType.getAllFields()) {
            try {
                IData data = field.getRunType().convert(dataSource);
                data.setCanExport(field.canExport());
                fieldData.add(data);
            } catch (Exception ex) {
                log.error("解析 {} 的字段 {} 数据失败, 当前记录为 {}, 错误详细信息为", actualType.getName(), field.getName(), fieldData);
                ex.printStackTrace();
                //直接退出
                System.exit(0);
            }
        }
        return new IDataBean(beanDefine, actualType, fieldData);
    }

    private BeanDefine getActualBeanDefine(String subTypeName) {
        BeanDefine actualType = Context.getIns().getBean(beanDefine.getPackageName() + "." + subTypeName);
        if (actualType == null) {
            throw new RuntimeException(String.format("抽象类%s的子类%s不存在", beanDefine.getName(), subTypeName));
        }
        if (actualType.isDynamic()) {
            throw new RuntimeException(String.format("抽象类%s的子类%s不能作为数据源配置，", beanDefine.getName(), subTypeName));
        }
        return actualType;
    }


    @Override
    public IData convert(JsonElement jsonElement) {
        var fieldData = new ArrayList<IData>();
        var actualType = beanDefine;
        if (beanDefine.isDynamic()) {
            String subTypeName = jsonElement.getAsJsonObject().get("subType").getAsString();
            actualType = getActualBeanDefine(subTypeName);
        }
        for (var field : actualType.getAllFields()) {
            try {
                JsonElement fieldObject = jsonElement.getAsJsonObject().get(field.getName());
                IData data = field.getRunType().convert(fieldObject);
                data.setCanExport(field.canExport());
                fieldData.add(data);
            } catch (Exception ex) {
                log.error("解析 {} 的字段 {} 数据失败, 当前记录为 {}, 错误详细信息为", actualType.getName(), field.getName(), fieldData);
                ex.printStackTrace();
                //直接退出
                System.exit(0);
            }
        }
        return new IDataBean(beanDefine, actualType, fieldData);
    }

    @Override
    public IData convert(Node node) {
        GridPane gridPane;
        if (node instanceof TitledPane) {
            gridPane = (GridPane) ((TitledPane)node).getContent();
        } else {
            gridPane = (GridPane) node;
        }
        ArrayList<Pair<BeanField, Node>> allFields = (ArrayList<Pair<BeanField, Node>>) gridPane.getProperties().get("fields");
        BeanDefine actual = beanDefine;
        var subType = gridPane.getProperties().get("subType");
        if (beanDefine.isDynamic() && subType == null) {
            //没有选择具体的数据类型，得报错
            return null;
        }
        if (subType != null) {
            actual = (BeanDefine) subType;
        }
        List<IData> data = new ArrayList<>();
        for (Pair<BeanField, Node> entry : allFields) {
            var field = entry.getKey();
            var value = entry.getValue();
            var temp = field.getRunType().convert(value);
            if (temp != null) {
                data.add(temp);
            }
        }
        return new IDataBean(beanDefine, actual, data);
    }
}
