package generator.language;

import cn.hutool.core.io.FileUtil;
import constdef.StringConst;
import lombok.extern.slf4j.Slf4j;
import java.io.File;

@Slf4j
public class GoGenerator extends AbsGenerator {

    @Override
    public void createExtensions(String packageName, String javaName, Object data) {
        createFile(packageName + "/extension", getFileName(javaName), data, "extensions");
    }


    @Override
    public String getFileName(String javaName) {
        return javaName.substring(0, 1).toLowerCase() + javaName.substring(1);
    }

    @Override
    public void createFile(String packageName, String javaName, Object data, String template) {
        String text = generate("/template/go", template + ".ftl", data);
        String path = packageName.replace(".", "/") + "/" + javaName + ".go";
        File file = new File(StringConst.OUTPUT_CODE_DIR, path);
        FileUtil.writeUtf8String(text, file);
        log.info("write go file :: {}", file);
    }
}
