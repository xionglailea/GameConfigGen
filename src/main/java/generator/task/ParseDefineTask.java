package generator.task;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import constdef.StringConst;
import define.*;
import define.column.ConstField;
import define.column.EnumField;
import define.type.TypeUtil;
import generator.Context;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

/**
 * 解析定义
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:04
 */
public class ParseDefineTask extends AbsTask {

    private XmlMapper xmlMapper;

    public ParseDefineTask(Context context) {
        super(context);
        xmlMapper = new XmlMapper();
    }

    @Override
    public void run() throws Exception {
        File cfgFile = new File(StringConst.INPUT_DIR + "/cfg.xml");
        context.setCfgDefine(xmlMapper.readValue(FileUtil.readUtf8String(cfgFile), CfgDefine.class));

        String parentDir = cfgFile.getParent();
        for (String sub : context.getCfgDefine().getIncludes()) {
            File moduleFile = new File(parentDir, sub);
            ModuleDefine moduleDefine = xmlMapper.readValue(FileUtil.readUtf8String(moduleFile), ModuleDefine.class);
            parseModule(moduleDefine);
        }
        context.getAllTypeDefine().setRootPackage(context.getCfgDefine().getRootPackage());
        context.getAllTypeDefine().setExtensionFileName("Extensions");
    }

    private void parseModule(ModuleDefine moduleDefine) {
        context.getModules().add(moduleDefine);
        //枚举
        for (var e : moduleDefine.getEnums()) {
            e.setPackageName(context.getRootPackage() + "." + moduleDefine.getPackageName());
            e.setFullName(e.getPackageName() + "." + e.getName());
            e.setModuleName(moduleDefine.getPackageName());
            parseEnum(e);
            context.getConstAndEnums().put(e.getFullName(), e);
        }
        //常量
        for (var e : moduleDefine.getConsts()) {
            e.setPackageName(context.getRootPackage() + "." + moduleDefine.getPackageName());
            e.setFullName(e.getPackageName() + "." + e.getName());
            e.setModuleName(moduleDefine.getPackageName());
            parseConst(e);
            context.getConstAndEnums().put(e.getFullName(), e);
        }
        //结构体
        for (var e : moduleDefine.getBeans()) {
            e.setPackageName(context.getRootPackage() + "." + moduleDefine.getPackageName());
            e.setModuleName(moduleDefine.getPackageName());
            parseBean(e);
        }
        //配置
        for (var e : moduleDefine.getTables()) {
            e.setPackageName(context.getRootPackage() + "." + moduleDefine.getPackageName());
            e.setModuleName(moduleDefine.getPackageName());
            parseBean(e);
            context.getTables().put(e.getName(), e);
        }
    }

    /**
     * 处理枚举
     */
    private void parseEnum(EnumDefine enumDefine) {
        for (EnumField field : enumDefine.getFields()) {
            field.resolve(enumDefine);
            field.setRunType(
                TypeUtil.create(Collections.singletonList("int"), enumDefine.getPackageName()));
        }
    }

    /**
     * 处理常量
     */
    private void parseConst(ConstDefine constDefine) {
        for (ConstField field : constDefine.getFields()) {
            field.resolve(constDefine);
            field.setRunType(TypeUtil.create(Arrays.asList(field.getType().split("[,;]")),
                constDefine.getPackageName()));
        }
    }

    /**
     * 处理结构体
     */
    private void parseBean(BeanDefine beanDefine) {
        beanDefine.parse();
    }

}
