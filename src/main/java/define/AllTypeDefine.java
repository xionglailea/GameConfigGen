package define;

import define.type.IBean;
import define.type.IType;
import generator.Context;
import generator.language.AbsGenerator;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    public List<String> getAllImport() {
        List<String> result = new ArrayList<>();
        for (ModuleDefine module : Context.getIns().getModules()) {
            result.add(Context.getIns().getRootPackage() + "/" + module.getPackageName());
        }
        return result;
    }

    public String getGoReturnType(IType type) {
        if (!(type instanceof IBean)) {
            return type.getGoType();
        }
        var beanType = (IBean)type;
        var beanDefine = beanType.getBeanDefine();
        if (beanDefine.isDynamic()) {
            return beanDefine.getModuleName() + ".I" + beanDefine.getName();
        } else {
            return type.getGoType();
        }
    }

}
