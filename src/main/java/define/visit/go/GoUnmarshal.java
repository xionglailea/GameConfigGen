package define.visit.go;

import define.type.*;

public class GoUnmarshal {

    public static GoUnmarshal INS = new GoUnmarshal();

    protected String getExtension(IType t) {
        return "cfg.Extensions" + "." + t.getUnmarshalMethodName() + "(os)";
    }

    public String accept(IType t) {
        if (t instanceof IInt) {
            return "os.ReadFixedInt32";
        } else if (t instanceof IBoolean) {
            return "os.ReadBool()";
        } else if (t instanceof IFloat) {
            return "os.ReadFloat32()";
        } else if (t instanceof ILong) {
            return "os.readFixedInt64()";
        } else if (t instanceof IString) {
            return "os.ReadString()";
        } else if (t instanceof IEnum) {
            return "os.readFixedInt()";
        } else {
            return getExtension(t);
        }
    }

}
