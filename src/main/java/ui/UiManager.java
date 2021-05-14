package ui;


import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.SneakyThrows;

/**
 * <p>
 * create by xiongjieqing on 2021/5/13 21:14
 */
public class UiManager {


    private URL baseUrl;

    @SneakyThrows
    public UiManager(URL url) {
        this.baseUrl = url;
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(baseUrl);
        primaryStage.setTitle("数据编辑器");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
