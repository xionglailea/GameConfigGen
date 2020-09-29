package define.type;


import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataString;

/**
 * string类型
 * <p>
 * create by xiongjieqing on 2020-07-25 10:04
 */
public class IString implements IType {

    private String value;

    @Override
    public String getTypeName() {
        return "string";
    }

    @Override
    public String getJavaType() {
        return "String";
    }

    @Override
    public String getUnmarshalMethodName() {
        throw new RuntimeException("unsupport method");
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        var v = dataSource.getNextNotEmpty();
        return new IDataString(v.equals("null") ? "" : v);

    }

    @Override
    public String getConstValue(String origin) {
        return "\"" + origin + "\"";
    }

    @Override
    public boolean canBeIndex() {
        return true;
    }

    @Override
    public String toString() {
        return "string";
    }
}
