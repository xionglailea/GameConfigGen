package define;

import define.type.IType;
import generator.JavaGenerator;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;


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

    public void genCode(JavaGenerator javaGenerator) {
        javaGenerator.createExtensions(rootPackage, extensionFileName, this);
    }

}
