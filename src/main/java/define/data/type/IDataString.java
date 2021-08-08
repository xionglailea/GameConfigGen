package define.data.type;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import datastream.Octets;

/**
 * create by xiongjieqing on 2020/8/5 16:01
 */
public class IDataString extends IData {

    private String value;

    public IDataString(String value) {
        this.value = value;
    }

    public IDataString() {
    }

    @Override
    public void export(Octets os) {
        os.writeString(value);
    }

    @Override
    public boolean isDefaultValue() {
        return value.equals("null") || value.equals("");
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IDataString)) {
            return false;
        }
        return value.equals(((IDataString) o).value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }
}
