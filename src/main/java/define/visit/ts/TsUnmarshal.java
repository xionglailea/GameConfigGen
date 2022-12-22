package define.visit.ts;


import define.type.*;


public class TsUnmarshal {

    public static TsUnmarshal INS = new TsUnmarshal();

    protected String getExtension(IType t) {
        return "extension" + "." + t.getTsUnmarshalMethodName() + "(os)";
    }

    public String accept(IType t) {
        if (t instanceof IInt) {
            return "os.readFixedInt32()";
        } else if (t instanceof IBoolean) {
            return "os.readBool()";
        } else if (t instanceof IFloat) {
            return "os.readFloat32()";
        } else if (t instanceof ILong) {
            return "os.readFixedInt64()";
        } else if (t instanceof IString) {
            return "os.readString()";
        } else if (t instanceof IEnum) {
            return "os.readFixedInt32()";
        } else {
            return getExtension(t);
        }
    }

}
