package define;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import define.column.EnumField;
import generator.language.AbsGenerator;

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
    @JacksonXmlProperty(localName = "field")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<EnumField> fields = new ArrayList<>();

    @Override
    public void genCode(AbsGenerator generator) {
        generator.createEnum(getPackageName(), name, this);
    }

    public int getEnumValue(String enumName) {
        for (EnumField field : fields) {
            if (field.getName().equals(enumName) || field.getAlias().equals(enumName)) {
                return field.getValue();
            }
        }
        throw new RuntimeException(String.format("枚举类型 %s 中没有名字或者别称为 %s 的枚举定义", name, enumName));
    }
}
