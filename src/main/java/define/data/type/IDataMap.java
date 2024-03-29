package define.data.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import datastream.Octets;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * create by xiongjieqing on 2020/8/5 16:01
 */
public class IDataMap extends IData {

    @Getter
    private Map<IData, IData> values;

    public IDataMap(Map<IData, IData> values) {
        this.values = values;
    }

    public IDataMap() {
        values = new HashMap<>();
    }

    @Override
    public void export(Octets os) {
        os.writeSize(values.size());
        for (var e : values.entrySet()) {
            e.getKey().export(os);
            e.getValue().export(os);
        }
    }

    @Override
    public void validate() {
        for (var entry : values.entrySet()) {
            entry.getKey().validate();
            entry.getValue().validate();
        }
    }

    @Override
    public void validateRef(String name, String ref) {
        for (var value : values.values()) {
            value.validateRef(name, ref);
        }
    }

    @Override
    public void validateRange(double min, double max) {
        for (var value : values.values()) {
            value.validateRange(min, max);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("map:[");
        for (var data : values.entrySet()) {
            builder.append(data.getKey());
            builder.append("->");
            builder.append(data.getValue());
            builder.append(" ");
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public JsonElement save() {
        JsonArray jsonArray = new JsonArray();
        for (Map.Entry<IData, IData> entry : values.entrySet()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("key", entry.getKey().save());
            jsonObject.add("value", entry.getValue().save());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
