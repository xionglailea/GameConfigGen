package define.type;

import define.EnumDefine;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataEnum;


/**
 * 枚举类型
 * <p>
 * create by xiongjieqing on 2020/8/28 17:03
 */
public class IEnum implements IType {

    private EnumDefine enumDefine;

    public IEnum(EnumDefine enumDefine) {
        this.enumDefine = enumDefine;
    }

    @Override
    public String getTypeName() {
        return "enum";
    }

    @Override
    public String getCsType() {
        return "enum";
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
    public IData convert(XlsxDataSource dataSource) {
        String value = dataSource.getNextNotEmpty();
        int intValue = enumDefine.getEnumValue(value);
        return new IDataEnum(enumDefine, intValue);
    }

    @Override
    public String getUnmarshalMethodName() {
        throw new RuntimeException("unsupport method");
    }


    @Override
    public String toString() {
        return "enum";
    }
}
