package generator.language;

import cn.hutool.core.util.ClassLoaderUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * 共有方法
 *
 * <p>
 * create by xiongjieqing on 2021-02-28 23:32
 */
public abstract class AbsGenerator {

    public void createConst(String packageName, String javaName, Object data) {
        createFile(packageName, getFileName(javaName), data, "const");
    }

    public void createEnum(String packageName, String javaName, Object data) {
        createFile(packageName, getFileName(javaName), data, "enum");
    }

    public void createBean(String packageName, String javaName, Object data) {
        createFile(packageName, getFileName(javaName), data, "bean");
    }

    public void createExtensions(String packageName, String javaName, Object data) {
        createFile(packageName, getFileName(javaName), data, "extensions");
    }

    public void createCfgMgr(String packageName, String javaName, Object data) {
        createFile(packageName, getFileName(javaName), data, "cfgMgr");
    }


    /**
     * 写java文件
     */
    public abstract void createFile(String packageName, String javaName, Object data, String template);

    public String getFileName(String javaName) {
        return javaName;
    }

    /**
     * 根据freemarker模板生成代码
     *
     * @param template 模板文件，相对于template的路径
     * @param data     数据
     * @return string
     */
    @SneakyThrows
    public static String generate(String resourcePath, String template, Object data) {
        ClassTemplateLoader loader = new ClassTemplateLoader(ClassLoaderUtil.getClassLoader(), resourcePath);
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateLoader(loader);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        StringWriter writer = new StringWriter();
        configuration.getTemplate(template).process(data, writer);
        return writer.getBuffer().toString();
    }


}
