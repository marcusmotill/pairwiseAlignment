package com.company;

/**
 * Created by marcusmotill on 2/10/16.
 */
public class MatrixItem {
    private int score;
    private Direction direction;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
