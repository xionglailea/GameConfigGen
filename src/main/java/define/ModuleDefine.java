package define;

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
    private List<BeanDefine> beans = new ArrayList<>();//结构体定义
    private List<BeanDefine> tables = new ArrayList<>(); //表格定义
    private List<EnumDefine> enums = new ArrayList<>(); //枚举定义
    private List<ConstDefine> consts = new ArrayList<>(); //常量定义
}