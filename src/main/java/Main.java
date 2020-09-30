import cn.hutool.core.io.FileUtil;
import generator.Context;
import generator.task.AbsTask;
import generator.task.ExportDataTask;
import generator.task.GenCodeTask;
import generator.task.LoadDataTask;
import generator.task.ParseDefineTask;
import generator.task.PreProcessTask;
import java.util.ArrayList;
import java.util.List;

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

        List<AbsTask> tasks = new ArrayList<>();
        tasks.add(new ParseDefineTask(context));
        tasks.add(new PreProcessTask(context));
        tasks.add(new GenCodeTask(context));
        tasks.add(new LoadDataTask(context));
        tasks.add(new ExportDataTask(context));

        for (AbsTask task : tasks) {
            task.run();
        }
    }
}
