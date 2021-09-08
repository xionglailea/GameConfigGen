package define.data.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import datastream.Octets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;


/**
 * create by xiongjieqing on 2020/8/5 16:00
 */
public class IDataList extends IData {

    @Getter
    private List<IData> values;

    public IDataList(List<IData> values) {
        this.values = values;
    }

    public IDataList() {
        values = new ArrayList<>();
    }

    @Override
    public void export(Octets os) {
        os.writeSize(values.size());
        for (var data : values) {
            data.export(os);
        }
    }

    @Override
    public void validate() {
        for (var data : values) {
            data.validate();
        }
    }

    @Override
    public void validateRef(String name, String ref) {
        for (var data : values) {
            data.validateRef(name, ref);
        }
    }

    @Override
    public void validateRange(double min, double max) {
        for (var data : values) {
            data.validateRange(min, max);
        }
    }

    @Override
    public String toString() {
        return "list:[" +
            values.stream().map(Object::toString).collect(Collectors.joining(",")) +
            "]";
    }

    @Override
    public JsonElement save() {
        JsonArray jsonArray = new JsonArray();
        for (IData value : values) {
            jsonArray.add(value.save());
        }
        return jsonArray;
    }
}
