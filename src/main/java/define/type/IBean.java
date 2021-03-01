package define.type;

import define.BeanDefine;
import define.data.source.XlsxDataSource;
import define.data.type.IData;
import define.data.type.IDataBean;
import generator.Context;
import java.util.ArrayList;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * 结构体
 * <p>
 * create by xiongjieqing on 2020-07-24 16:52
 */
@Getter
@Slf4j
public class IBean implements IType {

    private BeanDefine beanDefine;

    public IBean(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
    }

    @Override
    public String getTypeName() {
        return beanDefine.getName();
    }

    @Override
    public String getJavaType() {
        return beanDefine.getFullName();
    }

    @Override
    public String getCsType() {
        return beanDefine.getFullName();
    }

    @Override
    public String toString() {
        return beanDefine.getFullName().replace(".", "_");
    }

    @Override
    public void addExtensionType(Consumer<IType> consumer) {
        consumer.accept(this);
    }

    @Override
    public IData convert(XlsxDataSource dataSource) {
        var fieldData = new ArrayList<IData>();
        var actualType = beanDefine;
        if (beanDefine.isDynamic()) {
            String subTypeName = dataSource.getNextNotEmpty();
            actualType = Context.getIns().getBean(beanDefine.getPackageName() + "." + subTypeName);
            if (actualType == null) {
                throw new RuntimeException(String.format("抽象类%s的子类%s不存在", beanDefine.getName(), subTypeName));
            }
            if (actualType.isDynamic()) {
                throw new RuntimeException(String.format("抽象类%s的子类%s不能作为数据源配置，", beanDefine.getName(), subTypeName));
            }
        }
        for (var field : actualType.getAllFields()) {
            try {
                IData data = field.getRunType().convert(dataSource);
                data.setCanExport(field.canExport());
                fieldData.add(data);
            } catch (Exception ex) {
                log.error("解析 {} 的字段 {} 数据失败, 当前记录为 {}, 错误详细信息为", actualType.getName(), field.getName(), fieldData);
                ex.printStackTrace();
                //直接退出
                System.exit(0);
            }
        }
        return new IDataBean(beanDefine, actualType, fieldData);
    }
}
