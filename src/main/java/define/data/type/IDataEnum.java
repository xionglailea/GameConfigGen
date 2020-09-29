package define.data.type;

import datastream.Octets;
import define.EnumDefine;
import lombok.extern.slf4j.Slf4j;


/**
 * create by xiongjieqing on 2020/8/28 17:13
 */
@Slf4j
public class IDataEnum extends IData {

    private EnumDefine enumDefine;
    private int value;

    public IDataEnum(EnumDefine enumDefine, int value) {
        this.enumDefine = enumDefine;
        this.value = value;
    }


    @Override
    public void export(Octets os) {
        os.writeFixedInt(value);
    }

    @Override
    public void validateRef(String ref) {
        if (ref == null) {
            return;
        }
        log.error("{} 是一个枚举, 不能有引用", enumDefine.getName());
    }

}
