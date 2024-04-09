package define.visit.java;


import define.type.*;
import generator.Context;

/**
 * 解析字段调用代码生成
 * <p>
 * create by xiongjieqing on 2020/7/27 11:32
 */
public class Unmarshal {

    public static Unmarshal INS = new Unmarshal();

    protected String getExtension(IType t) {
        return Context.getIns().getRootPackage() + ".Extensions" + "." + t.getUnmarshalMethodName() + "(os)";
    }

    public String accept(IType t) {
        if (t instanceof IInt) {
            return "os.readFixedInt()";
        } else if (t instanceof IBoolean) {
            return "os.readBool()";
        } else if (t instanceof IFloat) {
            return "os.readFloat()";
        } else if (t instanceof ILong) {
            return "os.readFixedLong()";
        } else if (t instanceof IString) {
            return "os.readString()";
        } else if (t instanceof IEnum) {
            return "os.readFixedInt()";
        } else {
            return getExtension(t);
        }
    }

}
