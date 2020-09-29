package define.column;

import define.ConstDefine;
import lombok.Getter;
import lombok.Setter;

/**
 * 常量的字段定义
 * <p>
 * create by xiongjieqing on 2020-07-24 11:23
 */
@Getter
@Setter
public class ConstField extends AbsField {

    private String name;
    private String type;
    private String value;

    /**
     * 检查定义的合法性
     */
    public void resolve(ConstDefine host) {
        if (name == null) {
            throw new RuntimeException(String.format("%s 中定义了一个没有名字的常量类型", host.getName()));
        }
        if (type == null) {
            throw new RuntimeException(String.format("%s 中的常量字段 %s 没有定义类型", host.getName(), name));
        }
        if (value == null) {
            throw new RuntimeException(String.format("%s 中的常量字段 %s 没有定义值", host.getName(), name));
        }
    }

}
