package generator.task;

import cn.hutool.core.io.FileUtil;
import constdef.StringConst;
import generator.Context;
import java.io.File;

/**
 * 导出数据任务
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:07
 */
public class ExportDataTask extends AbsTask {

    public ExportDataTask(Context context) {
        super(context);
    }

    @Override
    public void run() {
        for (var table : context.getTables().values()) {
            var data = table.getExportData();
            if (data != null) {
                FileUtil.writeBytes(data.copyRemainData(),
                    new File(StringConst.OUTPUT_DATA_DIR + "/" + table.getName().toLowerCase() + ".data"));
            }
        }
    }
}
