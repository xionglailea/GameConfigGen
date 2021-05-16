package ui;

import define.BeanDefine;
import define.column.BeanField;
import define.data.type.IData;
import define.data.type.IDataBean;
import generator.Context;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>
 * create by xiongjieqing on 2021-05-13 22:53
 */
public class MainUi implements Initializable {

    @FXML
    private MenuBar mainMenu;

    @FXML
    private ListView<String> configList;

    @FXML
    private TableView<IData> dataTableView;

    private Stage oldBeanStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initConfigList();
    }

    private void initConfigList() {
        var items = FXCollections.observableArrayList(Context.getIns().getTables().keySet());
        configList.setItems(items);
        configList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                setDataTableView(newValue);
            }
        });
        dataTableView.setRowFactory(param -> {
            TableRow<IData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    if (oldBeanStage != null) {
                        oldBeanStage.close();
                    }
                    var data = row.getItem();
                    var beanData = (IDataBean) data;
                    try {
                        Stage beanView = new Stage();
                        FXMLLoader loader = new FXMLLoader(UiManager.class.getResource("/ui/beanUi.fxml"));
                        Parent root = loader.load();
                        beanView.setTitle(beanData.getActual().getName());
                        beanView.setScene(new Scene(root));
                        beanView.show();
                        BeanUi controller = loader.getController();
                        controller.initData(beanData);
                        oldBeanStage = beanView;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            return row;
        });
    }


    private void setDataTableView(String tableName) {
        dataTableView.getColumns().clear();
        var tableCfg = Context.getIns().getTables().get(tableName);
        var records = FXCollections.observableList(tableCfg.getRecords());
        dataTableView.setItems(records);
        if (tableCfg.isDynamic()) {
            TableColumn<IData, String> dynamicType = new TableColumn<>("dataType");
            dynamicType.setCellValueFactory(param -> new SimpleObjectProperty<>(((IDataBean) param.getValue()).getActual().getName()));
            dataTableView.getColumns().add(dynamicType);
        }
        addColumn(tableCfg);
    }

    private void addColumn(BeanDefine beanDefine) {
        for (BeanField field : beanDefine.getFields()) {
            TableColumn<IData, String> column = new TableColumn<>(field.getName());
            column.setCellValueFactory(param -> {
                var data = ((IDataBean) param.getValue()).getDataByFieldName(field.getName());
                if (data == null) {
                    return new SimpleObjectProperty<>("");
                } else {
                    return new SimpleObjectProperty<>(data.toString());
                }

            });
            dataTableView.getColumns().add(column);
        }
        for (BeanDefine child : beanDefine.getChildren()) {
            addColumn(child);
        }
    }

}
