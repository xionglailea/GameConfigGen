package define.data.type;

import datastream.Octets;
import java.util.ArrayList;
import java.util.List;


/**
 * create by xiongjieqing on 2020/8/5 16:00
 */
public class IDataList extends IData {

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
    public void validateRef(String ref) {
        for (var data : values) {
            data.validateRef(ref);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("list:[");
        for (var data : values) {
            builder.append(data);
            builder.append(" ");
        }
        builder.append("]");
        return builder.toString();
    }
}