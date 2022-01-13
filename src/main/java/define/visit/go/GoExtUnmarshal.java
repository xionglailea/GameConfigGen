package define.visit.go;

import define.BeanDefine;
import define.column.BeanField;
import define.type.IBean;
import define.type.IList;
import define.type.IMap;
import define.type.IType;
import generator.Context;
import java.util.HashSet;
import java.util.Set;

public class GoExtUnmarshal {

    public static Set<String> getBeanImportInfo(BeanDefine beanDefine) {
        var result = new HashSet<String>();
        for (BeanField field : beanDefine.getFields()) {
            check(beanDefine, field.getRunType(), result);
        }
        return result;
    }

    private static void check(BeanDefine beanDefine, IType target, Set<String> result) {
        if (target instanceof IList) {
            result.add("container/list");
            check(beanDefine, ((IList) (target)).getValueType(), result);
        } else if (target instanceof IMap) {
            var mapType = (IMap) target;
            check(beanDefine, mapType.getValue(), result);
        } else if (target instanceof IBean) {
            var beanType = (IBean) target;
            if (!beanDefine.getPackageName().equals(beanType.getBeanDefine().getPackageName())) {
                result.add(Context.getIns().getRootPackage() + "/" + beanType.getBeanDefine().getPackageName());
            }
        }
    }

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
        s.append("var n = int(os.ReadSize())");
        s.append("  var x = list.New()");
        s.append("  for i:= 0; i < n; i++{\n");
        s.append("      x.PushBack(").append(listType.getValueType().getGoUnmarshal()).append(")\n");
        s.append("  }\n");
        s.append("  return x");
        return s.toString();
    }

    public String accept(IMap mapType) {
        var s = new StringBuilder();
        return s.toString();
    }

}
