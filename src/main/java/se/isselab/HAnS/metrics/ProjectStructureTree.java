package se.isselab.HAnS.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProjectStructureTree {

    private String name;
    private String path;
    private Type type;
    private int depth;
    private HashSet<String> featureList;
    private List<ProjectStructureTree> children;

    private enum Type {FOLDER, FILE, LINE}

    public ProjectStructureTree() {}

    public ProjectStructureTree(String name, String path, Type type, int depth) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.depth = depth;
        this.featureList = new HashSet<>();
        this.children = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getDepth() {
        return depth;
    }

    public HashSet<String> getFeatureList() {
        return featureList;
    }

    public List<ProjectStructureTree> getChildren() {
        return children;
    }

}
