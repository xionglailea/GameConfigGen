package generator.task;

import generator.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * 加载并校验数据
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:06
 */
@Slf4j
public class LoadDataTask extends AbsTask {

    public LoadDataTask(Context context) {
        super(context);
    }

    @Override
    public void run() throws Exception {
        log.info("==========开始加载并校验数据==========");
        for (var table : context.getTables().values()) {
            table.load();
        }
        //检查数据的正确性
        for (var table : context.getTables().values()) {
            table.checkData();
        }
        log.info("==========加载并校验数据完成==========");
    }
}
