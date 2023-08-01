package com.WooGLEFX.Structures;

import com.WooGLEFX.Engine.FXCreator;
import com.WooGLEFX.File.FileManager;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.WoGAnimation;
import com.WooGLEFX.Structures.UserActions.AttributeChangeAction;
import com.WorldOfGoo.Addin.*;
import com.WorldOfGoo.Ball.*;
import com.WorldOfGoo.Level.*;
import com.WorldOfGoo.Particle.Ambientparticleeffect;
import com.WorldOfGoo.Particle.Axialsinoffset;
import com.WorldOfGoo.Particle.Particleeffect;
import com.WorldOfGoo.Particle._Particle;
import com.WorldOfGoo.Resrc.*;
import com.WorldOfGoo.Scene.*;
import com.WorldOfGoo.Scene.Label;
import com.WorldOfGoo.Scene.Button;
import com.WorldOfGoo.Scene.Slider;
import com.WorldOfGoo.Text.TextString;
import com.WorldOfGoo.Text.TextStrings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class EditorObject {

    private String realName;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    private EditorObject parent;

    private double depth = 0;

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public EditorAttribute getObjName() {
        return nameAttribute;
    }

    public void setNameAttribute(EditorAttribute nameAttribute) {
        this.nameAttribute = nameAttribute;
    }

    private EditorAttribute nameAttribute = null;

    public TreeItem<EditorObject> getTreeItem() {
        return treeItem;
    }

    public void setTreeItem(TreeItem<EditorObject> treeItem) {
        this.treeItem = treeItem;
    }

    private TreeItem<EditorObject> treeItem;

    public EditorObject getParent() {
        return parent;
    }

    public void setParent(EditorObject parent, int row) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.getChildren().add(row, this);
        }
    }

    public void setParent(EditorObject parent) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.getChildren().add(this);
        }
    }

    public void setChangeListener(String attributeName, ChangeListener<String> changeListener) {
        this.getAttribute2(attributeName).setChangeListener(changeListener);
    }

    private ArrayList<MetaEditorAttribute> metaAttributes = new ArrayList<>();

    public ArrayList<MetaEditorAttribute> getMetaAttributes() {
        return metaAttributes;
    }

    public void setMetaAttributes(ArrayList<MetaEditorAttribute> metaAttributes) {
        this.metaAttributes = metaAttributes;
        this.propertiesTreeItem = FXCreator.makePropertiesViewTreeItem(this);
    }

    public EditorObject(EditorObject _parent) {
        this.parent = _parent;
    }

    public static EditorObject create(String _name, EditorAttribute[] _attributes, EditorObject _parent) {
        EditorObject toAdd = null;
        switch (_name) {
            case "Addin_addin" -> toAdd = new Addin(_parent);
            case "Addin_id" -> toAdd = new AddinID(_parent);
            case "Addin_name" -> toAdd = new AddinName(_parent);
            case "Addin_type" -> toAdd = new AddinType(_parent);
            case "Addin_version" -> toAdd = new AddinVersion(_parent);
            case "Addin_description" -> toAdd = new AddinDescription(_parent);
            case "Addin_author" -> toAdd = new AddinAuthor(_parent);
            case "Addin_levels" -> toAdd = new AddinLevels(_parent);
            case "Addin_level" -> toAdd = new AddinLevel(_parent);
            case "Addin_dir" -> toAdd = new AddinLevelDir(_parent);
            case "Addin_wtf_name" -> toAdd = new AddinLevelName(_parent);
            case "Addin_subtitle" -> toAdd = new AddinLevelSubtitle(_parent);
            case "Addin_ocd" -> toAdd = new AddinLevelOCD(_parent);
            case "ambientparticleeffect" -> toAdd = new Ambientparticleeffect(_parent);
            case "axialsinoffset" -> toAdd = new Axialsinoffset(_parent);
            case "ball", "font" -> toAdd = new Ball(_parent);
            case "BallInstance" -> toAdd = new BallInstance(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "button" -> toAdd = new Button(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "buttongroup" -> toAdd = new Buttongroup(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "camera" -> toAdd = new Camera(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "circle" -> toAdd = new Circle(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "compositegeom" -> toAdd = new Compositegeom(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "detachstrand" -> toAdd = new Detachstrand(_parent);
            case "effects" -> toAdd = new Effects(_parent);
            case "endoncollision" -> toAdd = new Endoncollision(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "endonmessage" -> toAdd = new Endonmessage(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "endonnogeom" -> toAdd = new Endonnogeom(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "fire" -> toAdd = new Fire(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "hinge" -> toAdd = new Hinge(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "label" -> toAdd = new Label(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "level" -> toAdd = new Level(null);
            case "levelexit" -> toAdd = new Levelexit(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "line" -> toAdd = new Line(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "linearforcefield" -> toAdd = new Linearforcefield(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "loopsound" -> toAdd = new Loopsound(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "marker" -> toAdd = new Marker(_parent);
            case "motor" -> toAdd = new Motor(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "music" -> toAdd = new Music(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "part" -> toAdd = new Part(_parent);
            case "particleeffect" -> toAdd = new Particleeffect(_parent);
            case "particle" -> toAdd = new _Particle(null);
            case "particles" -> toAdd = new Particles(null);
            case "pipe" -> toAdd = new Pipe(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "poi" -> toAdd = new Poi(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "rectangle" -> toAdd = new Rectangle(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "radialforcefield" -> toAdd = new Radialforcefield(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "ResourceManifest" -> toAdd = new ResourceManifest(null);
            case "Resources" -> toAdd = new Resources(_parent);
            case "Image" -> toAdd = new ResrcImage(_parent);
            case "scene" -> toAdd = new Scene(null);
            case "SceneLayer" -> toAdd = new SceneLayer(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "SetDefaults" -> toAdd = new SetDefaults(_parent);
            case "shadow" -> toAdd = new Shadow(_parent);
            case "signpost" -> toAdd = new Signpost(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "sinanim" -> toAdd = new Sinanim(_parent);
            case "sinvariance" -> toAdd = new Sinvariance(_parent);
            case "slider" -> toAdd = new Slider(_parent == null ? Main.getLevel().getSceneObject() : null);
            case "sound", "Sound" -> toAdd = new Sound(_parent);
            case "splat" -> toAdd = new Splat(_parent);
            case "Strand" -> toAdd = new Strand(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "strand" -> toAdd = new BallStrand(_parent);
            case "string" -> toAdd = new TextString(_parent);
            case "strings" -> toAdd = new TextStrings(_parent);
            case "targetheight" -> toAdd = new Targetheight(_parent == null ? Main.getLevel().getLevelObject() : null);
            case "Vertex" -> toAdd = new Vertex(_parent == null ? Main.getLevel().getLevelObject() : null);
            default -> System.out.println(_name);
        }
        if (_parent != null) {
            toAdd.setParent(_parent);
            if (!_parent.getChildren().contains(toAdd)) {
                _parent.getChildren().add(toAdd);
            }
        }
        for (EditorAttribute attribute : _attributes){
            toAdd.setAttribute(attribute.getName(), attribute.getDefaultValue());
        }
        toAdd.setTreeItem(new TreeItem<>(toAdd));
        if (toAdd.getParent() != null) {
            toAdd.getParent().getTreeItem().getChildren().add(toAdd.getTreeItem());
        }
        return toAdd;
    }

    public void setToScreenCenter() {

    }

    public DragSettings mouseIntersection(double mX2, double mY2) { return new DragSettings(DragSettings.NONE); }

    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {
        return new DragSettings(DragSettings.NONE);
    }

    public DragSettings mouseImageIntersection(double mX2, double mY2) { return new DragSettings(DragSettings.NONE); }

    public DragSettings mouseImageIntersectingCorners(double mX2, double mY2) {
        return new DragSettings(DragSettings.NONE);
    }

    public static Point2D rotate(Point2D input, double theta, Point2D center){

        double rotatedX = (input.getX() - center.getX()) * Math.cos(theta) - (input.getY() - center.getY()) * Math.sin(theta);
        double rotatedY = (input.getX() - center.getX()) * Math.sin(theta) + (input.getY() - center.getY()) * Math.cos(theta);

        return new Point2D(rotatedX + center.getX(), rotatedY + center.getY());

    }

    private int spacesInFile = 1;

    public int getSpacesInFile() {
        return spacesInFile;
    }

    public void setSpacesInFile(int spacesInFile) {
        this.spacesInFile = spacesInFile;
    }

    public EditorObject cloneThis() {
        ArrayList<EditorAttribute> editorAttributes = new ArrayList<>();
        for (EditorAttribute attribute : attributes) {
            editorAttributes.add(new EditorAttribute(attribute.getName(), attribute.getValue(), attribute.getDefaultValue(), attribute.getInput(), false));
        }
        return EditorObject.create(getRealName(), editorAttributes.toArray(new EditorAttribute[0]), getParent());
    }

    public EditorObject deepClone(EditorObject parent) {
        EditorObject clone = create(realName, new EditorAttribute[0], parent);

        for (EditorAttribute attribute : getAttributes()) {
            clone.setAttribute(attribute.getName(), attribute.getValue());
        }

        for (EditorObject child : getChildren()) {
            EditorObject childClone = child.deepClone(clone);
            clone.getTreeItem().getChildren().add(childClone.getTreeItem());
        }
        return clone;
    }

    public float reverseInterpolate(float a, float b, float c){
        if (b > a) {
            return (c - a) / (b - a);
        } else if (b == a) {
            return a;
        } else {
            return (c - b) / (a - b);
        }
    }

    public int fix(int a){
        if (a != -1){
            return a;
        } else {
            return 0;
        }
    }

    private InputField[] inputs;

    public InputField[] getInputs() {
        return inputs;
    }

    public void setInputs(InputField[] inputs) {
        this.inputs = inputs;
    }

    private EditorAttribute[] attributes = new EditorAttribute[0];

    public EditorAttribute[] getAttributes(){
        return this.attributes;
    }

    public String getAttribute(String name){
        for (EditorAttribute attribute : this.attributes) {
            if (attribute.getName().equals(name)) {
                if (attribute.getValue().equals("")) {
                    return attribute.getDefaultValue();
                } else {
                    return attribute.getValue();
                }
            }
        }
        return null;
    }

    public EditorAttribute getAttribute2(String name){
        for (EditorAttribute attribute : this.attributes) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }

    private TreeItem<EditorAttribute> propertiesTreeItem = new TreeItem<>(new EditorAttribute("", "", "", new InputField("", InputField.NULL), false));

    public TreeItem<EditorAttribute> getPropertiesTreeItem() {
        return propertiesTreeItem;
    }

    public void setPropertiesTreeItem(TreeItem<EditorAttribute> propertiesTreeItem) {
        this.propertiesTreeItem = propertiesTreeItem;
    }

    public void setAttribute(String name, Object value){
        for (EditorAttribute attribute : this.attributes) {
            if (attribute.getName().equals(name)) {
                attribute.setValue(String.valueOf(value));
                return;
            }
        }
    }

    public void addAttribute(String name, String value, int inputFieldType, boolean required) {

        EditorAttribute[] newAttributes = new EditorAttribute[this.attributes.length + 1];
        int i2 = 0;
        for (EditorAttribute attribute : this.attributes){
            newAttributes[i2] = attribute;
            i2++;
        }
        newAttributes[i2] = new EditorAttribute(name, value, "", new InputField(name, inputFieldType), required);
        this.attributes = newAttributes;
    }

    public boolean isValid() {
        for (EditorAttribute attribute : this.attributes) {
            if (attribute.getValue().equals("")) {
                if (!attribute.getInput().verify(attribute.getDefaultValue())) {
                    return false;
                }
            } else if (!attribute.getInput().verify(attribute.getValue())) {
                System.out.println("failed to verify " + attribute.getValue());
                return false;
            }
        }
        return true;
    }

    public double getDouble(String attributeName) {
        try {
            for (EditorAttribute attribute : this.attributes) {
                if (attribute.getName().equals(attributeName)) {
                    if (attribute.getValue().equals("")) {
                        return Double.parseDouble(attribute.getDefaultValue());
                    } else {
                        return Double.parseDouble(attribute.getValue());
                    }
                }
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Could not find attribute " + attributeName + " for " + getRealName());
    }

    public AttributeChangeAction[] getUserActions(EditorAttribute[] oldAttributes){
        ArrayList<AttributeChangeAction> changes = new ArrayList<>();
        for (EditorAttribute attribute : this.attributes) {
            for (EditorAttribute oldAttribute : oldAttributes) {
                if (attribute.getName().equals(oldAttribute.getName()) && !attribute.getValue().equals(oldAttribute.getValue())) {
                    changes.add(new AttributeChangeAction(this, attribute.getName(), oldAttribute.getValue(), attribute.getValue()));
                }
            }
        }
        return changes.toArray(new AttributeChangeAction[0]);
    }

    private ArrayList<EditorObject> children = new ArrayList<>();

    public ArrayList<EditorObject> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<EditorObject> children) {
        this.children = children;
    }

    public EditorAttribute[] cloneAttributes(){
        ArrayList<EditorAttribute> output = new ArrayList<>();
        for (EditorAttribute attribute : this.attributes) {
            output.add(new EditorAttribute(attribute.getName(), attribute.getDefaultValue(), attribute.getValue(), attribute.getInput(), attribute.getRequiredInFile()));
        }
        return output.toArray(new EditorAttribute[0]);
    }

    public EditorObject getAbsoluteParent() {
        if (getParent() == null) {
            return this;
        } else {
            return getParent().getAbsoluteParent();
        }
    }

    public void update(){
        if (getAttributes().length != 0) {
            nameAttribute = getAttributes()[0];
        }
    }

    public String[] getPossibleChildren() {
        return new String[0];
    }

    public double getX(){
        return 0;
    }

    public double getY(){
        return 0;
    }

    public void updateWithAnimation(WoGAnimation animation, float timer){

    }

    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext){

    }

    public void drawImage(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext){

    }

    public void dragFromMouse(double mouseX, double mouseY, double dragSourceX, double dragSourceY){
        setAttribute("x", mouseX - dragSourceX);
        setAttribute("y", dragSourceY - mouseY);
    }

    public void resizeFromMouse(double mouseX, double mouseY, double resizeDragSourceX, double resizeDragSourceY, double resizeDragAnchorX, double resizeDragAnchorY){

    }

    public void setAnchor(double mouseX, double mouseY, double anchorStartX, double anchorStartY) {

    }

    public void rotateFromMouse(double mouseX, double mouseY, double rotateAngleOffset){

    }

}
