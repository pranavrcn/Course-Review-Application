# Course Review Application (Capstone Project)

## Overview

This project is a JavaFX-based CRUD (Create, Read, Update, Delete) application for managing course reviews at the University of Virginia (UVA). The application allows students to log in, create and manage course reviews, and view reviews submitted by others. This capstone project demonstrates full-stack development skills, including GUI design, database management, and persistent data storage using SQLite.

## Key Features

### 1. User Authentication
   - **Login:** Users can log in using a unique username and password.
   - **User Registration:** Users can create a new account with a unique username and a password of at least 8 characters.
   - **Logout:** Users can log out, returning to the login screen.

### 2. Course Management
   - **Course Search:** Users can search for courses by subject mnemonic (e.g., "CS"), course number (e.g., 3140), or course title.
   - **Course Listing:** A list of courses is displayed, showing the course subject, number, title, and average rating.
   - **Add Course:** Users can add new courses to the database, with validation for course subject, number, and title.

### 3. Course Reviews
   - **View Reviews:** Users can view all reviews for a selected course, including ratings, timestamps, and optional comments.
   - **Submit Review:** Users can submit a new review for a course, which includes a rating (1-5) and an optional comment.
   - **Edit Review:** Users can edit their existing reviews, updating both the rating and comment.
   - **Delete Review:** Users can delete their reviews, removing them from the database.

### 4. My Reviews
   - **Review Listing:** Users can view all the reviews they have submitted, including the course details and their ratings.
   - **Review Navigation:** Users can navigate to the review details for each course from their list of reviews.

### 5. Data Persistence
   - **SQLite Database:** All data (users, courses, reviews) is stored in a persistent SQLite database.
   - **Database Rebuilding:** If the database is missing, the application can rebuild it automatically.

### 6. Error Handling and Usability
   - **Input Validation:** The application provides feedback for invalid input, such as incorrect login details or invalid course information.
   - **GUI Usability:** The interface is designed to be user-friendly and works on a screen resolution of 1280x720 pixels.

## Technical Skills Demonstrated

- **JavaFX:** Designed and implemented a user-friendly GUI with multiple scenes for different user interactions.
- **Database Management:** Utilized SQLite for data persistence, ensuring all user data and course reviews are saved and retrievable.
- **SQL:** Wrote SQL queries to manage data in a relational database, ensuring data integrity and enforcing constraints.
- **Data Validation:** Implemented input validation and error handling to ensure robustness and usability.
- **Java Programming:** Employed object-oriented principles, including the use of JDBC.
- 
