package code.calvin.leagueinfo;

import code.calvin.leagueinfo.summoner.SummonerProfileManager;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Region;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LeagueInfo extends Application {

    private Stage window;
    private Scene mainScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new SummonerProfileManager();

        RiotAPI.setRegion(Region.NA);
        RiotAPI.setAPIKey("c707d84b-c49c-46f6-8ece-f36c3b796f4a");

        window = primaryStage;
        window.setTitle("LeagueInfo");
        window.setMinHeight(700);
        window.setMinWidth(1250);

        setupMainScene();

        window.setScene(mainScene);
        window.show();
    }

    public void setupMainScene() throws Exception {
        Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/sample.fxml"));
        mainScene = new Scene(parent);

        window.setTitle("LeagueInfo");
        window.setMinHeight(700);
        window.setMinWidth(1250);
        window.show();
    }
}
