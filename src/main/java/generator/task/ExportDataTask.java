package generator.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import constdef.StringConst;
import generator.Context;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 导出数据任务
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:07
 */
@Slf4j
public class ExportDataTask extends AbsTask {

    private final boolean exportJson;

    public ExportDataTask(Context context, boolean exportJson) {
        super(context);
        this.exportJson = exportJson;
    }

    @Override
    public void run() {
        log.info("==========开始导出数据==========");
        for (var table : context.getTables().values()) {
            if (!exportJson) {
                var data = table.getExportData();
                if (data != null) {
                    File file = new File(StringConst.OUTPUT_DATA_DIR + "/" + table.getName().toLowerCase() + ".data");
                    FileUtil.writeBytes(data.copyRemainData(), file);
                    log.info("导出数据 = {} 成功", file.getName());
                }
            } else {
                try {
                    if (table.isSingle()) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        Files.writeString(new File(".temp/" + table.getName() + ".json").toPath(), gson.toJson(table.getRecords().get(0)));
                    } else {
                        File file = new File(StringConst.OUTPUT_DATA_DIR + "/" + table.getName().toLowerCase() + ".txt");
                        FileWriter writer = new FileWriter(file);
                        for (var recordEntry : table.getRecordsByIndex().entrySet()) {
                            JsonElement jsonObject = recordEntry.getValue().save();
                            String txt = jsonObject.toString();
                            writer.write(txt, true);
                            writer.write("\r\n", true);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.info("导出数据 = {} 成功", table.getName());
            }
        }
        log.info("==========导出数据结束==========");
    }
}
