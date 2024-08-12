package edu.virginia.sde.reviews;

import javafx.collections.*;

import javafx.fxml.FXML;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CourseSearchController implements Initializable {
    @FXML
    private TextField mnemonicField;
    @FXML
    private TextField numberField;
    @FXML
    private TextField titleField;
    @FXML
    private Label errorMessageLabel;
    @FXML
    DatabaseDriver databaseDriver;
    @FXML
    private Pane bannerPane;
    @FXML
    private Label course;
    @FXML
    private TableView<Course> resultsTable; // Add TableView<Course> field


    public CourseSearchController() {
        databaseDriver = new DatabaseDriver("CRUD.sqlite");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseDriver.updateCourseListView(resultsTable);

        bannerPane.setStyle("-fx-background-color: #334499");
        course.setStyle("-fx-font-size: 20px; -fx-text-fill: white");
    }

    @FXML
    private void searchCourse() {
        String mnemonic = mnemonicField.getText();
        String courseNumberStr = numberField.getText();
        String title = titleField.getText();
        resultsTable.getItems().setAll(databaseDriver.searchCourse(mnemonic, courseNumberStr, title));

    }
    @FXML
    private void addCourse() {
        String mnemonic = mnemonicField.getText();
        String courseNumberStr = numberField.getText();
        String title = titleField.getText();

        try {
            databaseDriver.connect();
            int parsedCourseNumber = Integer.parseInt(courseNumberStr);
            boolean added = databaseDriver.addCourse(mnemonic, parsedCourseNumber, title);
            if (added) {
                errorMessageLabel.setVisible(false);
                databaseDriver.updateCourseListView(resultsTable);
            } else {
                errorMessageLabel.setText("Invalid course data.");
                errorMessageLabel.setVisible(true);
            }
        } catch (NumberFormatException e) {
            errorMessageLabel.setText("Course number must be a 4-digit number.");
            errorMessageLabel.setVisible(true);
        } catch (SQLException e) {
            errorMessageLabel.setText("Failed to add course. Database error occurred.");
            errorMessageLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    public void redirectCourseReview() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClassReviews.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) resultsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("MyReview");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onCourseSelected() {
        Course selectedCourse = resultsTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {


            SessionManager.getInstance().setSelectedCourse(selectedCourse);
            redirectCourseReview();
        }
    }

    @FXML
    private void redirectMyReview() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyReviews.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) numberField.getScene().getWindow();
            stage.setScene(scene);

            stage.setTitle("My Reviews");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void logOut(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) mnemonicField.getScene().getWindow();
            stage.setScene(scene);

            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
