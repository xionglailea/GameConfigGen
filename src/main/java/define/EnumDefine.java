package define;

import define.column.EnumField;
import generator.JavaGenerator;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


/**
 * 枚举定义
 * <p>
 * create by xiongjieqing on 2020/7/23 18:49
 */
@Getter
@Setter
public class EnumDefine extends AbsClassDefine{

    private String name;
    private String comment;
    private List<EnumField> fields = new ArrayList<>();

    @Override
    public void genCode(JavaGenerator generator) {
        generator.createEnum(getPackageName(), name, this);
    }

    public int getEnumValue(String value) {
        for (EnumField field : fields) {
            if (field.getName().equals(value) || field.getAlias().equals(value)) {
                return field.getValue();
            }
        }
        throw new RuntimeException(String.format("枚举类型 %s 中没有名字或者别称为 %s 的枚举定义", name, value));
    }
}
