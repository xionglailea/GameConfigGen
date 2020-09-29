package define.column;

import define.EnumDefine;
import lombok.Getter;
import lombok.Setter;

/**
 * 枚举的字段定义
 * <p>
 * create by xiongjieqing on 2020-07-24 11:24
 */
@Getter
@Setter
public class EnumField extends AbsField {

    private String name;
    private String alias = "";
    private int value = Integer.MIN_VALUE;

    /**
     * 检查定义的合法性
     */
    public void resolve(EnumDefine host) {
        if (name == null) {
            throw new RuntimeException(String.format("%s 中定义了一个没有名字的枚举类型", host.getName()));
        }
        if (value == Integer.MIN_VALUE) {
            throw new RuntimeException(String.format("%s 中的枚举字段 %s 没有定义值", host.getName(), name));
        }
    }

}
