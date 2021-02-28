package define.type;


import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataInt;

/**
 * 整形
 * <p>
 * create by xiongjieqing on 2020-07-24 16:38
 */
public class IInt implements IType {

    @Override
    public String getTypeName() {
        return "int";
    }

    @Override
    public String getJavaType() {
        return "int";
    }

    @Override
    public String getJavaBoxType() {
        return "Integer";
    }

    @Override
    public String getCsType() {
        return "int";
    }

    @Override
    public String getConstValue(String origin) {
        return origin;
    }

    @Override
    public String getUnmarshalMethodName() {
        throw new RuntimeException("unsupport method");
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        return new IDataInt(Integer.parseInt(dataSource.getNextNotEmpty()));
    }

    @Override
    public boolean canBeIndex() {
        return true;
    }

    @Override
    public String toString() {
        return "int";
    }
}
