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
public class GoGenerator extends AbsGenerator {

    @Override
    public void createExtensions(String packageName, String javaName, Object data) {
        Context.getIns().getBeans().values().forEach(e -> {
            if (!e.isDynamic()) {
                Context.getIns().getAllTypeDefine().addType(new IBean(e));
            }
        });
        createFile(packageName, "extensions_" + getFileName(javaName), data, "extensions");
        // datastream拷贝
        try {
            List<String> contents = Files.readAllLines(Path.of("./export/go/octets.go"));
            contents.set(0, "package " + packageName);
            String path = getGoFilePath(packageName, "datastream");
            File file = new File(StringConst.OUTPUT_CODE_DIR, path);
            FileUtil.writeUtf8Lines(contents, file);
            log.info("write go file :: {}", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getFileName(String javaName) {
        return javaName.substring(0, 1).toLowerCase() + javaName.substring(1);
    }

    @Override
    public void createFile(String packageName, String javaName, Object data, String template) {
        String text = generate("/template/go", template + ".ftl", data);
        String path = getGoFilePath(packageName, javaName);
        File file = new File(StringConst.OUTPUT_CODE_DIR, path);
        FileUtil.writeUtf8String(text, file);
        log.info("write go file :: {}", file);
    }

    private String getGoFilePath(String packageName, String javaName) {
        int firstPoint = packageName.indexOf(".");
        String path = "";
        if (firstPoint > 0) {
            path = packageName.substring(0, firstPoint) + "/" + packageName.substring(firstPoint + 1).replace(".", "_") + "_" + javaName + ".go";
        } else {
            path = packageName + "/" + javaName + ".go";
        }
        return path;
    }
}
