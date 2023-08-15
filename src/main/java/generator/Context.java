package generator;

import constdef.Mode;
import constdef.StringConst;
import define.*;
import define.data.type.IDataBean;
import define.data.type.IDataString;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 上下文
 * <p>
 * create by xiongjieqing on 2020-07-25 10:23
 */
@Getter
@Slf4j
public class Context {

    private static Context ins;

    public static Context getIns() {
        if (ins == null) {
            ins = new Context();
        }
        return ins;
    }

    @Setter
    private Mode mode;

    /**
     * 所有的bean
     */
    private Map<String, BeanDefine> beans = new HashMap<>();

    /**
     * 根配置
     */
    @Setter
    private CfgDefine cfgDefine;

    /**
     * 所有目录模块
     */
    private List<ModuleDefine> modules = new ArrayList<>();

    /**
     * 枚举和常量
     */
    private Map<String, AbsClassDefine> constAndEnums = new HashMap<>();

    /**
     * 出现在定义中的所有类型（非原始类型）
     */
    private AllTypeDefine allTypeDefine = new AllTypeDefine();

    /**
     * 表格配置，有excel配置的
     */
    private Map<String, BeanDefine> tables = new HashMap<>();


    /**
     * 默认所有的都输出
     */
    @Setter
    private String group = "all";

    private Context() {
    }


    public String getRootPackage() {
        return cfgDefine.getRootPackage();
    }

    public void putBean(BeanDefine beanDefine) {
        var old = beans.put(beanDefine.getFullName(), beanDefine);
        if (old != null) {
            throw new RuntimeException("bean define duplicate >>>" + beanDefine.getFullName());
        }
    }

    public BeanDefine getBean(String fullName) {
        return beans.get(fullName);
    }

    public EnumDefine getEnum(String fullName) {
        return (EnumDefine) constAndEnums.get(fullName);
    }

    /**
     * 是否应该导出
     *
     * @param mark 标记
     */
    public boolean shouldOutPut(String mark) {
        if (mark == null || mark.equals("editor") || group.equals("all")) {
            return true;
        }
        return mark.contains(group);
    }

    public boolean canModifyData() {
        return mode == Mode.Editor;
    }

    public Set<String> getEditTable() {
        return tables.entrySet().stream().filter(e -> e.getValue().getGroup() != null && e.getValue().getGroup().contains("editor")).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public String getL10n(IDataString value) {
        var l10nData = tables.get(StringConst.DEFAULT_L10N);
        if (l10nData == null) {
            return value.toString();
        }
        var beanData = (IDataBean) l10nData.getRecordsByIndex().get(value);
        if (beanData == null) {
            log.error("l10n id >>> {} not found", value);
            return value.toString();
        }
        var result = beanData.getDataByFieldName(StringConst.L10N_LAN);
        if (result == null) {
            log.error("l10n id >>> {} target language {} not found", value, StringConst.L10N_LAN);
            return value.toString();
        }
        return result.toString();
    }

}