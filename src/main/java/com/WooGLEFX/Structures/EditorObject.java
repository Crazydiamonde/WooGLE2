package com.WooGLEFX.Structures;

import java.util.ArrayList;
import java.util.Arrays;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;

import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;

import javafx.scene.control.TreeItem;

public class EditorObject {

    /** The name that World of Goo assigns to this object.
     * This is the same for every object of the same type. */
    private final String type;
    public final String getType() {
        return type;
    }


    private WorldLevel level;
    public final WorldLevel getLevel() {
        return level;
    }
    public final void setLevel(WorldLevel level) {
        this.level = level;
    }


    /** The parent of this object.
     * Every object is required to have a parent except for level/scene/resrc/addin/text root objects. */
    private EditorObject parent;
    public final EditorObject getParent() {
        return parent;
    }
    public final void setParent(EditorObject parent) {
        this.parent = parent;
        if (parent != null) parent.getChildren().add(this);
    }


    public EditorObject(EditorObject _parent, String type) {
        this.parent = _parent;
        this.type = type;
    }


    /** The TreeItem associated with this object. */
    private TreeItem<EditorObject> treeItem;
    public final TreeItem<EditorObject> getTreeItem() {
        return treeItem;
    }
    public final void setTreeItem(TreeItem<EditorObject> treeItem) {
        this.treeItem = treeItem;
    }
    public final void setParent(EditorObject parent, int row) {
        this.parent = parent;
        if (parent != null) parent.getChildren().add(row, this);
    }


    /** The EditorAttributes for this object.
     * All of these are added when the object is created. */
    private EditorAttribute[] attributes = new EditorAttribute[0];
    public final EditorAttribute[] getAttributes(){
        return this.attributes;
    }
    public final boolean attributeExists(String name) {
        return Arrays.stream(attributes).anyMatch(e -> e.getName().equals(name));
    }
    public final EditorAttribute getAttribute(String name) {
        for (EditorAttribute attribute : attributes) if (attribute.getName().equals(name)) return attribute;
        throw new RuntimeException("Accessed invalid attribute " + name + " (for " + type + ")");
    }
    public final void setAttribute(String name, Object value) {
        getAttribute(name).setValue(String.valueOf(value));
    }


    public final EditorAttribute addAttribute(String name, int inputFieldType) {
        EditorAttribute[] newAttributes = new EditorAttribute[attributes.length + 1];
        System.arraycopy(attributes, 0, newAttributes, 0, attributes.length);
        EditorAttribute newAttribute = new EditorAttribute(name, inputFieldType);
        newAttributes[attributes.length] = newAttribute;
        attributes = newAttributes;
        return newAttribute;
    }


    /** The children of this object. */
    private final ArrayList<EditorObject> children = new ArrayList<>();
    public final ArrayList<EditorObject> getChildren() {
        return children;
    }


    /** The meta attributes of the object.
     * These control how attributes are displayed to the user. */
    private ArrayList<MetaEditorAttribute> metaAttributes = new ArrayList<>();
    public final ArrayList<MetaEditorAttribute> getMetaAttributes() {
        return metaAttributes;
    }
    public final void setMetaAttributes(ArrayList<MetaEditorAttribute> metaAttributes) {
        this.metaAttributes = metaAttributes;
    }


    private final ArrayList<ObjectPosition> objectPositions = new ArrayList<>();
    public final ObjectPosition[] getObjectPositions() {
        return objectPositions.toArray(new ObjectPosition[0]);
    }
    public final void addObjectPosition(ObjectPosition objectPosition) {
        objectPositions.add(objectPosition);
    }
    public final void clearObjectPositions() {
        objectPositions.clear();
    }


    public String getName() {
        return "";
    }


    public void update() {
    }


    public String[] getPossibleChildren() {
        return new String[0];
    }

}
