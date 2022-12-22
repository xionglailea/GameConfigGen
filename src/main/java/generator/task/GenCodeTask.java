package generator.task;

import generator.Context;
import generator.language.*;

/**
 * 生成代码
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:05
 */
public class GenCodeTask extends AbsTask {

    private final AbsGenerator codeGenerator;


    public GenCodeTask(Context context, String lan) {
        super(context);
        switch (lan) {
            case "java":
                codeGenerator = new JavaGenerator();
                break;
            case "cs":
                codeGenerator = new CsGenerator();
                break;
            case "go":
                codeGenerator = new GoGenerator();
                break;
            case "ts":
                codeGenerator = new TsGenerator();
                break;
            default:
                throw new RuntimeException("unknown lan " + lan);
        }
    }

    @Override
    public void run() {
        //生成常量和枚举代码
        for (var e : context.getConstAndEnums().values()) {
            e.genCode(codeGenerator);
        }
        //生成bean的定义代码
        for (var e : context.getBeans().values()) {
            e.genCode(codeGenerator);
        }
        //生成extension中的代码
        context.getAllTypeDefine().genCode(codeGenerator);
        //生成CfgMgr代码
        codeGenerator.createCfgMgr(context.getRootPackage(), "CfgMgr", context);
    }
}
