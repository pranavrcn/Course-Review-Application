package edu.virginia.sde.reviews;


import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;




public class ClassReviewsController implements Initializable {


    @FXML
    private TextArea reviewsArea;


    @FXML
    private TextField ratingField, commentField;
    @FXML
    DatabaseDriver databaseDriver;
    User curUser = null;
    Course selCourse=null;


    public ClassReviewsController() {
        databaseDriver = new DatabaseDriver("CRUD.sqlite");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        databaseDriver = new DatabaseDriver("CRUD.sqlite");
        curUser = SessionManager.getInstance().getCurrentUser();
        selCourse = SessionManager.getInstance().getSelectedCourse();
        if (curUser == null) {
            reviewsArea.setText("No user is currently logged in.");
            return;
        }
        try {
            databaseDriver.connect();
            onLoadReviews();


        } catch (SQLException e) {
            e.printStackTrace();
            reviewsArea.setText("Failed to initialize reviews due to: " + e.getMessage());
        } finally {
            try {
                databaseDriver.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    protected void onLoadReviews() {
        if (selCourse == null) {
            reviewsArea.setText("No course is selected.");
            return;
        }


        try {
            databaseDriver.connect();


            int courseId = databaseDriver.findCourseId(selCourse.getMnemonic(), selCourse.getNumber(), selCourse.getTitle());
            if (courseId == -1) {
                reviewsArea.setText("Selected course not found in the database.");
                return;
            }


            List<Review> reviews = databaseDriver.loadReviewsByCourseId(courseId);
            if (reviews != null && !reviews.isEmpty()) {
                String reviewsText = reviews.stream()
                        .map(Review::toString)
                        .collect(Collectors.joining("\n"));
                reviewsArea.setText(reviewsText);
            } else {
                reviewsArea.setText("No reviews available for this course.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            reviewsArea.setText("Failed to load reviews: " + e.getMessage());
        } finally {
            try {
                databaseDriver.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseSearch.fxml"));
            Parent courseSearchPage = loader.load();


            Scene scene = new Scene(courseSearchPage);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Get the current stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void onSubmitReview() throws SQLException {
        int courseId = databaseDriver.findCourseId(selCourse.getMnemonic(), selCourse.getNumber(), selCourse.getTitle());
        String username = curUser.getUsername();
        double rating = Double.parseDouble(ratingField.getText());
        String comment = commentField.getText();




        try  {
            databaseDriver.connect();
            databaseDriver.SubmitReview(courseId, username, rating, comment);
            databaseDriver.disconnect();
            onLoadReviews();
        } catch (SQLException e) {
            e.printStackTrace();
            reviewsArea.setText("Failed to submit review: " + e.getMessage());
        }
    }
    @FXML
    protected void onDeleteReview() {
        if (selCourse == null || curUser == null) {
            reviewsArea.setText("A course or user must be selected.");
            return;
        }

        try {
            databaseDriver.connect();
            int courseId = databaseDriver.findCourseId(selCourse.getMnemonic(), selCourse.getNumber(), selCourse.getTitle());
            if (courseId == -1) {
                reviewsArea.setText("Selected course not found in the database.");
                return;
            }

            boolean success = databaseDriver.deleteReview(courseId, curUser.getUsername());
            if (success) {
                reviewsArea.setText("Review deleted successfully.");
                onLoadReviews(); // Refresh the reviews display
            } else {
                reviewsArea.setText("Failed to delete review.");
            }
        } catch (SQLException e) {
            reviewsArea.setText("Failed to delete review: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                databaseDriver.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
