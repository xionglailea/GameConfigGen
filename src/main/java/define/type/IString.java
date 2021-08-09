package define.type;


import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataString;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * string类型
 * <p>
 * create by xiongjieqing on 2020-07-25 10:04
 */
public class IString implements IType {

    private String value;

    @Override
    public String getTypeName() {
        return "string";
    }

    @Override
    public String getJavaType() {
        return "String";
    }

    @Override
    public String getCsType() {
        return "string";
    }

    @Override
    public String getUnmarshalMethodName() {
        throw new RuntimeException("unsupport method");
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        var v = dataSource.getNextNotEmpty();
        return new IDataString(v.equals("null") ? "" : v);
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        return new IDataString(jsonElement.getAsString());
    }


    @Override
    public IData convert(Node node) {
        TextField textField = (TextField) node;
        String content = textField.getText();
        return new IDataString(content);
    }

    @Override
    public String getConstValue(String origin) {
        return "\"" + origin + "\"";
    }

    @Override
    public boolean canBeIndex() {
        return true;
    }

    @Override
    public String toString() {
        return "string";
    }
}
