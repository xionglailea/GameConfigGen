package define.type;

import cn.hutool.core.collection.CollUtil;
import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataList;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * 列表
 * <p>
 * create by xiongjieqing on 2020-07-24 16:38
 */
@Getter
public class IList extends AbsComplexType {

    private IType valueType;

    public IList(IType valueType) {
        this.valueType = valueType;
    }

    @Override
    public String getTypeName() {
        return "list";
    }

    @Override
    public String getJavaType() {
        return String.format("java.util.List<%s>", valueType.getJavaBoxType());
    }

    @Override
    public String getCsType() {
        return String.format("System.Collections.Generic.List<%s>", valueType.getCsType());
    }

    @Override
    public String getGoType() {
        return "*list.List";
    }

    @Override
    public String toString() {
        return "list_" + valueType;
    }

    @Override
    public void addExtensionType(Consumer<IType> consumer) {
        consumer.accept(this);
        valueType.addExtensionType(consumer);
    }

    public boolean isDynamic() {
        return valueType instanceof IBean && ((IBean) valueType).isDynamic();
    }

    @Override
    public IData convert(List<String> values, String sep) {
        var dataList = new IDataList();
        if (values.size() == 1) {
            //所有数据在一个单元格内
            String firstSep = sep.substring(0, 1);
            String left = null;
            if (sep.length() > 1) {
                left = sep.substring(1);
            }
            String[] elements = values.get(0).split(replaceRegex(firstSep));
            for (String element : elements) {
                dataList.getValues().add(valueType.convert(CollUtil.newArrayList(element), left));
            }
        } else {
            //数据存放在不同的单元格内
            if (sep == null) {
                //数据全部展开
                if (isDynamic()) {
                    throw new RuntimeException(String.format("%s 存放的是多态数据类型，需要定义分隔符号，放在一个单元格中", getJavaType()));
                }
                while (!values.isEmpty()) {
                    dataList.getValues().add(valueType.convert(values, null));
                }
            } else {
                //每个元素在一个单元格内,单元格内的数据分隔符为sep，意味着最好只嵌套一层，如果数据定义为多态，只能用这种形式
                for (String value : values) {
                    if (value.equals(XlsxDataSource.EMPTY_STR)) {
                        continue;
                    }
                    dataList.getValues().add(valueType.convert(CollUtil.newArrayList(value), sep));
                }
            }
        }
        return dataList;
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        var dataList = new IDataList();
        for (JsonElement temp : jsonElement.getAsJsonArray()) {
            dataList.getValues().add(valueType.convert(temp));
        }
        return dataList;
    }

    @Override
    public IData convert(Node node) {
        TitledPane titledPane = (TitledPane) node;
        GridPane gridPane = (GridPane) titledPane.getContent();
        ArrayList<Node> values = (ArrayList<Node>) gridPane.getProperties().get("list");
        IDataList iDataList = new IDataList();
        if (values != null) {
            for (Node value : values) {
                iDataList.getValues().add(valueType.convert(value));
            }
        }
        return iDataList;
    }
}
