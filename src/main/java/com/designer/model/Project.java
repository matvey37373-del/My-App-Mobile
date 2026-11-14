package com.designer.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Project implements Serializable {
    private String name;
    private List<DesignElement> elements = new ArrayList<>();
    public Project() {}
    public Project(String name) { this.name = name; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<DesignElement> getElements() { return elements; }
    public void setElements(List<DesignElement> elements) { this.elements = elements; }
}
