package edu.virginia.sde.reviews;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private Course selectedCourse;

    private SessionManager() { }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void clearCurrentUser() {
        currentUser = null;
    }

    public void setSelectedCourse(Course course) {
        this.selectedCourse = course;
    }

    public Course getSelectedCourse() {
        return selectedCourse;
    }
}
