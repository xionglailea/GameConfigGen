package define.data.source;

import define.data.type.IData;
import define.type.IType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


/**
 * excel数据源
 * <p>
 * create by xiongjieqing on 2020-08-02 16:54
 */
@Slf4j
@Getter
public class XlsxDataSource extends AbsDataSource {


    public static class SheetData {

        public String sheetName;
        public List<List<String>> rows = new ArrayList<>();
    }

    private static String LINE_COMMENT = "##";
    private static String LIST_BEGIN = "{";
    private static String LIST_END = "}";
    private File file;
    private List<SheetData> sheetDatas = new ArrayList<>();
    private int sheetIndex; //表索引
    private int rowIndex; //行索引
    private int columnIndex; //列索引


    public XlsxDataSource(File file, IType type) {
        super(type);
        this.file = file;
    }

    @Override
    public void load() throws Exception {
        loadExcelSheets();
        setData(readListData(getDataType(), true));
    }

    public void loadExcelSheets() throws Exception {
        Workbook workbook = WorkbookFactory.create(file);
        //        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            SheetData sheetData = new SheetData();
            sheetData.sheetName = sheet.getSheetName();
            sheetDatas.add(sheetData);
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                sheetData.rows.add(rowData);
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            rowData.add(cell.getRichStringCellValue().getString());
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            rowData.add(convertNum(cell.getNumericCellValue()));
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            rowData.add(Boolean.toString(cell.getBooleanCellValue()));
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            rowData.add("");
                            break;
                        default:
                            throw new RuntimeException("unknow cell type: " + cell.getCellType());
                    }
                }
            }
        }
    }

    public String getNext() {
        while (true) {
            if (sheetIndex >= sheetDatas.size()) {
                return null;
            }
            var rows = sheetDatas.get(sheetIndex).rows;
            if (rowIndex >= rows.size()) {
                ++sheetIndex;
                rowIndex = 0;
                continue;
            }
            var row = rows.get(rowIndex);
            if (columnIndex >= row.size()) {
                rowIndex++;
                columnIndex = 0;
                continue;
            }
            String data = row.get(columnIndex);
            if (data.startsWith(LINE_COMMENT)) {
                rowIndex++;
                columnIndex = 0;
                continue;
            }
            if (!data.isEmpty()) { //读到了有效数据
                ++columnIndex;
                return data;
            } else {
                ++columnIndex;
            }
        }
    }

    //是否到文件结尾了
    public boolean isEOF() {
        while (true) {
            if (sheetIndex >= sheetDatas.size()) {
                return true;
            }
            var rows = sheetDatas.get(sheetIndex).rows;
            if (rowIndex >= rows.size()) {
                ++sheetIndex;
                rowIndex = 0;
                continue;
            }
            var row = rows.get(rowIndex);
            if (columnIndex >= row.size()) {
                ++rowIndex;
                columnIndex = 0;
                continue;
            }
            String data = row.get(columnIndex);
            if (data.startsWith(LINE_COMMENT)) {
                rowIndex++;
                columnIndex = 0;
                continue;
            }
            if (!data.isEmpty()) {
                return false;
            } else {
                ++columnIndex;
            }
        }
    }


    private boolean isExpectString(String s) {
        while (true) {
            if (sheetIndex >= sheetDatas.size()) {
                return false;
            }
            var rows = sheetDatas.get(sheetIndex).rows;
            if (rowIndex >= rows.size()) {
                ++sheetIndex;
                rowIndex = 0;
                continue;
            }
            List<String> row = rows.get(rowIndex);
            if (columnIndex >= row.size()) {
                rowIndex++;
                columnIndex = 0;
                continue;
            }
            String data = row.get(columnIndex);
            if (data.startsWith(LINE_COMMENT)) {
                rowIndex++;
                columnIndex = 0;
                continue;
            }
            if (!data.isEmpty()) {
                return data.equals(s);
            } else {
                ++columnIndex;
            }
        }
    }

    //判断下一个字符是不是列表结束
    public boolean isListEnd() {
        return isExpectString(LIST_END);
    }

    //获取非空的数据
    public String getNextNotEmpty() {
        var s = getNext();
        if (s == null) {
            log.error("{}数据不足", getDataType().getTypeName());
        }
        return s;
    }

    private String convertNum(double value) {
        long lvalue = (long) value;
        if (lvalue == value) {
            return Long.toString(lvalue);
        } else {
            return Double.toString(value);
        }
    }

    public void expectListBegin() {
        if (!LIST_BEGIN.equals(getNext())) {
            throw new RuntimeException("期望读到数组起始符 {");
        }
    }

    public void expectListEnd() {
        if (!LIST_END.equals(getNext())) {
            throw new RuntimeException("期望读到数组结束符 }");
        }
    }

    public List<IData> readListData(IType type, boolean readRecord) {
        var values = new ArrayList<IData>();
        if (!readRecord) {
            expectListBegin();
        }
        while (!(readRecord ? isEOF() : isListEnd())) {
            values.add(type.convert(this));
        }
        if (!readRecord) {
            expectListEnd();
        }
        return values;
    }

}
