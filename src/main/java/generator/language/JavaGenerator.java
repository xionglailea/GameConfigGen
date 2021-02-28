package generator.language;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 生产java代码 create by xiongjieqing on 2020-07-25 10:22
 */
@Slf4j
public class JavaGenerator extends AbsGenerator {

    /**
     * 写java文件
     */
    public void createFile(String packageName, String javaName, Object data, String template) {
        String text = generate("/template/java",template + ".ftl", data);
        String path = packageName.replace(".", "/") + "/" + javaName + ".java";
        File file = new File(".temp", path);
        FileUtil.writeUtf8String(text, file);
        log.info("write java file :: {}", file);
    }


}
