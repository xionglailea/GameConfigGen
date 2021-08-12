package ui;

import define.BeanDefine;
import define.column.BeanField;
import define.data.type.IData;
import define.data.type.IDataBean;
import generator.Context;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * <p>
 * create by xiongjieqing on 2021-05-13 22:53
 */
public class MainUi implements Initializable {

    @FXML
    private MenuBar menuBar;

    @FXML
    private ListView<String> configList;

    @FXML
    private TableView<IData> dataTableView;

    @FXML
    private Button addData;

    @FXML
    private Button removeData;

    private Stage oldEditStage;

    private ObservableList curDataRecords;

    private TableRow selectRow;

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
                if (event.getClickCount() == 1) {
                    selectRow = row;
                } else if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    if (oldEditStage != null && oldEditStage.isShowing()) {
                        return;
                    }
                    var data = row.getItem();
                    var beanData = (IDataBean) data;
                    try {
                        Stage beanView = new Stage();
                        FXMLLoader loader = new FXMLLoader(UiManager.class.getResource("/ui/EditUi.fxml"));
                        Parent root = loader.load();
                        beanView.setTitle(beanData.getDefine().getName());
                        beanView.setScene(new Scene(root));
                        beanView.show();
                        EditUi controller = loader.getController();
                        controller.changeData(this, beanData);
                        oldEditStage = beanView;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            return row;
        });
        configList.getSelectionModel().select(0);
        addData.setOnMouseClicked(event -> {
            if (oldEditStage != null && oldEditStage.isShowing()) {
                return;
            }
            String tableName = configList.getSelectionModel().getSelectedItem();
            var tableCfg = Context.getIns().getTables().get(tableName);
            try {
                Stage editView = new Stage();
                FXMLLoader loader = new FXMLLoader(UiManager.class.getResource("/ui/EditUi.fxml"));
                Parent root = loader.load();
                editView.setTitle(tableCfg.getName());
                editView.setScene(new Scene(root));
                editView.show();
                EditUi controller = loader.getController();
                controller.setDataModel(this, tableCfg);
                oldEditStage = editView;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        removeData.setOnMouseClicked(event -> {
            if (selectRow == null) {
                showTips("请选择要删除的数据");
                return;
            }
            if (oldEditStage != null && oldEditStage.isShowing()) {
                showTips("当前正在编辑数据，请保存后再开始删除数据！");
                return;
            }
            var item = selectRow.getItem();
            curDataRecords.remove(item);
            selectRow = null;
            removeOriginalData((IDataBean) item);
        });

    }

    private void showTips(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void setDataTableView(String tableName) {
        dataTableView.getColumns().clear();
        var tableCfg = Context.getIns().getTables().get(tableName);
        var records = FXCollections.observableList(tableCfg.getRecords());
        dataTableView.setItems(records);
        curDataRecords = records;
        if (tableCfg.isDynamic()) {
            TableColumn<IData, String> dynamicType = new TableColumn<>("dataType");
            dynamicType.setCellValueFactory(param -> new SimpleObjectProperty<>(((IDataBean) param.getValue()).getActual().getName()));
            dataTableView.getColumns().add(dynamicType);
        }
        addColumn(tableCfg);
    }

    public void addOriginalData(IDataBean data) {
        //if (configList.getSelectionModel().getSelectedItem().equals(data.getDefine().getName())) {
        //    var newIndex = BeanDefine.getDataIndexString(data);
        //    for (var item : dataTableView.getItems()) {
        //        if (newIndex == BeanDefine.getDataIndexString((IDataBean) item)) {
        //            dataTableView.getItems().remove(item);
        //            break;
        //        }
        //    }
        //    dataTableView.getItems().add(data);
        //}
        data.getDefine().addDataFromEditor(data);
        //
        if (data.getDefine().getName().equals(configList.getSelectionModel().getSelectedItem())) {
            setDataTableView(data.getDefine().getName());
        }
    }

    public void removeOriginalData(IDataBean data) {
        data.getDefine().removeData(data);
    }

    private void addColumn(BeanDefine beanDefine) {
        for (BeanField field : beanDefine.getFields()) {
            TableColumn<IData, String> column = new TableColumn<>(field.getName());
            column.setMaxWidth(100);
            column.setCellFactory(new Callback<>() {
                @Override
                public TableCell<IData, String> call(TableColumn<IData, String> param) {
                    return new TableCell<>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(item);
                            if (item != null && !item.trim().isEmpty()) {
                                setTooltip(new Tooltip(item));
                            }
                        }
                    };
                }
            });
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
