package define.data.type;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import datastream.Octets;

/**
 * create by xiongjieqing on 2020/8/5 16:01
 */
public class IDataLong extends IData {

    private long value;

    public IDataLong(long value) {
        this.value = value;
    }

    @Override
    public void export(Octets os) {
        os.writeFixedLong(value);
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
        return Long.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IDataLong)) {
            return false;
        }
        return value == ((IDataLong) obj).value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }
}
