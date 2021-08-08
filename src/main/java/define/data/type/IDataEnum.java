package define.data.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import datastream.Octets;
import define.EnumDefine;
import lombok.extern.slf4j.Slf4j;


/**
 * create by xiongjieqing on 2020/8/28 17:13
 */
@Slf4j
public class IDataEnum extends IData {

    private EnumDefine enumDefine;
    private int value;
    private String enumName;

    public IDataEnum(EnumDefine enumDefine, String name) {
        this.enumDefine = enumDefine;
        this.enumName = name;
        this.value = enumDefine.getEnumValue(name);
    }

    public IDataEnum(EnumDefine enumDefine, String name, int value) {
        this.enumDefine = enumDefine;
        this.value = value;
        this.enumName = name;
    }


    @Override
    public void export(Octets os) {
        os.writeFixedInt(value);
    }

    @Override
    public void validateRef(String ref) {
        if (ref == null) {
            return;
        }
        log.error("{} 是一个枚举, 不能有引用", enumDefine.getName());
    }

    @Override
    public String toString() {
        return value + "-" + enumName;
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(enumName);
    }
}
