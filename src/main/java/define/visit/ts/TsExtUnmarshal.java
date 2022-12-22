package define.visit.ts;


import define.BeanDefine;
import define.column.BeanField;
import define.type.IBean;
import define.type.IList;
import define.type.IMap;
import define.type.IType;

import java.util.HashSet;
import java.util.Set;

/**
 * 扩展类型调用代码
 * <p>
 * create by xiongjieqing on 2022/12/22 11:15
 */
public class TsExtUnmarshal {


    public static Set<String> getTsBeanImportInfo(BeanDefine beanDefine) {
        var result = new HashSet<String>();
        for (BeanField field : beanDefine.getAllFields()) {
            checkTs(beanDefine, field.getRunType(), result);
        }

        result.add("import * as extension from '../extension/Extensions';");
        result.add("import { Octets } from '../datastream/Octets';");
        if (beanDefine.isHasParent()) {
            var parentName = beanDefine.getParent().getName();
            result.add(String.format("import { %s } from './%s';", parentName, parentName));
        }
        return result;
    }

    private static void checkTs(BeanDefine beanDefine, IType target, Set<String> result) {
        if (target instanceof IList) {
            checkTs(beanDefine, ((IList) (target)).getValueType(), result);
        } else if (target instanceof IMap) {
            var mapType = (IMap) target;
            checkTs(beanDefine, mapType.getValue(), result);
        } else if (target instanceof IBean) {
            var beanType = (IBean) target;
            if (!beanDefine.getPackageName().equals(beanType.getBeanDefine().getPackageName())) {
                result.add(String.format("import { %s } from '../%s/%s';", beanType.getTypeName(), beanType.getBeanDefine().getModuleName(), beanType.getTypeName()));
            } else {
                result.add(String.format("import { %s } from './%s';", beanType.getTypeName(), beanType.getTypeName()));
            }
        }
    }

    public static TsExtUnmarshal INS = new TsExtUnmarshal();

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
        s.append("let n = Math.min(os.readSize(), os.remaining() + 1)\n");
        s.append(String.format("    let x: %s = new Array(n)\n", listType.getTsType()));
        s.append("    for (let i = 0 ; i < n ; i++) {\n");
        s.append("        x[i] = ").append(listType.getValueType().getTsUnmarshal().replace("extension.", "")).append("\n");
        s.append("    }\n");
        s.append("    return x");
        return s.toString();
    }

    public String accept(IMap mapType) {
        var s = new StringBuilder();
        s.append("let n = Math.min(os.readSize(), os.remaining() + 1)\n");
        s.append(String.format("    let x = new %s()\n", mapType.getTsType()));
        s.append("    for (let i = 0 ; i < n ; i++) {\n");
        s.append("        x.set(").append(mapType.getKey().getTsUnmarshal().replace("extension.", "")).append(", ")
                .append(mapType.getValue().getTsUnmarshal().replace("extension.", "")).append(")\n");
        s.append("    }\n");
        s.append("    return x");
        return s.toString();
    }


    public String accept(IBean t) {
        var bean = t.getBeanDefine();

        if (!bean.isDynamic()) {
            return String.format("return new %s(os)", t.getTsType());
        }

        var s = new StringBuilder();
        s.append("let id = os.readInt32()\n");
        s.append("    let x: ").append(t.getTsType()).append("\n");
        s.append("    switch(id) {\n");
        if (bean.isDynamic()) {
            for (var c : bean.getLeafChildren()) {
                s.append(String.format("        case %d : x = new %s(os); break\n", c.getId(), new IBean(c).getTsType()));
            }
        }
        s.append("        default: throw new Error(\"unknown bean id:\" + id)\n");
        s.append("    }\n");
        s.append("    return x");
        return s.toString();
    }

}
