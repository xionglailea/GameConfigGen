package define.type;


import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataFloat;

/**
 * 浮点数
 * <p>
 * create by xiongjieqing on 2020/7/26 17:29
 */
public class IFloat implements IType {

    @Override
    public String getTypeName() {
        return "float";
    }

    @Override
    public String getJavaType() {
        return "float";
    }

    @Override
    public String getJavaBoxType() {
        return "Float";
    }

    @Override
    public String getCsType() {
        return "float";
    }

    @Override
    public String getConstValue(String origin) {
        return origin + "f";
    }

    @Override
    public String getUnmarshalMethodName() {
        throw new RuntimeException("unsupport method");
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        return new IDataFloat(Float.parseFloat(dataSource.getNextNotEmpty()));
    }

    @Override
    public String toString() {
        return "float";
    }
}
