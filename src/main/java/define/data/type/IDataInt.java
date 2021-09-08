package define.data.type;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import datastream.Octets;

/**
 * create by xiongjieqing on 2020/8/5 16:00
 */
public class IDataInt extends IData {

    private int value;

    public IDataInt(int value) {
        this.value = value;
    }

    @Override
    public void export(Octets os) {
        os.writeFixedInt(value);
    }

    @Override
    public void validateRange(double min, double max) {
        if (value < min || value > max) {
            printValidateRangeError();
        }
    }

    @Override
    public boolean isDefaultValue() {
        return value == 0;
    }


    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IDataInt)) {
            return false;
        }
        return value == ((IDataInt) obj).value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }
}
