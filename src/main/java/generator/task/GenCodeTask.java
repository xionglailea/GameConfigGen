package generator.task;

import cn.hutool.core.io.FileUtil;
import generator.Context;
import generator.JavaGenerator;

/**
 * 生成代码
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:05
 */
public class GenCodeTask extends AbsTask{

    private JavaGenerator javaGenerator = new JavaGenerator();


    public GenCodeTask(Context context) {
        super(context);
    }

    @Override
    public void run() {
        FileUtil.del(".temp/" + context.getCfgDefine().getRootPackage());
        //生成常量和枚举代码
        for (var e : context.getConstAndEnums().values()) {
            e.genCode(javaGenerator);
        }
        //生成bean的定义代码
        for (var e : context.getBeans().values()) {
            e.genCode(javaGenerator);
        }
        //生成extension中的代码
        context.getAllTypeDefine().genCode(javaGenerator);
        //生成CfgMgr代码
        javaGenerator.createCfgMgr(context.getRootPackage(), "CfgMgr", context);
    }
}
