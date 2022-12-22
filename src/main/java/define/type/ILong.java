package define.type;


import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataLong;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * long类型
 * <p>
 * create by xiongjieqing on 2020/7/26 17:33
 */
public class ILong extends AbsSimpleType {

    public static IDataLong defaultLong = new IDataLong(0L);

    @Override
    public String getTypeName() {
        return "long";
    }

    @Override
    public String getJavaType() {
        return "long";
    }

    @Override
    public String getJavaBoxType() {
        return "Long";
    }

    @Override
    public String getCsType() {
        return "long";
    }

    @Override
    public String getGoType() {
        return "int64";
    }

    @Override
    public String getTsType() {
        return "number";
    }

    @Override
    public String getConstValue(String origin) {
        return origin + "L";
    }

    @Override
    public IData convert(List<String> values, String sep) {
        String value = values.remove(0);
        return value.equals(XlsxDataSource.EMPTY_STR) || value.equals(XlsxDataSource.NULL_STR) ? defaultLong : new IDataLong(Long.parseLong(value));
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        return new IDataLong(jsonElement.getAsLong());
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
            return defaultLong;
        }
        return new IDataLong(Long.parseLong(content));
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
        return "long";
    }
}
