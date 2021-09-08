package define.type;


import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataFloat;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * 浮点数
 * <p>
 * create by xiongjieqing on 2020/7/26 17:29
 */
public class IFloat extends AbsSimpleType {

    public static IDataFloat defaultFloat = new IDataFloat(0);

    @Override
    public String getTypeName() {
        return "float";
    }

    @Override
    public String getJavaType() {
        return "float";
    }

    @Override
    public String getJavaBoxType() {
        return "Float";
    }

    @Override
    public String getCsType() {
        return "float";
    }

    @Override
    public String getConstValue(String origin) {
        return origin + "f";
    }

    @Override
    public IData convert(List<String> values, String sep) {
        String value = values.remove(0);
        return value.equals(XlsxDataSource.EMPTY_STR) || value.equals(XlsxDataSource.NULL_STR) ? defaultFloat : new IDataFloat(Float.parseFloat(value));
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        return new IDataFloat(jsonElement.getAsFloat());
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
            return defaultFloat;
        }
        return new IDataFloat(Float.parseFloat(content));
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
        return "float";
    }
}
