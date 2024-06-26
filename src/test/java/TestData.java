import cfg.CfgMgr;
import com.google.gson.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 测试
 *
 */
public class TestData {

    @Test
    public void testLoadData() {
        cfg.CfgMgr.setDir(".temp/data");
        cfg.CfgMgr.load();
        System.out.println("ok");
//        System.out.println(CfgMgr.ins.getAllTypeTestList().size());
    }

    @Test
    public void testGson() {
        String json = "[\"本级小计\",368.00,328.00,]";
        JsonElement sourceJsonElement = new JsonParser().parse(json);
        JsonArray jsonArray = sourceJsonElement.getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement jsonElement = jsonArray.get(i);
            if (jsonElement.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if (jsonPrimitive.isString()) {
                    System.out.println(jsonPrimitive.getAsString());
                } else if (jsonPrimitive.isNumber()) {
                    System.out.println(jsonPrimitive.getAsDouble());
                }
            }
        }
    }

    @Test
    public void testGson2() throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", 1);
        jsonObject.addProperty("name", "xiong");
        jsonObject.add("test", new JsonPrimitive(2));
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(2);
        jsonArray.add(3);
        jsonArray.add(4);
        jsonObject.add("data", jsonArray);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String text = gson.toJson(jsonObject);
        Files.writeString(new File("temp.json").toPath(), text);

    }

    @Test
    public void testReadExcel() throws Exception {
        Workbook workbook = WorkbookFactory.create(new File("test.xlsx"));
        var sheet = workbook.getSheetAt(0);
        System.out.println(sheet.getLastRowNum());
        var row = sheet.getRow(1);
        for (Cell cell : row) {
            System.out.println(cell.getColumnIndex() + " " + cell.getStringCellValue());
            System.out.println(cell.getStringCellValue().equals(""));
        }

    }

    @Test
    public void testString() {
        String a = "a,d|b|c";
        for (String s : a.split("\\|")) {
            System.out.println(s);
        }
        String b = ",a";
        String c = "a,";
        var temp1 = b.split(",");
        var temp2 = c.split(",");
        System.out.println(temp1.length);
        System.out.println(temp2.length);
    }

}
