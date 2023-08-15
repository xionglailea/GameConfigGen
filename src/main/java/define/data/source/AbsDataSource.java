package define.data.source;

import define.data.type.IData;
import define.type.IBean;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


/**
 * 数据源
 * <p>
 * create by xiongjieqing on 2020-08-02 16:53
 */
@Getter
@Setter
public abstract class AbsDataSource {

    private IBean dataType;
    private List<IData> data;
    private boolean multi = true;

    public AbsDataSource(IBean dataType) {
        this.dataType = dataType;
    }

    public abstract void load() throws Exception;

    public abstract void close();

    protected boolean isDynamic() {
        return dataType.getBeanDefine().isDynamic();
    }

}
