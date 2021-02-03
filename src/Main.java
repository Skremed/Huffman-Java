import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main extends Application {
    public static Stage window;


    @Override
    public void start(Stage primaryStage) throws Exception{
        //File n = encoder.Encode(new File("G:\\DataStracture\\src\\text.txt"));
        //encoder.Decode(n);

        Main.window = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainPage.fxml"));
        Parent layout = loader.load();
        Scene MainPage = new Scene(layout,780,600);
        Main.window.setScene(MainPage);
        Main.window.setTitle("Main");
        Main.window.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
