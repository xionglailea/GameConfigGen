package generator.language;

import cn.hutool.core.io.FileUtil;
import constdef.StringConst;
import define.type.IBean;
import generator.Context;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UeGenerator extends AbsGenerator {

    @Override
    public void createBean(String packageName, String javaName, Object data) {
        super.createBean(packageName, javaName, data);
        createFile(packageName, javaName, data, "beancpp");
    }

    @Override
    public void createExtensions(String packageName, String javaName, Object data) {
        Context.getIns().getBeans().values().forEach(e -> {
            if (!e.isDynamic()) {
                Context.getIns().getAllTypeDefine().addType(new IBean(e));
            }
        });
        super.createExtensions(packageName, javaName, data);
        createFile(packageName, javaName, data, "extensionscpp");
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("export/ue/FOctets.h");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> contents = reader.lines().collect(Collectors.toList());
            File file = new File(StringConst.OUTPUT_CODE_DIR, packageName + "/FOctets.h");
            FileUtil.writeUtf8Lines(contents, file);
            log.info("write ue file :: {}", file);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createFile(String packageName, String javaName, Object data, String template) {
        String text = generate("/template/ue", template + ".ftl", data);
        String path = packageName.replace(".", "/") + "/" + javaName + ".h";
        if (template.endsWith("cpp")) {
            path = packageName.replace(".", "/") + "/" + javaName + ".cpp";
        }
        File file = new File(StringConst.OUTPUT_CODE_DIR, path);
        FileUtil.writeUtf8String(text, file);
        log.info("write ue file :: {}", file);
    }
}