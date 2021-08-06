package define.data.source;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import define.type.IType;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;

/**
 * json data
 * json file sava a single data record
 * <p>
 * create by xiongjieqing on 2021/8/6 11:19
 */
public class JsonDataSource extends AbsDataSource {

    private File file;

    public static String curFileName;

    public JsonDataSource(File file, IType dataType) {
        super(dataType);
        this.file = file;
    }

    @Override
    public void load() throws Exception {
        JsonObject jsonObject = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        setData(Collections.singletonList(getDataType().convert(jsonObject)));
    }
}
