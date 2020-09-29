package generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 生产java代码 create by xiongjieqing on 2020-07-25 10:22
 */
@Slf4j
public class
JavaGenerator {


    public void createConst(String packageName, String javaName, Object data) {
        createJavaFile(packageName, javaName, data, "const");
    }

    public void createEnum(String packageName, String javaName, Object data) {
        createJavaFile(packageName, javaName, data, "enum");
    }

    public void createBean(String packageName, String javaName, Object data) {
        createJavaFile(packageName, javaName, data, "bean");
    }

    public void createCfgMgr(){

    }

    public void createExtensions(String packageName, String javaName, Object data) {
        createJavaFile(packageName, javaName, data, "extensions");
    }

    public void createCfgMgr(String packageName, String javaName, Object data) {
        createJavaFile(packageName, javaName, data, "cfgMgr");
    }


    /**
     * 写java文件
     */
    private void createJavaFile(String packageName, String javaName, Object data, String template) {
        String text = generate( template + ".ftl", data);
        String path = packageName.replace(".", "/") + "/" + javaName + ".java";
        File file = new File(".temp", path);
        FileUtil.writeUtf8String(text, file);
        log.info("write java file :: {}", file);
    }


    /**
     * 根据freemarker模板生成代码
     *
     * @param template 模板文件，相对于template的路径
     * @param data     数据
     * @return string
     */
    @SneakyThrows
    public static String generate(String template, Object data) {
        ClassTemplateLoader loader = new ClassTemplateLoader(ClassLoaderUtil.getClassLoader(),
            "/template/java");
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateLoader(loader);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        StringWriter writer = new StringWriter();
        configuration.getTemplate(template).process(data, writer);
        return writer.getBuffer().toString();
    }

}
