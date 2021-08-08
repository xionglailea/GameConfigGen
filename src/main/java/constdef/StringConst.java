package constdef;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 一些常量临时配置
 * <p>
 * create by xiongjieqing on 2020/8/3 20:12
 */
public class StringConst {

    public static String INPUT_DIR = "cfgdefine";

    public static String OUTPUT_CODE_DIR = ".temp";

    public static String OUTPUT_DATA_DIR = ".temp/data";

    //导出分组
    public static Set<String> ALL_GROUPS = new HashSet<>(Arrays.asList("all", "server", "client", "editor"));

}
