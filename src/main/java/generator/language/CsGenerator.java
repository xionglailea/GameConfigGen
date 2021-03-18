package generator.language;

import cn.hutool.core.io.FileUtil;
import constdef.StringConst;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 生产cs代码
 *
 * <p>
 * create by xiongjieqing on 2021-02-28 23:32
 */
@Slf4j
public class CsGenerator extends AbsGenerator {
    @Override
    public void createFile(String packageName, String javaName, Object data, String template) {
        String text = generate("/template/cs",template + ".ftl", data);
        String path = packageName.replace(".", "/") + "/" + javaName + ".cs";
        File file = new File(StringConst.OUTPUT_CODE_DIR, path);
        FileUtil.writeUtf8String(text, file);
        log.info("write cs file :: {}", file);
    }
}
