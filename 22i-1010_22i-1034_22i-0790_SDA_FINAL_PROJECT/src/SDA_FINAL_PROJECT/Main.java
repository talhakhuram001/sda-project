package SDA_FINAL_PROJECT;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private MainMenu mainMenu = new MainMenu(); 
    static persistance_handler ph;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController loginController = loader.getController();
        loginController.setMainMenu(mainMenu);

        primaryStage.setTitle("Turf and Player Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {
    	try {
			ph = new persistance_handler();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        launch(args);
    }
}
