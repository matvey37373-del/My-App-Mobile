package com.designer.model;
import java.io.Serializable;
public class DesignElement implements Serializable {
    private String id;
    private String type;
    private int x, y, width, height;
    private String text;
    private String bgColor;
    private String textColor;
    public DesignElement() {}
    public DesignElement(String id, String type, int x, int y, int width, int height, String text, String bgColor, String textColor) {
        this.id = id; this.type = type; this.x = x; this.y = y; this.width = width; this.height = height; this.text = text; this.bgColor = bgColor; this.textColor = textColor;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getBgColor() { return bgColor; }
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }
    public String getTextColor() { return textColor; }
    public void setTextColor(String textColor) { this.textColor = textColor; }
}
