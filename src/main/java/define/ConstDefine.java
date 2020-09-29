package define;

import define.column.ConstField;
import generator.JavaGenerator;
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
    private List<ConstField> fields = new ArrayList<>();

    @Override
    public void genCode(JavaGenerator generator) {
        generator.createConst(getPackageName(), name, this);
    }
}
