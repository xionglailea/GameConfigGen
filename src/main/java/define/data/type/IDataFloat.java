package define.data.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import datastream.Octets;
import lombok.Getter;


/**
 * create by xiongjieqing on 2020/8/5 16:00
 */
@Getter
public class IDataFloat extends IData {

    private float value;

    public IDataFloat(float value) {
        this.value = value;
    }

    @Override
    public void export(Octets os) {
        os.writeFloat(value);
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
        return Float.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IDataFloat)) {
            return false;
        }
        return Float.compare(value, ((IDataFloat) obj).value) == 0;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }
}
