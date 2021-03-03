package define.visit.cs;

import define.type.IBean;
import define.type.IList;
import define.type.IMap;
import define.type.IType;

/**
 * cs扩展代码
 *
 * <p>
 * create by xiongjieqing on 2021-02-28 23:09
 */
public class CsExtUnmarshal {
    public static CsExtUnmarshal INS = new CsExtUnmarshal();

    public String accept(IType type) {
        if (type instanceof IList) {
            return accept((IList) type);
        } else if (type instanceof IMap) {
            return accept((IMap) type);
        } else if (type instanceof IBean) {
            return accept((IBean) type);
        } else {
            throw new RuntimeException("type " + type.getJavaType() + " should not put in extensions");
        }
    }


    public String accept(IList listType) {
        var s = new StringBuilder();
        s.append("var n = Math.Min(os.ReadSize(), os.Size + 1);\n");
        s.append(String.format("            var x = new %s(n);\n", listType.getCsType()));
        s.append("            for (int i = 0 ; i < n ; i++) {\n");
        s.append("                x.Add(").append(listType.getValueType().getCsUnmarshal()).append(");\n");
        s.append("            }\n");
        s.append("            return x;");
        return s.toString();
    }

    public String accept(IMap mapType) {
        var s = new StringBuilder();
        s.append("var n = Math.Min(os.ReadSize(), os.Size + 1);\n");
        s.append(String.format("            var x = new %s(n);\n", mapType.getCsType()));
        s.append("            for (int i = 0 ; i < n ; i++) {\n");
        s.append("                x.Add(").append(mapType.getKey().getCsUnmarshal()).append(", ")
                .append(mapType.getValue().getCsUnmarshal()).append(");\n");
        s.append("            }\n");
        s.append("            return x;");
        return s.toString();
    }


    public String accept(IBean t) {
        var bean = t.getBeanDefine();

        if (!bean.isDynamic()) {
            return String.format("return new %s(os);", t.getCsType());
        }

        var s = new StringBuilder();
        s.append("var id = os.ReadInt();\n");
        s.append("            ").append(t.getCsType()).append(" x;\n");
        s.append("            switch(id) {\n");
        s.append("                case 0: return null;\n");
        if (bean.isDynamic()) {
            for (var c : bean.getLeafChildren()) {
                s.append(String.format("                case %d : x = new %s(os); break;\n", c.getId(), new IBean(c).getCsType()));
            }
        }
        s.append("                default: throw new Exception(\"unknown bean id:\" + id);\n");
        s.append("                }\n");
        s.append("            return x;");
        return s.toString();
    }
}
