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
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
    private TableView<IData> content;

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
                setContent(newValue);
            }
        });
    }


    private void setContent(String tableName) {
        content.getColumns().clear();
        var tableCfg = Context.getIns().getTables().get(tableName);
        var records = FXCollections.observableList(tableCfg.getRecords());
        content.setItems(records);
        if (tableCfg.isDynamic()) {
            TableColumn<IData, String> dynamicType = new TableColumn<>("dataType");
            dynamicType.setCellValueFactory(param -> new SimpleObjectProperty<>(((IDataBean) param.getValue()).getActual().getName()));
            content.getColumns().add(dynamicType);
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
            content.getColumns().add(column);
        }
        for (BeanDefine child : beanDefine.getChildren()) {
            addColumn(child);
        }
    }

}
