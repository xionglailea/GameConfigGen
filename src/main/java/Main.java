import cn.hutool.core.io.FileUtil;
import constdef.StringConst;
import generator.Context;
import generator.task.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import ui.UiManager;

/**
 * 启动入口
 *
 * <p>
 * create by xiongjieqing on 2020/9/29 15:12
 */
public class Main {


    public static void main(String[] args) throws Exception {

        String lan = "java";

        for (int i = 0; i < args.length; i = i + 2) {
            String option = args[i];
            switch (option) {
                case "-l":
                    lan = args[i + 1];
                    break;
                case "-i":
                    StringConst.INPUT_DIR = args[i + 1];
                    break;
                case "-code":
                    StringConst.OUTPUT_CODE_DIR = args[i + 1];
                    break;
                case "-data":
                    StringConst.OUTPUT_DATA_DIR = args[i + 1];
                    break;
                default:
                    throw new RuntimeException("unknown args " + option);
            }
        }
        if (FileUtil.del(new File(StringConst.OUTPUT_DATA_DIR))) {
            System.out.println("清理生成数据成功");
        }
        if (FileUtil.del(new File(StringConst.OUTPUT_CODE_DIR))) {
            System.out.println("清理生成代码成功");
        }

        Context context = Context.getIns();

        List<AbsTask> tasks = new ArrayList<>();
        tasks.add(new ParseDefineTask(context));
        tasks.add(new PreProcessTask(context));
        tasks.add(new GenCodeTask(context, lan));
        tasks.add(new LoadDataTask(context));
        tasks.add(new ExportDataTask(context));

        for (AbsTask task : tasks) {
            task.run();
        }
        var url = Main.class.getResource("/ui/MainUi.fxml");

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            countDownLatch.countDown();
        });
        //要等待javafx的线程初始化完毕
        countDownLatch.await();
        Platform.runLater(() -> {
            new UiManager(url);

        });

    }
}
