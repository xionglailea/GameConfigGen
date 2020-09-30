package generator.task;

import generator.Context;

/**
 * 加载并校验数据
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:06
 */
public class LoadDataTask extends AbsTask {

    public LoadDataTask(Context context) {
        super(context);
    }

    @Override
    public void run() throws Exception {
        for (var table : context.getTables().values()) {
            table.load();
        }
        //检查数据的正确性
        for (var table : context.getTables().values()) {
            table.checkData();
        }
    }
}
