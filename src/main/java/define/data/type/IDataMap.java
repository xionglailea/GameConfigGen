package define.data.type;

import datastream.Octets;
import java.util.HashMap;
import java.util.Map;

/**
 * create by xiongjieqing on 2020/8/5 16:01
 */
public class IDataMap extends IData {

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
    public void validateRef(String ref) {
        for (var value : values.values()) {
            value.validateRef(ref);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("map:[");
        for (var data : values.entrySet()) {
            builder.append(data.getKey());
            builder.append(":");
            builder.append(data.getValue());
            builder.append(" ");
        }
        builder.append("]");
        return builder.toString();
    }
}
