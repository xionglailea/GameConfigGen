package generator.task;

import cn.hutool.core.io.FileUtil;
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

    public ExportDataTask(Context context) {
        super(context);
    }

    @Override
    public void run() {
        for (var table : context.getTables().values()) {
            var data = table.getExportData();
            if (data != null) {
                File file = new File(StringConst.OUTPUT_DATA_DIR + "/" + table.getName().toLowerCase() + ".data");
                FileUtil.writeBytes(data.copyRemainData(), file);
                log.info("导出数据 = {} 成功", file.getName());
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

//            try {
//                if (table.isSingle()) {
//                    Files.writeString(new File(".temp/" + table.getName() + ".json").toPath(), gson.toJson(table.getRecords().get(0)));
//                } else {
//                    for (var recordEntry : table.getRecordsByIndex().entrySet()) {
//                        JsonElement jsonObject = recordEntry.getValue().save();
//                        String text = gson.toJson(jsonObject);
//                        String fileName = table.getName() + "_" + recordEntry.getKey().toString();
//                        Files.writeString(new File(".temp/" + fileName + ".json").toPath(), text);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
