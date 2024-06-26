package define.type;

import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataInt;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * 整形
 * <p>
 * create by xiongjieqing on 2020-07-24 16:38
 */
public class IInt extends AbsSimpleType {

    public static IDataInt defaultInt = new IDataInt(0);

    @Override
    public String getTypeName() {
        return "int";
    }

    @Override
    public String getJavaType() {
        return "int";
    }

    @Override
    public String getJavaBoxType() {
        return "Integer";
    }

    @Override
    public String getCsType() {
        return "int";
    }

    @Override
    public String getUeType() {
        return "int32";
    }

    @Override
    public String getGoType() {
        return "int32";
    }

    @Override
    public String getTsType() {
        return "number";
    }

    @Override
    public String getConstValue(String origin) {
        return origin;
    }

    @Override
    public IData convert(List<String> values, String sep) {
        String value = values.remove(0);
        return value.equals(XlsxDataSource.EMPTY_STR) || value.equals(XlsxDataSource.NULL_STR) ? defaultInt : new IDataInt(Integer.parseInt(value));
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        return new IDataInt(jsonElement.getAsInt());
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
        if (content.isEmpty()) {
            return defaultInt;
        }
        return new IDataInt(Integer.parseInt(content));
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
        return "int";
    }
}
