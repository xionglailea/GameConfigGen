package define.type;

import com.google.gson.JsonElement;
import define.data.source.JsonDataSource;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * hash表
 * <p>
 * create by xiongjieqing on 2020-07-24 16:39
 */
@Getter
@Slf4j
public class IMap implements IType {

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
    public String toString() {
        return "map_" + key + "_" + value;
    }

    @Override
    public void addExtensionType(Consumer<IType> consumer) {
        consumer.accept(this);
        key.addExtensionType(consumer);
        value.addExtensionType(consumer);
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        var values = new HashMap<IData, IData>();
        dataSource.expectListBegin();
        while (!dataSource.isListEnd()) {
            var tempKey = key.convert(dataSource);
            var tempValue = value.convert(dataSource);
            if (values.put(tempKey, tempValue) != null) {
                throw new RuntimeException(String.format("%s key %s 重复", dataSource.getFile().getName(), tempKey));
            }
        }
        dataSource.expectListEnd();
        return new IDataMap(values);
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
