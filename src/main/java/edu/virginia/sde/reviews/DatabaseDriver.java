package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseDriver {
    private final String databaseUrl;
    private Connection connection;
    @FXML
    private TableView<Course> resultsTable;


    public DatabaseDriver(String databaseFilename) {
        this.databaseUrl = "jdbc:sqlite:" + databaseFilename;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(databaseUrl);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        statement.execute("PRAGMA foreign_keys = ON");
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Users (" +
                    "Username TEXT PRIMARY KEY, " +
                    "Password TEXT NOT NULL" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS Courses (" +
                    "CourseID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Mnemonic TEXT NOT NULL, " +
                    "Number INTEGER NOT NULL, " +
                    "Title TEXT NOT NULL, " +
                    "AvgReview INTEGER NOT NULL" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS Reviews (" +
                    "ReviewID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "CourseID INTEGER NOT NULL, " +
                    "Username INTEGER NOT NULL, " +
                    "Rating INTEGER NOT NULL, " +
                    "Comment TEXT, " +
                    "Timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (Username) REFERENCES Users(Username), " +
                    "FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE" +
                    ")");
        }
    }

    public boolean registerUser(String username, String password) throws SQLException {
        if (password.length() < 8) {
            return false;
        }
        String sql = "INSERT INTO Users (Username, Password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public boolean loginUser(String username, String password) throws SQLException {
        String sql = "SELECT Password FROM Users WHERE Username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("Password");
                if (storedPassword.equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addCourse(String mnemonic, int courseNumber, String title) throws SQLException {
        if (mnemonic == null || !mnemonic.matches("[a-zA-Z]{2,4}")) {
            return false;
        }
        if (String.valueOf(courseNumber).length() != 4) {
            return false;
        }
        if (title == null || title.isEmpty() || title.length() > 50) {
            return false;
        }

        // Check if the course already exists
        if (courseExists(mnemonic, courseNumber, title)) {
            return false; // Course already exists, so return false
        }

        String sql = "INSERT INTO Courses (Mnemonic, Number, Title) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, mnemonic.toUpperCase()); // Ensuring mnemonic is stored in uppercase
            pstmt.setInt(2, courseNumber);
            pstmt.setString(3, title);
            pstmt.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    private boolean courseExists(String mnemonic, int courseNumber, String title) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM Courses WHERE Mnemonic = ? AND Number = ? AND Title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, mnemonic.toUpperCase());
            pstmt.setInt(2, courseNumber);
            pstmt.setString(3, title);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("count") > 0; // If count is greater than 0, course exists
        }
    }


    public void updateCourseListView(TableView<Course> resultsTable) {
        ObservableList<Course> courseList = FXCollections.observableArrayList();
        try {
            connect();

            String sql = "SELECT c.Mnemonic, c.Number, c.Title, COALESCE(AVG(r.Rating), 0) AS AvgRating \n" +
                    "FROM Courses c \n" +
                    "LEFT JOIN Reviews r ON c.CourseID = r.CourseID \n" +
                    "GROUP BY c.CourseID\n";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String mnemonic = rs.getString("Mnemonic");
                    String number = rs.getString("Number");
                    String title = rs.getString("Title");
                    double avgRating = rs.getDouble("AvgRating");
                    courseList.add(new Course(mnemonic, number, title, avgRating));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error
        } finally {
            try {
                disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        resultsTable.getItems().setAll(courseList);
    }

    public int findCourseId(String mnemonic, String courseNumber, String title) throws SQLException {
        connect();
        try {
            String sql = "SELECT CourseID FROM Courses WHERE Mnemonic = ? AND Number = ? AND Title = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, mnemonic.toUpperCase());
                pstmt.setString(2, courseNumber);
                pstmt.setString(3, title);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("CourseID");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            disconnect();
        }
    }

    public List<Review> loadReviewsByCourseId(int courseId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        connect();
        String sql = "SELECT * FROM Reviews WHERE CourseID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int reviewId = rs.getInt("ReviewID");
                int receivedCourseId = rs.getInt("CourseID");
                String username = rs.getString("Username");
                int rating = rs.getInt("Rating");
                String comment = rs.getString("Comment");
                Timestamp timestamp = rs.getTimestamp("Timestamp");
                Review review = new Review(reviewId, receivedCourseId, username, rating, comment, timestamp);
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.out.println("Error loading reviews: " + e.getMessage());
            e.printStackTrace();
        }
        return reviews;
    }


    @FXML
    protected void SubmitReview(int courseId, String username, double rating, String comment) {
        try {
            connect();  // Ensure the database connection is active

            // SQL to check if a review already exists for the given course and user
            String checkSql = "SELECT ReviewID FROM Reviews WHERE CourseID = ? AND Username = ?";
            boolean updateNeeded = false;
            int reviewId = -1;

            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, courseId);
                checkStmt.setString(2, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    updateNeeded = true;
                    reviewId = rs.getInt("ReviewID");
                }
            }

            if (updateNeeded) {
                // If the review exists, update it
                String updateSql = "UPDATE Reviews SET Rating = ?, Comment = ? WHERE ReviewID = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, rating);
                    updateStmt.setString(2, comment);
                    updateStmt.setInt(3, reviewId);
                    updateStmt.executeUpdate();
                }
            } else {
                // If the review does not exist, insert it
                String insertSql = "INSERT INTO Reviews (CourseID, Username, Rating, Comment) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, courseId);
                    insertStmt.setString(2, username);
                    insertStmt.setDouble(3, rating);
                    insertStmt.setString(4, comment);
                    insertStmt.executeUpdate();
                }
            }

            connection.commit();  // Commit the transaction
        } catch (SQLException e) {
            try {
                connection.rollback();  // Roll back the transaction in case of an error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                disconnect();  // Ensure the connection is closed
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }



    public ObservableList<Course> searchCourse(String subject, String courseNumber, String title) {
        ObservableList<Course> courseList = FXCollections.observableArrayList();
        try {
            connect();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT c.Mnemonic, c.Number, c.Title, AVG(r.Rating) AS AvgRating ")
                    .append("FROM Courses c ")
                    .append("LEFT JOIN Reviews r ON c.CourseID = r.CourseID ")
                    .append("WHERE 1 = 1 ");

            List<Object> params = new ArrayList<>(); // List to store query parameters

            if (subject != null && !subject.isEmpty()) {
                sqlBuilder.append("AND c.Mnemonic = ? ");
                params.add(subject.toUpperCase());
            }
            if (courseNumber != null && !courseNumber.isEmpty()) {
                sqlBuilder.append("AND c.Number = ? ");
                params.add(Integer.parseInt(courseNumber));
            }
            if (title != null && !title.isEmpty()) {
                sqlBuilder.append("AND UPPER(c.Title) LIKE ? ");
                params.add("%" + title.toUpperCase() + "%");
            }
            sqlBuilder.append("GROUP BY c.CourseID");
            try (PreparedStatement pstmt = connection.prepareStatement(sqlBuilder.toString())) {
                // Set parameters for the prepared statement
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String resultMnemonic = rs.getString("Mnemonic");
                    String resultNumber = rs.getString("Number");
                    String resultTitle = rs.getString("Title");
                    double avgRating = rs.getDouble("AvgRating");
                    courseList.add(new Course(resultMnemonic, resultNumber, resultTitle, avgRating));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error
        } finally {
            try {
                disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return courseList;
    }

    public List<Review> loadReviewsByLoggedInUser(String username) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, c.Mnemonic, c.Number, c.Title FROM Reviews r JOIN Courses c ON r.CourseID = c.CourseID WHERE Username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int reviewId = rs.getInt("ReviewID");
                String mnemonic = rs.getString("Mnemonic");
                int number = rs.getInt("Number");
                int rating = rs.getInt("Rating");
                String title = rs.getString("Title");
                String comment = rs.getString("Comment");
                Timestamp timestamp = rs.getTimestamp("Timestamp");
                Review review = new Review(reviewId, mnemonic, number, title, username, rating, comment, timestamp);
                reviews.add(review);
            }

            return reviews;
        }


    }

    public boolean deleteReview(int courseId, String username) throws SQLException {
        connect();
        String sql = "DELETE FROM Reviews WHERE CourseID = ? AND Username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, username);
            int affectedRows = pstmt.executeUpdate();
            connection.commit();
            return affectedRows > 0;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

}