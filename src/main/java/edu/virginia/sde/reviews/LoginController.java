package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private DatabaseDriver databaseDriver;
    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label loginErrorMessageLabel;
    @FXML
    private Pane bannerPane;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label login, username, password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseDriver = new DatabaseDriver("CRUD.sqlite");
        // Set font size for labels
        loginErrorMessageLabel.setStyle("-fx-font-size: 10px");

        // Set font size for text fields
        loginUsernameField.setStyle("-fx-font-size: 14px");
        loginPasswordField.setStyle("-fx-font-size: 14px");

        bannerPane.setStyle("-fx-background-color: #334499");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20.0");
        login.setStyle("-fx-font-size: 15px; -fx-font-weight: bold");
        username.setStyle("-fx-font-size: 13px");
        password.setStyle("-fx-font-size: 13px");

    }
    // Method to handle login button action
    @FXML
    private void login() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();

        try {
            databaseDriver.connect();
            boolean isAuthenticated = databaseDriver.loginUser(username, password);
            if(isAuthenticated){
                SessionManager.getInstance().setCurrentUser(new User(username, password));
                redirectToCourseSearch();
            } else {
                loginErrorMessageLabel.setText("Invalid Username or Password");
                clearTextFields();
            }
        } catch (SQLException e) {
            loginErrorMessageLabel.setText("Database error occurred");
        } finally {
            try {
                databaseDriver.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void redirectToCourseSearch() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CourseSearch.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            stage.setScene(scene);

            stage.setTitle("Course Search");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void redirectToRegistration() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) loginPasswordField.getScene().getWindow();
            stage.setScene(scene);

            stage.setTitle("Register New User");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void clearTextFields() {
        loginUsernameField.clear();
        loginPasswordField.clear();
    }
}

