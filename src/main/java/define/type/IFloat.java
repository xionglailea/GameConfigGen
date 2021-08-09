package define.type;


import com.google.gson.JsonElement;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataFloat;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * 浮点数
 * <p>
 * create by xiongjieqing on 2020/7/26 17:29
 */
public class IFloat implements IType {

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
    public String getUnmarshalMethodName() {
        throw new RuntimeException("unsupport method");
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        return new IDataFloat(Float.parseFloat(dataSource.getNextNotEmpty()));
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        return new IDataFloat(jsonElement.getAsFloat());
    }

    @Override
    public IData convert(Node node) {
        TextField textField = (TextField) node;
        String content = textField.getText();
        return new IDataFloat(Float.parseFloat(content));
    }

    @Override
    public String toString() {
        return "float";
    }
}
