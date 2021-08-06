package define.data;

import define.data.source.AbsDataSource;
import define.data.source.JsonDataSource;
import define.data.source.XlsxDataSource;
import define.type.IType;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


/**
 * 数据解析器
 * <p>
 * create by xiongjieqing on 2020-08-02 16:58
 */
@Slf4j
public class DataCreator {

    public static List<AbsDataSource> getDataSource(IType type, String[] filePaths) {
        var files = new ArrayList<File>();
        for (var path : filePaths) {
            File f = new File(path);
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

    public static AbsDataSource createDataSource(IType type, File file) {
        String fileName = file.getName();
        if (fileName.endsWith("xlsx")) {
            return new XlsxDataSource(file, type);
        } else if (fileName.endsWith("json")) {
            return new JsonDataSource(file, type);
        } else {
            throw new RuntimeException("无法识别的文件" + fileName);
        }
    }

}
