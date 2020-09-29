package define.visit;


import define.type.IBean;
import define.type.IList;
import define.type.IMap;
import define.type.IType;

/**
 * 扩展类型调用代码
 * <p>
 * create by xiongjieqing on 2020/7/27 11:15
 */
public class ExtUnmarshal {

    public static ExtUnmarshal INS = new ExtUnmarshal();

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
        s.append("var n = Math.min(os.readSize(), os.size() + 1);\n");
        s.append(String.format("        %s x = new java.util.ArrayList<>(n);\n", listType.getJavaType()));
        s.append("        for (int i = 0 ; i < n ; i++) {\n");
        s.append("            x.add(").append(listType.getValueType().getUnmarshal()).append(");\n");
        s.append("        }\n");
        s.append("        return x;");
        return s.toString();
    }

    public String accept(IMap mapType) {
        var s = new StringBuilder();
        s.append("var n = Math.min(os.readSize(), os.size() + 1);\n");
        s.append(String.format("        %s x = new java.util.HashMap<>(n);\n", mapType.getJavaType()));
        s.append("        for (int i = 0 ; i < n ; i++) {\n");
        s.append("            x.put(").append(mapType.getKey().getUnmarshal()).append(", ")
            .append(mapType.getValue().getUnmarshal()).append(");\n");
        s.append("        }\n");
        s.append("        return x;");
        return s.toString();
    }


    public String accept(IBean t) {
        var bean = t.getBeanDefine();

        if (!bean.isDynamic()) {
            return String.format("return new %s(os);", t.getJavaType());
        }

        var s = new StringBuilder();
        s.append("var id = os.readInt();\n");
        s.append("        ").append(t.getJavaType()).append(" x;\n");
        s.append("        switch(id) {\n");
        s.append("            case 0: return null;\n");
        if (bean.isDynamic()) {
            for (var c : bean.getLeafChildren()) {
                s.append(String.format("            case %d : x = new %s(os); break;\n", c.getId(), new IBean(c).getJavaType()));
            }
        }
        s.append("            default: throw new RuntimeException(\"unknown bean id:\" + id);\n");
        s.append("            }\n");
        s.append("        return x;");
        return s.toString();
    }

}
