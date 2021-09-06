package define.data.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import datastream.Octets;

/**
 * <p>
 * create by xiongjieqing on 2021/9/6 17:21
 */
public class IDataBoolean extends IData {

    private boolean value;

    public IDataBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public void export(Octets os) {
        os.writeBool(value);
    }


    @Override
    public boolean isDefaultValue() {
        return !value;
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IDataBoolean)) {
            return false;
        }
        return value == ((IDataBoolean) obj).value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
