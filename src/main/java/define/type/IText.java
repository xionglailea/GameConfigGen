package define.type;

import define.data.type.IDataString;

public class IText extends IString {
    @Override
    public boolean canBeIndex() {
        return false;
    }

    @Override
    public IDataString buildData(String value) {
        return new IDataString(value, true);
    }
}
