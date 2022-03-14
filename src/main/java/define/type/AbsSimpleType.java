package define.type;

import java.util.function.Consumer;

/**
 * 简单类型
 *
 * <p>
 * create by xiongjieqing on 2021/9/7 17:37
 */
public abstract class AbsSimpleType implements IType {

    @Override
    public void addExtensionType(Consumer<IType> consumer) {

    }
}
