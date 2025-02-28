package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;


public class Client extends Application {
    public static void main(String[] args) {
       launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Clientu.fxml"));
        Parent root = loader.load();

        CleintController controller = loader.getController();
        controller.setServerInfo("localhost",1234);

        primaryStage.setTitle("Currency Converter");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}


