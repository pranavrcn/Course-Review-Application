package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    private DatabaseDriver databaseDriver;
    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private Label createErrorMessageLabel;
    @FXML
    private Label registerLabel;
    @FXML
    private Pane bannerPane;


    public void initialize(URL location, ResourceBundle resources){
        databaseDriver = new DatabaseDriver("CRUD.sqlite");
        // Set font size for labels
        createErrorMessageLabel.setStyle("-fx-font-size: 10px");

        // Set font size for text fields
        newUsernameField.setStyle("-fx-font-size: 14px");
        newPasswordField.setStyle("-fx-font-size: 14px");

        bannerPane.setStyle("-fx-background-color: #334499");
        registerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #FFFFFF");

    }

    @FXML
    private void createAccount() {
        String newusername = newUsernameField.getText();
        String newpassword = newPasswordField.getText();

        try {
            databaseDriver.connect();
            boolean isRegistered = databaseDriver.registerUser(newusername, newpassword);

            if (isRegistered) {
                createErrorMessageLabel.setText("Successfully created");
                clearTextFields();
            } else {
                createErrorMessageLabel.setText("Failed to create account");
                clearTextFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            createErrorMessageLabel.setText("User already exists, try again");
        } finally {
            try {
                databaseDriver.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    @FXML
    private void redirectLogin(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) newPasswordField.getScene().getWindow();
            stage.setScene(scene);

            stage.setTitle("User Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearTextFields() {
        newUsernameField.clear();
        newPasswordField.clear();
    }
}
