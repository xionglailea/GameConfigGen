package define.data.type;

import com.google.gson.JsonElement;
import datastream.Octets;
import define.BeanDefine;
import generator.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据接口
 * <p>
 * create by xiongjieqing on 2020/8/5 15:59
 */
@Slf4j
public abstract class IData {

    @Getter
    @Setter
    private boolean canExport = true;

    public void checkAndExport(Octets os) {
        if (canExport) {
            export(os);
        }
    }

    public abstract void export(Octets os);

    /**
     * 数据校验 只有复合结构的会重写
     */
    public void validate() {

    }

    /**
     * 范围校验
     *
     * @param min include
     * @param max include
     */
    public void validateRange(double min, double max) {
    }


    public boolean isDefaultValue() {
        return false;
    }


    //当前正在校验的表格名字
    public static String curValidateTable;

    ///当前正在校验的数据record
    public static IData curValidateData;

    /**
     * 引用检查
     */
    public void validateRef(String name, String ref) {
        if (ref == null) {
            return;
        }
        if (isDefaultValue()) {
            return;
        }
        BeanDefine table = Context.getIns().getTables().get(ref);
        if (!table.getRecordsByIndex().containsKey(this)) {
            if (name != null) {
                log.error("表 {} 中的数据 {},字段 {} = {} 引用 {} 表失败！", curValidateTable, curValidateData, name, this, ref);
            } else {
                log.error("表 {} 中的数据 {},成员 = {} 引用 {} 表失败！", curValidateTable, curValidateData, this, ref);

            }
        }
    }

    protected void printValidateRangeError() {
        log.error("表 {} 中的数据 {} 中的 {} 范围检查失败！", curValidateTable, curValidateData, this);
    }

    public abstract JsonElement save();

}
