package generator.language;

import cn.hutool.core.io.FileUtil;
import constdef.StringConst;
import define.type.IBean;
import generator.Context;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            URL resourceUrl = getClass().getResource("/export/ts/Octets.ts");
            List<String> contents = Files.readAllLines(Paths.get(resourceUrl.toURI()));
            String path = packageName + "/datastream/" + "Octets.ts";
            File file = new File(StringConst.OUTPUT_CODE_DIR, path);
            FileUtil.writeUtf8Lines(contents, file);
            log.info("write ts file :: {}", file);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createFile(String packageName, String javaName, Object data, String template) {
        String text = generate("/template/ts", template + ".ftl", data);
        String path = packageName.replace(".", "/") + "/" + javaName + ".ts";
        File file = new File(StringConst.OUTPUT_CODE_DIR, path);
        FileUtil.writeUtf8String(text, file);
        log.info("write ts file :: {}", file);
    }
}
