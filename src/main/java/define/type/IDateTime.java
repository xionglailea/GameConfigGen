package define.type;

import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataLong;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.List;

// 解析的时间格式为 yyyy-MM-dd HH:mm:ss 对应的是时间戳的毫秒
public class IDateTime extends ILong {

    @Override
    public String getConstValue(String origin) {
        return convertTime(origin) + "L";
    }

    @Override
    public IData convert(List<String> values, String sep) {
        String value = values.remove(0);
        return value.equals(XlsxDataSource.EMPTY_STR) || value.equals(XlsxDataSource.NULL_STR) ? defaultLong : new IDataLong(convertTime(value));
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        return new IDataLong(convertTime(jsonElement.getAsString()));
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
        return new IDataLong(convertTime(content));
    }

    @Override
    public boolean canBeIndex() {
        return false;
    }

    @SneakyThrows
    public long convertTime(String value) {
        var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var date = formatter.parse(value);
        return date.getTime();
    }
}
