package define.visit.ue;

import define.BeanDefine;
import define.column.BeanField;
import define.type.IBean;
import define.type.IList;
import define.type.IMap;
import define.type.IType;

import java.util.HashSet;
import java.util.Set;

public class UeExtUnmarshal {

    public static Set<String> getUeBeanImportInfo(BeanDefine beanDefine) {
        var result = new HashSet<String>();
        for (BeanField field : beanDefine.getFields()) {
            checkUe(beanDefine, field.getRunType(), result);
//            if (field.hasRef()) {
//                var refDefine = Context.getIns().getTables().get(field.getRef());
//                if (!beanDefine.getPackageName().equals(refDefine.getPackageName())) {
//                    result.add(String.format("#include \"cfg/%s/%s.h\"", refDefine.getModuleName(), refDefine.getName()));
//                } else {
//                    result.add(String.format("#include \"%s.h\"", refDefine.getName()));
//                }
//            }

        }
        result.add(String.format("#include \"%s/FOctets.h\"", beanDefine.getRootPkg()));
        if (beanDefine.isHasParent()) {
            var parentName = beanDefine.getParent().getName();
            result.add(String.format("#include \"%s.h\"", parentName));
        }
        return result;
    }

    public static boolean needExtensions(BeanDefine beanDefine) {
        for (BeanField field : beanDefine.getFields()) {
            if (!field.getRunType().simpleType()) {
                return true;
            }
        }
        return false;
    }

    private static void checkUe(BeanDefine beanDefine, IType target, Set<String> result) {
        if (target instanceof IList) {
            checkUe(beanDefine, ((IList) (target)).getValueType(), result);
        } else if (target instanceof IMap) {
            var mapType = (IMap) target;
            checkUe(beanDefine, mapType.getValue(), result);
        } else if (target instanceof IBean) {
            var beanType = (IBean) target;
            if (!beanDefine.getPackageName().equals(beanType.getBeanDefine().getPackageName())) {
                result.add(String.format("#include \"%s/%s/%s.h\"", beanType.getBeanDefine().getRootPkg(), beanType.getBeanDefine().getModuleName(), beanType.getTypeName()));
            } else {
                result.add(String.format("#include \"%s.h\"", beanType.getTypeName()));
            }
        }
    }

    public static UeExtUnmarshal INS = new UeExtUnmarshal();

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
        s.append("auto n = os->ReadSize();\n");
        s.append(String.format("    %s x;\n", listType.getUeType()));
        s.append("    for (int32 i = 0 ; i < n ; i++)\n");
        s.append("    {\n");
        s.append("          x.Add(").append(listType.getValueType().getUeUnmarshal().replace("Extensions::", "")).append(");\n");
        s.append("    }\n");
        s.append("    return x;");
        return s.toString();
    }

    public String accept(IMap mapType) {
        var s = new StringBuilder();
        s.append("auto n = os->ReadSize();\n");
        s.append(String.format("    %s x;\n", mapType.getUeType()));
        s.append("    for (int32 i = 0 ; i < n ; i++)\n");
        s.append("    {\n");
        s.append("        auto key = ").append(mapType.getKey().getUeUnmarshal().replace("Extensions::", "")).append(";\n");
        s.append("        auto value = ").append(mapType.getValue().getUeUnmarshal().replace("Extensions::", "")).append(";\n");
        s.append("        x.Add(key, value);\n");
        s.append("    }\n");
        s.append("    return x;");
        return s.toString();
    }


    public String accept(IBean t) {
        var bean = t.getBeanDefine();

        if (!bean.isDynamic()) {
            return String.format("return new %s(os);", t.getUeType().replace("*", ""));
        }

        var s = new StringBuilder();
        s.append("auto id = os->ReadInt();\n");
        s.append("    ").append(t.getUeType()).append(" x = nullptr;").append("\n");
        s.append("    switch(id)\n");
        s.append("    {\n");
        if (bean.isDynamic()) {
            for (var c : bean.getLeafChildren()) {
                s.append(String.format("        case %d : x = new %s(os); break;\n", c.getId(), new IBean(c).getUeType().replace("*", "")));
            }
        }
        s.append("        default: UE_LOG(LogTemp, Error, TEXT(\"Unexpected id: %d\"), id);\n");
        s.append("    }\n");
        s.append("    return x;");
        return s.toString();
    }

}
