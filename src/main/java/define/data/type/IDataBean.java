package define.data.type;

import datastream.Octets;
import define.BeanDefine;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * create by xiongjieqing on 2020/8/5 15:59
 */
@Slf4j
public class IDataBean extends IData {

    @Getter
    private BeanDefine define;
    @Getter
    private BeanDefine actual;
    @Getter
    private List<IData> values;

    public IDataBean(BeanDefine define, BeanDefine actual, List<IData> values) {
        this.define = define;
        this.actual = actual;
        this.values = values;
    }

    /**
     * 获取索引对应的数据
     */
    public IData getIndexData(String indexName) {
        var data = getDataByFieldName(indexName);
        if (data != null) {
            return data;
        }
        throw new RuntimeException(String.format("bean:%s index:%s not exists", actual.getFullName(), indexName));

    }

    public IData getDataByFieldName(String name) {
        for (int i = 0; i < values.size(); i++) {
            if (actual.getAllFields().get(i).getName().equals(name)) {
                return values.get(i);
            }
        }
        return null;
    }

    @Override
    public void export(Octets os) {
        if (define.isDynamic()) {
            os.writeSize(actual.getId());
        }
        for (var data : values) {
            data.checkAndExport(os);
        }
    }


    /**
     * 结构体是入口
     */
    @Override
    public void validate() {
        if (values.isEmpty()) {
            return;
        }
        var fields = actual.getAllFields();
        int index = 0;
        for (var v : values) {
            v.validate();
            v.validateRef(fields.get(index).getRef());
            ++index;
        }
    }

    @Override
    public void validateRef(String ref) {
        if (ref == null) {
            return;
        }
        log.error("{} 是一个结构体, 不能有引用", actual.getName());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(actual.getName()).append("[");
        for (int i = 0; i < values.size(); i++) {
            builder.append(actual.getAllFields().get(i).getName()).append(":").append(values.get(i));
            if (i != values.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
