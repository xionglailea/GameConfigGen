package ui;

import generator.Context;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;

/**
 * <p>
 * create by xiongjieqing on 2021-05-13 22:53
 */
public class MainUi implements Initializable {

    @FXML
    private MenuBar mainMenu;

    @FXML
    private ListView<String> configList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initConfigList();
    }

    public void initConfigList() {
        var items = FXCollections.observableArrayList(Context.getIns().getTables().keySet());
        System.out.println(items);
        configList.setItems(items);
        configList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue);
            }
        });
    }


}
