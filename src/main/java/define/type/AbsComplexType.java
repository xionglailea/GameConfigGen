package define.type;

/**
 * 复杂类型
 *
 * <p>
 * create by xiongjieqing on 2021/9/7 17:37
 */
public abstract class AbsComplexType implements IType {

    @Override
    public String getJavaBoxType() {
        return getJavaType();
    }

    @Override
    public String getConstValue(String origin) {
        throw new RuntimeException("unsupported operate!");
    }


    @Override
    public boolean canBeIndex() {
        return false;
    }

    @Override
    public boolean simpleType() {
        return false;
    }
}
