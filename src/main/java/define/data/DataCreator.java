package define.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import define.BeanDefine;
import define.data.source.AbsDataSource;
import define.data.source.JsonDataSource;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataBean;
import define.type.IBean;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


/**
 * 数据解析器
 * <p>
 * create by xiongjieqing on 2020-08-02 16:58
 */
@Slf4j
public class DataCreator {

    public static List<AbsDataSource> getDataSource(IBean type, String[] filePaths) {
        var files = new ArrayList<File>();
        for (var path : filePaths) {
            File f = new File(path);
            if (!f.exists() && !path.contains(".")) {
                f.mkdir();
            }
            if (f.isDirectory()) {
                var sub = f.listFiles();
                if (sub != null) {
                    files.addAll(Arrays.asList(sub));
                }
                //log.warn("path = {} is a directory", path);
            } else {
                files.add(f);
            }
        }
        return files.stream().map(e -> createDataSource(type, e)).collect(Collectors.toList());
    }

    public static AbsDataSource createDataSource(IBean type, File file) {
        String fileName = file.getName();
        if (fileName.endsWith("xlsx")) {
            return new XlsxDataSource(file, type);
        } else if (fileName.endsWith("json")) {
            return new JsonDataSource(file, type);
        } else {
            throw new RuntimeException("无法识别的文件" + fileName);
        }
    }

    //save json file like item_1001.json
    public static void saveData(BeanDefine beanDefine, IData data, String[] filePaths) {
        for (var path : filePaths) {
            File f = new File(path);
            if (f.isDirectory()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement jsonObject = data.save();
                String text = gson.toJson(jsonObject);
                try {
                    IDataBean dataBean = (IDataBean) data;
                    var indexValue = dataBean.getIndexData(beanDefine.getIndexField().getName());
                    Files.writeString(new File(path + "/" + beanDefine.getName() + "_" + indexValue + ".json").toPath(), text);
                    log.info("write new data = {}", data);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new RuntimeException(beanDefine.getName() + " not define data directory!!");
    }


    public static void removeData(BeanDefine beanDefine, IData data, String[] filePaths) {
        for (var path : filePaths) {
            File f = new File(path);
            if (f.isDirectory()) {
                try {
                    IDataBean dataBean = (IDataBean) data;
                    var indexValue = dataBean.getIndexData(beanDefine.getIndexField().getName());
                    var temp = new File(path + "/" + beanDefine.getName() + "_" + indexValue + ".json");
                    if (temp.exists()) {
                        Files.delete(temp.toPath());
                        log.info("delete data = {}", data);

                    }
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
