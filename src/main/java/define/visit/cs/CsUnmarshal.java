package define.visit.cs;

import define.type.*;

/**
 * cs字段读取
 *
 * <p>
 * create by xiongjieqing on 2021-03-03 22:09
 */
public class CsUnmarshal {

    public static CsUnmarshal INS = new CsUnmarshal();

    protected String getExtension(IType t) {
        return "cfg.Extensions" + "." + t.getCsUnmarshalMethodName() + "(os)";
    }

    public String accept(IType t) {
        if (t instanceof IInt) {
            return "os.ReadFixedInt()";
        } else if (t instanceof IBoolean) {
            return "os.ReadBool()";
        } else if (t instanceof IFloat) {
            return "os.ReadFloat()";
        } else if (t instanceof ILong) {
            return "os.ReadFixedLong()";
        } else if (t instanceof IString) {
            return "os.ReadString()";
        } else if (t instanceof IEnum) {
            return "os.ReadFixedInt()";
        } else {
            return getExtension(t);
        }
    }
}
