package define;

import generator.Context;
import generator.JavaGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * 要对应生成java类的配置
 * <p>
 * create by xiongjieqing on 2020-07-25 22:30
 */
@Setter
@Getter
public abstract class AbsClassDefine {

    private String packageName;

    private String fullName;

    private String group;

    public abstract void genCode(JavaGenerator generator);

    public boolean canExport() {
        return Context.getIns().shouldOutPut(group);
    }

}
