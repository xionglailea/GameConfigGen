package define.data.source;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import define.type.IBean;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;

/**
 * json data
 * json file sava a single data record
 * 如果是动态类型，使用subType表示具体的子类名称
 * 如果数据类型是map，使用jsonarray序列化，每个array元素中 使用 key value标签来表示map的键值对
 * <p>
 * create by xiongjieqing on 2021/8/6 11:19
 */
public class JsonDataSource extends AbsDataSource {

    public static String curFileName;
    private final File file;

    public JsonDataSource(File file, IBean dataType) {
        super(dataType);
        this.file = file;
    }

    @Override
    public void load() throws Exception {
        JsonObject jsonObject = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        setData(Collections.singletonList(getDataType().convert(jsonObject)));
    }

    public void close() {
    }

}
