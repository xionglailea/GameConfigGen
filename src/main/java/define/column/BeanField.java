package define.column;

import define.BeanDefine;
import generator.Context;
import lombok.Getter;
import lombok.Setter;


/**
 * 字段定义
 * <p>
 * create by xiongjieqing on 2020/7/23 18:51
 */
@Getter
@Setter
public class BeanField extends AbsField {

    private String name;
    private String type; //原始类型
    private String comment = "";
    private String ref; //索引的config

    /**
     * 检查定义的合法性
     */
    public void resolve(BeanDefine host) {
        if (ref != null && Context.getIns().getTables().get(ref) == null) {
            throw new RuntimeException(String.format("%s 中的字段 %s 定义的引用表 %s 不存在!", host.getName(), name, ref));
        }
        if (name == null) {
            throw new RuntimeException(String.format("%s 中定义了一个没有名字的字段", host.getName()));
        }
        if (type == null) {
            throw new RuntimeException(String.format("%s 中的字段 %s 没有定义类型", host.getName(), name));
        }
    }

    public boolean hasRef() {
        return ref != null;
    }

    public String getRefType() {
        return Context.getIns().getTables().get(ref).getFullName();
    }
}
