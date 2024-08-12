package edu.virginia.sde.reviews;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CourseReviewsApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseDriver dbDriver = new DatabaseDriver("CRUD.sqlite");
        dbDriver.connect();
        dbDriver.createTables();
        dbDriver.commit();
        dbDriver.disconnect();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("User Login");
        stage.setScene(scene);
        stage.show();
    }
}
