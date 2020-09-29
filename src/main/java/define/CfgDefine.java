package define;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 配置入口
 *
 * create by xiongjieqing on 2020-07-24 11:40
 */

@Getter
@Setter
public class CfgDefine {
    private String name;
    private String rootPackage;
    private String group;
    private List<String> includes = new ArrayList<>();
}
