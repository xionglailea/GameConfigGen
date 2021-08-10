package define.type;

import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataList;
import java.util.ArrayList;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;


/**
 * 列表
 * <p>
 * create by xiongjieqing on 2020-07-24 16:38
 */
@Getter
public class IList implements IType {

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
    public String toString() {
        return "list_" + valueType;
    }

    @Override
    public void addExtensionType(Consumer<IType> consumer) {
        consumer.accept(this);
        valueType.addExtensionType(consumer);
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        return new IDataList(dataSource.readListData(valueType, false));
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
