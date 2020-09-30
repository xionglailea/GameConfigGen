package generator.task;

import generator.Context;

/**
 * 任务
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:03
 */
public abstract class AbsTask {

    protected Context context;

    public AbsTask(Context context) {
        this.context = context;
    }

    public abstract void run() throws Exception;

}
