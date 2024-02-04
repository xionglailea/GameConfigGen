package generator.language;

import cn.hutool.core.io.FileUtil;
import constdef.StringConst;
import define.type.IBean;
import generator.Context;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class TsGenerator extends AbsGenerator {

    @Override
    public void createExtensions(String packageName, String javaName, Object data) {
        Context.getIns().getBeans().values().forEach(e -> {
            if (!e.isDynamic()) {
                Context.getIns().getAllTypeDefine().addType(new IBean(e));
            }
        });
        createFile(packageName + "/extension", getFileName(javaName), data, "extensions");
        // datastream拷贝
        try {
            List<String> contents = Files.readAllLines(Path.of("./export/ts/Octets.ts"));
            String path = packageName + "/datastream/" + "Octets.ts";
            File file = new File(StringConst.OUTPUT_CODE_DIR, path);
            FileUtil.writeUtf8Lines(contents, file);
            log.info("write ts file :: {}", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createFile(String packageName, String javaName, Object data, String template) {
        String text = generate("/template/ts",template + ".ftl", data);
        String path = packageName.replace(".", "/") + "/" + javaName + ".ts";
        File file = new File(StringConst.OUTPUT_CODE_DIR, path);
        FileUtil.writeUtf8String(text, file);
        log.info("write ts file :: {}", file);
    }
}
