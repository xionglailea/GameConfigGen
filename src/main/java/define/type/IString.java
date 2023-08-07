package define.type;


import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataString;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * string类型
 * <p>
 * create by xiongjieqing on 2020-07-25 10:04
 */
public class IString extends AbsSimpleType {

    public static IDataString defaultString = new IDataString("");

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
    public String getJavaBoxType() {
        return getJavaType();
    }

    @Override
    public String getCsType() {
        return "string";
    }

    @Override
    public String getGoType() {
        return "string";
    }

    @Override
    public String getTsType() {
        return "string";
    }

    @Override
    public IData convert(List<String> values, String sep) {
        String value = values.remove(0);
        return value.equals(XlsxDataSource.EMPTY_STR) || value.equalsIgnoreCase(XlsxDataSource.NULL_STR) ? defaultString : new IDataString(value);
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        return new IDataString(jsonElement.getAsString());
    }


    @Override
    public IData convert(Node node) {
        TextField textField;
        if (node instanceof TextField) {
            textField = (TextField) node;

        } else {
            ComboBox comboBox = (ComboBox) node;
            textField = comboBox.getEditor();
        }
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
    public boolean simpleType() {
        return true;
    }

    @Override
    public String toString() {
        return "string";
    }
}
