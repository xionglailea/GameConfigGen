package define.visit.ue;

import define.type.*;

public class UeUnmarshal {
    public static UeUnmarshal INS = new UeUnmarshal();

    protected String getExtension(IType t) {
        return "Extensions::" + t.getUeUnmarshalMethodName() + "(os)";
    }

    public String accept(IType t) {
        if (t instanceof IInt) {
            return "os->ReadFixedInt()";
        } else if (t instanceof IBoolean) {
            return "os->ReadBool()";
        } else if (t instanceof IFloat) {
            return "os->ReadFloat()";
        } else if (t instanceof ILong) {
            return "os->ReadFixedInt64()";
        } else if (t instanceof IString) {
            return "os->ReadString()";
        } else if (t instanceof IEnum) {
            return "os->ReadFixedInt()";
        } else {
            return getExtension(t);
        }
    }
}
