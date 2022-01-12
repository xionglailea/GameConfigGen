package define.type;

import cn.hutool.core.collection.CollUtil;
import com.google.gson.JsonElement;
import define.data.source.JsonDataSource;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataMap;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/**
 * hash表
 * <p>
 * create by xiongjieqing on 2020-07-24 16:39
 */
@Getter
@Slf4j
public class IMap extends AbsComplexType {

    private IType key;
    private IType value;

    public IMap(IType key, IType value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getTypeName() {
        return "map";
    }

    @Override
    public String getJavaType() {
        return String.format("java.util.Map<%s, %s>", key.getJavaBoxType(), value.getJavaBoxType());
    }

    @Override
    public String getCsType() {
        return String.format("System.Collections.Generic.Dictionary<%s, %s>", key.getCsType(), value.getCsType());
    }

    @Override
    public String getGoType() {
        return String.format("map[%s]%s", key.getGoType(), value.getGoType());
    }

    @Override
    public String toString() {
        return "map_" + key + "_" + value;
    }

    @Override
    public void addExtensionType(Consumer<IType> consumer) {
        consumer.accept(this);
        key.addExtensionType(consumer);
        value.addExtensionType(consumer);
    }


    public boolean isDynamic() {
        return value instanceof IBean && ((IBean) value).isDynamic();
    }

    //key和value之间使用->分隔
    @Override
    public IData convert(List<String> values, String sep) {
        var dataMap = new HashMap<IData, IData>();
        if (values.size() == 1) {
            //所有数据在一个单元格内
            String firstSep = sep.substring(0, 1);
            String left = null;
            if (sep.length() > 1) {
                left = sep.substring(1);
            }
            String[] keyValuePair = values.get(0).split(replaceRegex(firstSep));
            for (String temp : keyValuePair) {
                putOneCellData(left, dataMap, temp);
            }
        } else {
            //数据存放在不同的单元格内
            if (sep == null) {
                //数据全部展开
                if (isDynamic()) {
                    throw new RuntimeException(String.format("%s 存放的是多态数据类型，需要定义分隔符号，放在一个单元格中", getJavaType()));
                }
                while (!values.isEmpty()) {
                    var tempKey = key.convert(CollUtil.newArrayList(values.remove(0)), null);
                    var tempValue = value.convert(values, null);
                    if (dataMap.put(tempKey, tempValue) != null) {
                        throw new RuntimeException(String.format("%s key %s 重复", getJavaType(), tempKey));
                    }
                }
            } else {
                //每个元素在一个单元格内,单元格内的数据分隔符为sep，意味着最好只嵌套一层，如果数据定义为多态，只能用这种形式
                for (String temp : values) {
                    if (temp.equals(XlsxDataSource.EMPTY_STR)) {
                        continue;
                    }
                    putOneCellData(sep, dataMap, temp);
                }
            }
        }
        return new IDataMap(dataMap);
    }

    private void putOneCellData(String sep, HashMap<IData, IData> dataMap, String temp) {
        String[] entry = temp.split("->");
        var tempKey = key.convert(CollUtil.newArrayList(entry[0]), null);
        var tempValue = value.convert(CollUtil.newArrayList(entry[1]), sep);
        if (dataMap.put(tempKey, tempValue) != null) {
            throw new RuntimeException(String.format("%s key %s 重复", getJavaType(), tempKey));
        }
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        var values = new HashMap<IData, IData>();
        for (JsonElement temp : jsonElement.getAsJsonArray()) {
            var tempObject = temp.getAsJsonObject();
            var keyData = key.convert(tempObject.get("key"));
            var valueData = value.convert(tempObject.get("value"));
            if (values.put(keyData, valueData) != null) {
                throw new RuntimeException(String.format("%s key %s 重复", JsonDataSource.curFileName, keyData));
            }
        }
        return new IDataMap(values);
    }

    @Override
    public IData convert(Node node) {
        TitledPane titledPane = (TitledPane) node;
        GridPane gridPane = (GridPane) titledPane.getContent();
        Map<Node, Node> nodeMap = (Map<Node, Node>) gridPane.getProperties().get("map");
        IDataMap iDataMap = new IDataMap();
        if (nodeMap != null) {
            for (Map.Entry<Node, Node> nodeNodeEntry : nodeMap.entrySet()) {
                var keyNode = nodeNodeEntry.getKey();
                var valueNode = nodeNodeEntry.getValue();
                iDataMap.getValues().put(key.convert(keyNode), value.convert(valueNode));
            }
        }
        return iDataMap;
    }
}
