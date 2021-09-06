package define.type;

import com.google.gson.JsonElement;
import define.EnumDefine;
import define.data.type.IData;
import define.data.type.IDataEnum;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import lombok.Getter;


/**
 * 枚举类型
 * <p>
 * create by xiongjieqing on 2020/8/28 17:03
 */
public class IEnum implements IType {

    @Getter
    private EnumDefine enumDefine;

    public IEnum(EnumDefine enumDefine) {
        this.enumDefine = enumDefine;
    }

    @Override
    public String getTypeName() {
        return "enum";
    }

    @Override
    public String getCsType() {
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
    public IData convert(List<String> values, String sep) {
        String value = values.remove(0);
        int intValue = enumDefine.getEnumValue(value);
        return new IDataEnum(enumDefine, value, intValue);
    }

    @Override
    public IData convert(JsonElement jsonElement) {
        String value = jsonElement.getAsString();
        int intValue = enumDefine.getEnumValue(value);
        return new IDataEnum(enumDefine, value, intValue);
    }

    @Override
    public IData convert(Node node) {
        TextField textField = (TextField) node;
        String content = textField.getText().trim();
        if (content.isEmpty()) {
            throw new RuntimeException(String.format("枚举类型 %s 不能为空", enumDefine.getName()));
        }
        return new IDataEnum(enumDefine, content);
    }

    @Override
    public String getUnmarshalMethodName() {
        throw new RuntimeException("unsupport method");
    }


    @Override
    public String toString() {
        return "enum";
    }
}
