package core;

import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L; 
    private final String color;
    private final String value;
    private final int points;

    public Card(String color, String value, int points) {
        this.color = color;
        this.value = value;
        this.points = points;
    }

    public String getColor() {
        return color;
    }

    public String getValue() {
        return value;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return color + " " + value + " (" + points + " points)";
    }
}
