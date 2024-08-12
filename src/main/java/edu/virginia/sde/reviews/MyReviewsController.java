package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MyReviewsController {
    @FXML
    private TableView<Review> reviewsTable;
    @FXML
    private TableColumn<Review, String> mnemonicColumn;
    @FXML
    private TableColumn<Review, Integer> numberColumn;
    @FXML
    private TableColumn<Review, Integer> ratingColumn;
    @FXML
    private TableColumn<Review, Integer> titleColumn;
    @FXML
    private TableColumn<Review, Integer> commentColumn;
    @FXML
    private Button backButton;

    private DatabaseDriver dbDriver;
    User curUser = null;

    @FXML
    public void initialize() {
        dbDriver = new DatabaseDriver("CRUD.sqlite");
        curUser = SessionManager.getInstance().getCurrentUser();

        loadUserReviews();

        mnemonicColumn.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        // Setting up the click event on table rows
        reviewsTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 1 && reviewsTable.getSelectionModel().getSelectedItem() != null) {
                onReviewClicked();
            }
        });
    }

    private void onReviewClicked() {
        Review selectedReview = reviewsTable.getSelectionModel().getSelectedItem();
        if (selectedReview != null) {
            Course course = new Course(selectedReview.getMnemonic(), Integer.toString(selectedReview.getNumber()), selectedReview.getTitle(), selectedReview.getRating());
            SessionManager.getInstance().setSelectedCourse(course);
            redirectToCourseReview();
        }
    }

    private void redirectToCourseReview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClassReviews.fxml"));
            Parent reviewPage = loader.load();
            Scene scene = new Scene(reviewPage);
            Stage stage = (Stage) reviewsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserReviews() {
        try {
            dbDriver.connect();
            ObservableList<Review> reviews = FXCollections.observableArrayList(dbDriver.loadReviewsByLoggedInUser(curUser.getUsername())); // Implement this method in DatabaseDriver
            reviewsTable.setItems(reviews);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                dbDriver.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CourseSearch.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) reviewsTable.getScene().getWindow();
            stage.setScene(scene);

            stage.setTitle("Course Search");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
