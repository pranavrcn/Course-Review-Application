package edu.virginia.sde.reviews;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Course {
    private final StringProperty mnemonic;
    private final StringProperty number;
    private final StringProperty title;
    private final DoubleProperty rating;

    public Course(String mnemonic, String number, String title, double rating) {
        this.mnemonic = new SimpleStringProperty(mnemonic);
        this.number = new SimpleStringProperty(number);
        this.title = new SimpleStringProperty(title);
        this.rating = new SimpleDoubleProperty(rating);
    }

    // Getter methods for properties
    public String getMnemonic() {
        return mnemonic.get();
    }

    public String getNumber() {
        return number.get();
    }

    public String getTitle() {
        return title.get();
    }

    public double getRating() {
        return rating.get();
    }

    // Setter methods for properties
    public void setMnemonic(String value) {
        mnemonic.set(value);
    }

    public void setNumber(String value) {
        number.set(value);
    }

    public void setTitle(String value) {
        title.set(value);
    }

    public void setRating(double value) {
        rating.set(value);
    }

    // Property getter methods
    public StringProperty mnemonicProperty() {
        return mnemonic;
    }

    public StringProperty numberProperty() {
        return number;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public DoubleProperty ratingProperty() {
        return rating;
    }
}
