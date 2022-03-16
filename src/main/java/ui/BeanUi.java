package ui;

import define.data.type.IData;
import define.data.type.IDataBean;
import define.data.type.IDataList;
import define.data.type.IDataMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 展示bean的结构,修改无效
 *
 * <p>
 * create by xiongjieqing on 2021-05-15 21:20
 */
public class BeanUi implements Initializable {

    @FXML
    private GridPane rootGridPane;

    private IDataBean data;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initData(IDataBean data) {
        this.data = data;
        showRoot();
    }

    private void showRoot() {
        System.out.println(data.getActual().getName());
        for (int i = 0; i < data.getValues().size(); i++) {
            String name = data.getActual().getAllFields().get(i).getName();
            Label label = new Label(name);
            GridPane.setHalignment(label, HPos.LEFT);
            label.setPadding(new Insets(0, 3, 0, 5));
            rootGridPane.add(label, 0, i);
            label.setTooltip(new Tooltip(data.getActual().getAllFields().get(i).getComment()));
            Node fieldValue = createValue(data.getValues().get(i));
            GridPane.setHalignment(fieldValue, HPos.LEFT);
            rootGridPane.add(fieldValue, 1, i);
        }
    }

    private Node createValue(IData data) {
        if (data instanceof IDataBean) {
            IDataBean dataBean = (IDataBean) data;
            return createBeanTitledPane(dataBean);
        } else if (data instanceof IDataMap) {
            IDataMap dataMap = (IDataMap) data;
            return createMapTitledPane(dataMap);
        } else if (data instanceof IDataList) {
            IDataList dataList = (IDataList) data;
            return createListTitlePane(dataList);
        } else {
            return new TextField(data.toString());
        }

    }

    private TitledPane createBeanTitledPane(IDataBean dataBean) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane(dataBean.toString(), gridPane);
        titledPane.setExpanded(false);
        for (int i = 0; i < dataBean.getValues().size(); i++) {
            String name = dataBean.getActual().getAllFields().get(i).getName();
            Label label = new Label(name);
            label.setPadding(new Insets(0, 3, 0, 0));
            GridPane.setHalignment(label, HPos.LEFT);
            gridPane.add(label, 0, i);
            label.setTooltip(new Tooltip(dataBean.getActual().getAllFields().get(i).getComment()));
            Node fieldValue = createValue(dataBean.getValues().get(i));
            GridPane.setHalignment(fieldValue, HPos.LEFT);
            gridPane.add(fieldValue, 1, i);
        }
        return titledPane;
    }

    private TitledPane createMapTitledPane(IDataMap dataMap) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane(dataMap.toString(), gridPane);
        titledPane.setExpanded(false);
        int i = 0;
        for (Map.Entry<IData, IData> entry : dataMap.getValues().entrySet()) {
            String name = entry.getKey().toString();
            Label label = new Label(name);
            label.setPadding(new Insets(0, 3, 0, 0));
            GridPane.setHalignment(label, HPos.LEFT);
            gridPane.add(label, 0, i);
            Node fieldValue = createValue(entry.getValue());
            GridPane.setHalignment(fieldValue, HPos.LEFT);
            gridPane.add(fieldValue, 1, i);
            i++;
        }
        return titledPane;
    }

    private TitledPane createListTitlePane(IDataList dataList) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane(dataList.toString(), gridPane);
        titledPane.setExpanded(false);
        int i = 0;
        for (IData item : dataList.getValues()) {
            Node fieldValue = createValue(item);
            GridPane.setHalignment(fieldValue, HPos.LEFT);
            gridPane.add(fieldValue, 0, i);
            i++;
        }
        return titledPane;
    }

}
