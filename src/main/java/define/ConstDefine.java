package define;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import define.column.ConstField;
import generator.language.AbsGenerator;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


/**
 * 常量定义
 * <p>
 * create by xiongjieqing on 2020/7/23 19:42
 */
@Getter
@Setter
public class ConstDefine extends AbsClassDefine {

    private String name;
    private String comment;
    @JacksonXmlProperty(localName = "field")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ConstField> fields = new ArrayList<>();

    @Override
    public void genCode(AbsGenerator generator) {
        generator.createConst(getPackageName(), name, this);
    }
}
