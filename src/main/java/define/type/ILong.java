package define.type;


import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataInt;
import define.data.type.IDataLong;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * long类型
 * <p>
 * create by xiongjieqing on 2020/7/26 17:33
 */
public class ILong implements IType {

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
    public String getConstValue(String origin) {
        return origin + "L";
    }

    @Override
    public String getUnmarshalMethodName() {
        throw new RuntimeException("unsupport method");
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        return new IDataLong(Long.parseLong(dataSource.getNextNotEmpty()));
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
            return new IDataLong(0L);
        }
        return new IDataLong(Long.parseLong(content));
    }

    @Override
    public boolean canBeIndex() {
        return true;
    }

    @Override
    public boolean canBeMapKey() {
        return true;
    }

    @Override
    public String toString() {
        return "long";
    }
}
