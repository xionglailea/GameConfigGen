package define.data.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import constdef.StringConst;
import datastream.Octets;
import generator.Context;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * create by xiongjieqing on 2020/8/5 16:01
 */
public class IDataString extends IData {

    private String value;
    private boolean l10n;

    public IDataString(String value, boolean l10n) {
        this.value = value;
        this.l10n = l10n;
    }

    @Override
    public void export(Octets os) {
        if (!isDefaultValue() && l10n) {
            value = Context.getIns().getL10n(this);
        }
        os.writeString(value);
    }

    @Override
    public boolean isDefaultValue() {
        return value.equals("null") || value.equals("");
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IDataString)) {
            return false;
        }
        return value.equals(((IDataString) o).value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public JsonElement save() {
        if (!isDefaultValue() && l10n) {
            Context.getIns().getL10n(this);
        }
        return new JsonPrimitive(value);
    }

    @Override
    public void validatePath() {
        Path fullPath = Paths.get(StringConst.VALIDATE_ROOT_DIR, value);
        if (!Files.exists(fullPath)) {
            printValidatePathError();
        }
    }
}
