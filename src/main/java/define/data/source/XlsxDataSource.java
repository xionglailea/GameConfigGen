package define.data.source;

import define.data.type.IData;
import define.type.IBean;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel数据源
 * row1 table comment
 * row2 fieldName
 * row3 field Comment
 * row4 - rowN data
 * <p>
 * create by xiongjieqing on 2020-08-02 16:54
 */
@Slf4j
@Getter
public class XlsxDataSource extends AbsDataSource {

    public static final String DEFAULT_TYPE_FIELD = "_type";
    public static final String EMPTY_STR = "";
    public static final String NULL_STR = "null";
    private static final String LINE_COMMENT = "##";
    private static final String LIST_BEGIN = "{";
    private static final String LIST_END = "}";
    private final File file;
    private final List<SheetDataInfo> sheetDatas = new ArrayList<>();
    private Workbook workbook;
    private int sheetIndex = 0; //表索引
    private int rowIndex = 3; //行索引

    public XlsxDataSource(File file, IBean type) {
        super(type);
        this.file = file;
    }

    @Override
    public void load() throws Exception {
        loadExcelSheets();
        setData(readExcel());
    }

    public String getCellValue(Cell cell) {
        if (cell == null) {
            return EMPTY_STR;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                var temp = cell.getRichStringCellValue().getString().trim();
                return temp.equals(NULL_STR) ? EMPTY_STR : temp;
            case Cell.CELL_TYPE_NUMERIC:
                return convertNum(cell.getNumericCellValue());
            case Cell.CELL_TYPE_BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_BLANK:
                return EMPTY_STR;
            default:
                throw new RuntimeException("unknow cell type: " + cell.getCellType());
        }
    }

    public void loadExcelSheets() throws Exception {
        workbook = WorkbookFactory.create(file);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            SheetDataInfo sheetData = new SheetDataInfo();
            sheetData.sheetName = sheet.getSheetName();
            sheetData.fieldInfo = parseField(sheet);
            sheetDatas.add(sheetData);
        }
    }

    private Map<String, FieldDataRange> parseField(Sheet sheet) {
        var result = new HashMap<String, FieldDataRange>();
        var rowFiled = sheet.getRow(1);
        FieldDataRange lastValue = null;
        for (Cell cell : rowFiled) {
            var fieldName = cell.getStringCellValue().trim();
            if (fieldName.equals(EMPTY_STR)) {
                lastValue.endColumnIndex++;
            } else {
                var temp = new FieldDataRange(fieldName, cell.getColumnIndex(), cell.getColumnIndex());
                lastValue = temp;
                result.put(fieldName, temp);
            }
        }
        return result;
    }

    public List<String> getNext(String fieldName, boolean multiRow) {
        var fieldInfo = sheetDatas.get(sheetIndex).fieldInfo.get(fieldName);
        if (fieldInfo == null) {
            throw new RuntimeException(String.format("%s 中的字段 %s 没有配置数据", getDataType().getBeanDefine().getName(), fieldName));
        }
        var sheet = workbook.getSheetAt(sheetIndex);
        var result = new ArrayList<String>();
        for (int i = fieldInfo.startColumnIndex; i <= fieldInfo.endColumnIndex; i++) {
            var cell = sheet.getRow(rowIndex).getCell(i);
            result.add(getCellValue(cell));
        }

        if (multiRow) {
            int temp = rowIndex + 1;
            while (temp <= sheet.getLastRowNum() && sheet.getRow(temp).getCell(0) == null) {
                for (int i = fieldInfo.startColumnIndex; i <= fieldInfo.endColumnIndex; i++) {
                    var cell = sheet.getRow(temp).getCell(i);
                    result.add(getCellValue(cell));
                }
                temp++;
            }
        }
        return result;
    }


    public boolean findNewRecord() {
        while (true) {
            if (sheetIndex >= sheetDatas.size()) {
                return false;
            }
            var sheet = workbook.getSheetAt(sheetIndex);
            for (int i = rowIndex; i <= sheet.getLastRowNum(); i++) {
                var firstCell = sheet.getRow(i).getCell(0);
                if (firstCell != null) {
                    String cellValue = getCellValue(firstCell);
                    if (!cellValue.equals(EMPTY_STR) && !cellValue.startsWith(LINE_COMMENT)) {
                        rowIndex = i;
                        return true;
                    }
                }
            }
            sheetIndex++;
            rowIndex = 3;
        }

    }

    public String getNextNotEmpty() {
        return null;
    }

    private String convertNum(double value) {
        long lvalue = (long) value;
        if (lvalue == value) {
            return Long.toString(lvalue);
        } else {
            return Double.toString(value);
        }
    }

    public List<IData> readExcel() {
        var values = new ArrayList<IData>();
        while (findNewRecord()) {
            values.add(getDataType().readNewRecord(this));
            rowIndex++;
        }
        return values;
    }


    @AllArgsConstructor
    public static class FieldDataRange {
        public String fieldName;
        public int startColumnIndex;
        public int endColumnIndex;
    }

    public static class SheetDataInfo {
        public String sheetName;
        public Map<String, FieldDataRange> fieldInfo;

    }

}
