import cn.hutool.core.io.FileUtil;
import constdef.StringConst;
import generator.Context;

/**
 * 启动入口
 *
 * <p>
 * create by xiongjieqing on 2020/9/29 15:12
 */
public class Main {

    public static void main(String[] args) throws Exception {

        FileUtil.del(".temp/cfg");
        FileUtil.del(".temp/data");

        Context context = Context.getIns();
        context.parseDefine(StringConst.CSV_CFG);
        context.resolve();
        context.genCode();
        context.loadData(); //检查数据
        context.exportData(); //导出数据

    }
}
