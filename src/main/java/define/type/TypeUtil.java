package define.type;

import cn.hutool.core.lang.Assert;
import define.BeanDefine;
import define.EnumDefine;
import generator.Context;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


/**
 * 类型工具类
 * <p>
 * create by xiongjieqing on 2020-07-24 16:41
 */
@Slf4j
public class TypeUtil {

    public static IType create(List<String> types, String packageName) {
        int size = types.size();
        Assert.isTrue(size >= 1);
        String type = types.get(0);
        type = type.trim();
        switch (type) {
            case "int":
                Assert.isTrue(size == 1);
                return new IInt();
            case "bool":
                Assert.isTrue(size == 1);
                return new IBoolean();
            case "float":
                Assert.isTrue(size == 1);
                return new IFloat();
            case "long":
                Assert.isTrue(size == 1);
                return new ILong();
            case "string":
                Assert.isTrue(size == 1);
                return new IString();
            case "list":
                Assert.isTrue(size >= 2);
                return new IList(create(types.subList(1, size), packageName));
            case "map":
                Assert.isTrue(size >= 3);
                IMap mapType = new IMap(create(Collections.singletonList(types.get(1)), packageName),
                    create(types.subList(2, size), packageName));
                if (!mapType.getKey().simpleType()) {
                    throw new RuntimeException(String.format("%s中定义了一个错误的map，%s类型不能做key", packageName, types.get(1)));
                }
                return mapType;
            default:
                //如果是结构体
                Assert.isTrue(size == 1);
                //尝试读取bean的定义和enum的定义
                String rootPackage = Context.getIns().getRootPackage();
                String otherDir = rootPackage + "." + type;
                BeanDefine beanDefine = Context.getIns().getBean(otherDir);
                if (beanDefine != null) {
                    return new IBean(beanDefine);
                }
                EnumDefine enumDefine = Context.getIns().getEnum(otherDir);
                if (enumDefine != null) {
                    return new IEnum(enumDefine);
                }
                String thisDir = packageName + "." + type;
                beanDefine = Context.getIns().getBean(thisDir);
                if (beanDefine != null) {
                    return new IBean(beanDefine);
                }
                enumDefine = Context.getIns().getEnum(thisDir);
                if (enumDefine != null) {
                    return new IEnum(enumDefine);
                }
                throw new RuntimeException("找不到定义类型, type = " + type);
        }
    }
}
