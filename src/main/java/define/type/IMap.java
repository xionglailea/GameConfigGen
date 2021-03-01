package define.type;

import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataMap;
import java.util.HashMap;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * hash表
 * <p>
 * create by xiongjieqing on 2020-07-24 16:39
 */
@Getter
@Slf4j
public class IMap implements IType {

    private IType key;
    private IType value;

    public IMap(IType key, IType value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getTypeName() {
        return "map";
    }

    @Override
    public String getJavaType() {
        return String.format("java.util.Map<%s, %s>", key.getJavaBoxType(), value.getJavaBoxType());
    }

    @Override
    public String getCsType() {
        return String.format("System.Collections.Generic.Dictionary<%s, %s>", key.getCsType(), value.getCsType());
    }

    @Override
    public String toString() {
        return "map_" + key + "_" + value;
    }

    @Override
    public void addExtensionType(Consumer<IType> consumer) {
        consumer.accept(this);
        key.addExtensionType(consumer);
        value.addExtensionType(consumer);
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        var values = new HashMap<IData, IData>();
        dataSource.expectListBegin();
        while (!dataSource.isListEnd()) {
            var tempKey = key.convert(dataSource);
            var tempValue = value.convert(dataSource);
            if (values.put(tempKey, tempValue) != null) {
                throw new RuntimeException(String.format("%s key %s 重复", dataSource.getFile().getName(), tempKey));
            }
        }
        dataSource.expectListEnd();
        return new IDataMap(values);
    }
}
