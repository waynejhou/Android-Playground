package org.waynezhou.androidplayground.layout;

public class ViewLayoutVariables {
    public volatile Integer left = null;
    public volatile Integer top = null;
    public volatile Integer width = null;
    public volatile Integer height = null;

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRight() {
        return left + width;
    }

    public int getBottom() {
        return top + height;
    }
}
