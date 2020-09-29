package define.column;

import define.type.IType;
import generator.Context;
import lombok.Getter;
import lombok.Setter;


/**
 * create by xiongjieqing on 2020-07-25 21:29
 */
@Getter
@Setter
public abstract class AbsField {

    private IType runType;

    private String group;

    public boolean canExport() {
        return Context.getIns().shouldOutPut(group);
    }

}
