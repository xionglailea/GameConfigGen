package define.visit.go;

import define.BeanDefine;
import define.column.BeanField;
import define.type.IBean;
import define.type.IList;
import define.type.IMap;
import define.type.IType;
import java.util.HashSet;
import java.util.Set;

public class GoExtUnmarshal {

    public static GoExtUnmarshal INS = new GoExtUnmarshal();

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
        s.append("var n = int(os.ReadSize())\n");
        s.append(String.format("    var x = make(%s, 0)\n", listType.getGoType()));
        s.append("    for i:= 0; i < n; i++{\n");
        s.append("        x = append(x, ").append(listType.getValueType().getGoUnmarshal()).append(")\n");
        s.append("    }\n");
        s.append("    return x");
        return s.toString();
    }


    public String accept(IMap mapType) {
        var s = new StringBuilder();
        s.append("var n = int(os.ReadSize())\n");
        s.append(String.format("    var x = make(%s)\n", mapType.getGoType()));
        s.append("    for i := 0; i < n; i++ {\n");
        s.append("        x[").append(mapType.getKey().getGoUnmarshal()).append("] = ")
                .append(mapType.getValue().getGoUnmarshal()).append("\n");
        s.append("    }\n");
        s.append("    return x");
        return s.toString();
    }

    public String accept(IBean t) {
        var bean = t.getBeanDefine();
        if (!bean.isDynamic()) {
            return buildObject(t, bean);
        }
        var s = new StringBuilder();
        s.append("var id = os.ReadInt32()\n");
        s.append("    switch id {\n");
        if (bean.isDynamic()) {
            for (var c : bean.getLeafChildren()) {
                s.append(String.format("        case %d : return %s(os)\n", c.getId(), new IBean(c).getGoUnmarshalMethodName()));
            }
        }
        s.append("        default: panic(\"unknown bean id:\" + string(id))\n");
        s.append("    }");
        return s.toString();
    }


    private String buildObject(IBean beanType, BeanDefine beanDefine) {
        var s = new StringBuilder();
        s.append("return &").append(beanType.getGoType().replace("*", "")).append("{\n");
//        if (beanDefine.isHasParent()) {
//            s.append(buildParent(beanDefine));
//        }
        s.append(buildFields(beanDefine));
        s.append("    }");
        return s.toString();
    }

    private String buildParent(BeanDefine beanDefine) {
        var parent = beanDefine.getParent();
        var s = new StringBuilder();
        s.append(parent.getName()).append(": ").append(parent.getPackageName()).append(".").append(parent.getName()).append("{\n");
        if (parent.isHasParent()) {
            s.append(buildParent(parent));
        }
        s.append(buildFields(parent));
        s.append("},\n");
        return s.toString();
    }

    private String buildFields(BeanDefine beanDefine) {
        var s = new StringBuilder();
        for (BeanField field : beanDefine.getAllFields()) {
            s.append("        ").append(getGoFieldName(field)).append(": ").append(field.getRunType().getGoUnmarshal()).append(",\n");
        }
        return s.toString();
    }

    private String getGoFieldName(BeanField field) {
        return field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }


}
