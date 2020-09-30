package define.type;


import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataLong;

/**
 * long类型
 * <p>
 * create by xiongjieqing on 2020/7/26 17:33
 */
public class ILong implements IType {

    @Override
    public String getTypeName() {
        return "long";
    }

    @Override
    public String getJavaType() {
        return "long";
    }

    @Override
    public String getJavaBoxType() {
        return "Long";
    }

    @Override
    public String getConstValue(String origin) {
        return origin + "L";
    }

    @Override
    public String getUnmarshalMethodName() {
        throw new RuntimeException("unsupport method");
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        return new IDataLong(Long.parseLong(dataSource.getNextNotEmpty()));
    }

    @Override
    public boolean canBeIndex() {
        return true;
    }

    @Override
    public String toString() {
        return "long";
    }
}