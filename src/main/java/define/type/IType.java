package define.type;

import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.visit.cs.CsExtUnmarshal;
import define.visit.java.ExtUnmarshal;
import define.visit.Unmarshal;
import java.util.function.Consumer;


/**
 * 类型
 *
 * create by xiongjieqing on 2020-07-24 16:38
 */
public interface IType {
    String getTypeName();

    String getJavaType();

    default String getJavaBoxType() {
        return getJavaType();
    }

    default String getConstValue(String origin) {
        throw new RuntimeException("unsuport operate!");
    }

    default String getUnmarshal() {
        return Unmarshal.INS.accept(this);
    }

    default String getExtUnmarshal() {
        return ExtUnmarshal.INS.accept(this);
    }

    default String getCsExtUnmarshal() {
        return CsExtUnmarshal.INS.accept(this);
    }

    default String getUnmarshalMethodName() {
        return "unmarshal_" + this.toString();
    }

    //增加扩展类型，就是通过list和map扩展的
    default void addExtensionType(Consumer<IType> consumer) {
    }

    IData convert(XlsxDataSource dataSource);

    default boolean canBeIndex() {
        return false;
    }

    //cs 接口

    String getCsType();

}
