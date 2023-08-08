package generator.task;

import define.BeanDefine;
import define.column.BeanField;
import define.type.IList;
import define.type.IType;
import define.type.TypeUtil;
import generator.Context;
import java.util.Arrays;
import java.util.Collections;

/**
 * 预处理，检查合法性，处理嵌套结构等
 *
 * <p>
 * create by xiongjieqing on 2020/9/30 16:05
 */
public class PreProcessTask extends AbsTask {

    public PreProcessTask(Context context) {
        super(context);
    }

    @Override
    public void run() {
        //解析bean的字段
        for (var e : context.getBeans().values()) {
            parseBeanFields(e);
            e.collectAllFields();
        }

        //将读取数据的配置加到extension接口中
        for (BeanDefine tableBean : context.getTables().values()) {
            //本身做为bean添加到结构中
            IType type = TypeUtil
                .create(Collections.singletonList(tableBean.getName()), tableBean.getPackageName());
            if (tableBean.isSingle()) {
                type.addExtensionType(i -> {
                    if (tableBean.canExport()) {
                        //只有定义导出的数据才导出
                        context.getAllTypeDefine().addType(i);
                    }
                });
                tableBean.setReadFileType(type);
            } else {
                IList listType = new IList(type);
                listType.addExtensionType(i -> {
                    if (tableBean.canExport()) {
                        context.getAllTypeDefine().addType(i);
                    }
                });
                tableBean.setReadFileType(listType);
            }
            //解析索引
            tableBean.resolveIndex();
        }
    }

    /**
     * 检查字段的合法性，处理字段的嵌套结构
     */
    private void parseBeanFields(BeanDefine beanDefine) {
        for (BeanField field : beanDefine.getFields()) {
            field.setRunType(TypeUtil
                .create(Arrays.asList(field.getType().split("[,;]")), beanDefine.getPackageName()));
            field.getRunType().addExtensionType(type -> context.getAllTypeDefine().addType(type));
            field.resolve(beanDefine);
        }
    }
}
