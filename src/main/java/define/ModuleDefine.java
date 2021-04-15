package define;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块
 * <p>
 * create by xiongjieqing on 2020/7/23 18:49
 */
@Getter
@Setter
public class ModuleDefine {

    private String packageName;
    @JacksonXmlProperty(localName = "bean")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<BeanDefine> beans = new ArrayList<>();//结构体定义
    @JacksonXmlProperty(localName = "table")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<BeanDefine> tables = new ArrayList<>(); //表格定义
    @JacksonXmlProperty(localName = "enum")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<EnumDefine> enums = new ArrayList<>(); //枚举定义
    @JacksonXmlProperty(localName = "const")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ConstDefine> consts = new ArrayList<>(); //常量定义
}