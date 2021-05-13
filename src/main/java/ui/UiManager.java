package ui;


import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * <p>
 * create by xiongjieqing on 2021/5/13 21:14
 */
public class UiManager {


    private URL url;

    @SneakyThrows
    public UiManager(URL url) {
        this.url = url;
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("数据编辑器");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
