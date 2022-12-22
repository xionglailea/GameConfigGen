package define.type;

import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataBoolean;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * bool值类型
 *
 * <p>
 * create by xiongjieqing on 2021/9/6 17:14
 */
public class IBoolean extends AbsSimpleType {

    public static IDataBoolean defaultBool = new IDataBoolean(false);

    @Override
    public String getTypeName() {
        return "bool";
    }

    @Override
    public String getJavaType() {
        return "boolean";
    }

    @Override
    public String getJavaBoxType() {
        return "Boolean";
    }

    @Override
    public String getCsType() {
        return "bool";
    }

    @Override
    public String getGoType() {
        return "bool";
    }

    @Override
    public String getTsType() {
        return "boolean";
    }

    @Override
    public String getConstValue(String origin) {
        return origin.toLowerCase();
    }

    @Override
    public IData convert(List<String> values, String sep) {
        String value = values.remove(0);
        return value.equals(XlsxDataSource.EMPTY_STR) || value.equals(XlsxDataSource.NULL_STR) ? defaultBool : new IDataBoolean(Boolean.parseBoolean(value));

    }

    @Override
    public IData convert(JsonElement jsonElement) {
        return new IDataBoolean(jsonElement.getAsBoolean());
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
            return defaultBool;
        }
        return new IDataBoolean(Boolean.parseBoolean(content));
    }

    @Override
    public boolean canBeIndex() {
        return false;
    }

    @Override
    public boolean simpleType() {
        return true;
    }

    @Override
    public String toString() {
        return "bool";
    }
}
