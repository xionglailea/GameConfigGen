package define;

import define.type.IBean;
import define.type.IType;
import generator.Context;
import generator.language.AbsGenerator;
import lombok.Getter;
import lombok.Setter;

import java.util.*;


/**
 * 所有的类型定义
 * <p>
 * create by xiongjieqing on 2020/7/28 20:12
 */
@Getter
@Setter
public class AllTypeDefine {

    private String rootPackage;
    private String extensionFileName;
    private Map<String, IType> name2Type = new HashMap<>();


    public void addType(IType type) {
        if (name2Type.containsKey(type.toString())) {
            return;
        }
        name2Type.put(type.toString(), type);
    }

    public void genCode(AbsGenerator codeGenerator) {
        codeGenerator.createExtensions(rootPackage, extensionFileName, this);
    }

    public List<String> getGoAllImport() {
        List<String> result = new ArrayList<>();
        for (ModuleDefine module : Context.getIns().getModules()) {
            result.add(Context.getIns().getRootPackage() + "/" + module.getPackageName());
        }
        return result;
    }

    public Set<String> getTsAllImport() {
        var result = new HashSet<String>();
        for (IType iType : name2Type.values()) {
            if (!(iType instanceof IBean)) {
                continue;
            }
            var beanType = (IBean) iType;
            result.add(String.format("import { %s } from '../%s/%s';", beanType.getTypeName(), beanType.getBeanDefine().getModuleName(), beanType.getTypeName()));
        }
        result.add("import { Octets } from '../datastream/Octets';");
        return result;
    }

    public String getUeForwardDefine() {
        var result = new HashMap<String, Set<String>>();
        for (IType iType : name2Type.values()) {
            if (!(iType instanceof IBean)) {
                continue;
            }
            var beanType = (IBean) iType;
            var moduleName = beanType.getBeanDefine().getModuleName();
            if (!result.containsKey(moduleName)) {
                result.put(moduleName, new HashSet<>());
            }
            result.get(moduleName).add(beanType.getTypeName());
        }
        var sb = new StringBuilder();
        for (var entry : result.entrySet()) {
            sb.append("    namespace ").append(entry.getKey()).append("\n");
            sb.append("    {\n");
            for (var typeName : entry.getValue()) {
                sb.append("        class ").append(typeName).append(";\n");
            }
            sb.append("    }\n");
        }
        return sb.toString();
    }

    public Set<String> getUeExtensionInclude() {
        var result = new HashSet<String>();
        for (IType iType : name2Type.values()) {
            if (!(iType instanceof IBean)) {
                continue;
            }
            var beanType = (IBean)iType;
            result.add(String.format("#include \"%s/%s.h\"", beanType.getBeanDefine().getModuleName(), beanType.getTypeName()));
        }
        return result;
    }

    public String getGoReturnType(IType type) {
        if (!(type instanceof IBean)) {
            return type.getGoType();
        }
        var beanType = (IBean) type;
        var beanDefine = beanType.getBeanDefine();
        if (beanDefine.isDynamic()) {
            return "I" + beanDefine.getName();
        } else {
            return type.getGoType();
        }
    }

}
