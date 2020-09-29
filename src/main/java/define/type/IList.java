package define.type;

import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataList;
import java.util.function.Consumer;
import lombok.Getter;


/**
 * 列表
 * <p>
 * create by xiongjieqing on 2020-07-24 16:38
 */
@Getter
public class IList implements IType {

    private IType valueType;

    public IList(IType valueType) {
        this.valueType = valueType;
    }

    @Override
    public String getTypeName() {
        return "list";
    }

    @Override
    public String getJavaType() {
        return String.format("java.util.List<%s>", valueType.getJavaBoxType());
    }

    @Override
    public String toString() {
        return "list_" + valueType;
    }

    @Override
    public void addExtensionType(Consumer<IType> consumer) {
        consumer.accept(this);
        valueType.addExtensionType(consumer);
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        return new IDataList(dataSource.readListData(valueType, false));
    }
}
