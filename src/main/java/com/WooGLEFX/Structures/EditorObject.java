package com.WooGLEFX.Structures;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.WooGLEFX.Engine.FXCreator;
import com.WooGLEFX.Engine.Main;
import com.WooGLEFX.File.GlobalResourceManager;
import com.WooGLEFX.Structures.SimpleStructures.Color;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import com.WooGLEFX.Structures.SimpleStructures.MetaEditorAttribute;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import com.WooGLEFX.Structures.SimpleStructures.WoGAnimation;
import com.WooGLEFX.Structures.UserActions.AttributeChangeAction;
import com.WorldOfGoo.Addin.Addin;
import com.WorldOfGoo.Addin.AddinAuthor;
import com.WorldOfGoo.Addin.AddinDescription;
import com.WorldOfGoo.Addin.AddinID;
import com.WorldOfGoo.Addin.AddinLevel;
import com.WorldOfGoo.Addin.AddinLevelDir;
import com.WorldOfGoo.Addin.AddinLevelName;
import com.WorldOfGoo.Addin.AddinLevelOCD;
import com.WorldOfGoo.Addin.AddinLevelSubtitle;
import com.WorldOfGoo.Addin.AddinLevels;
import com.WorldOfGoo.Addin.AddinName;
import com.WorldOfGoo.Addin.AddinType;
import com.WorldOfGoo.Addin.AddinVersion;
import com.WorldOfGoo.Ball.Ball;
import com.WorldOfGoo.Ball.BallParticles;
import com.WorldOfGoo.Ball.BallSound;
import com.WorldOfGoo.Ball.BallStrand;
import com.WorldOfGoo.Ball.Detachstrand;
import com.WorldOfGoo.Ball.Marker;
import com.WorldOfGoo.Ball.Part;
import com.WorldOfGoo.Ball.Shadow;
import com.WorldOfGoo.Ball.Sinanim;
import com.WorldOfGoo.Ball.Sinvariance;
import com.WorldOfGoo.Ball.Splat;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Level.Camera;
import com.WorldOfGoo.Level.Endoncollision;
import com.WorldOfGoo.Level.Endonmessage;
import com.WorldOfGoo.Level.Endonnogeom;
import com.WorldOfGoo.Level.Fire;
import com.WorldOfGoo.Level.Level;
import com.WorldOfGoo.Level.Levelexit;
import com.WorldOfGoo.Level.Loopsound;
import com.WorldOfGoo.Level.Music;
import com.WorldOfGoo.Level.Pipe;
import com.WorldOfGoo.Level.Poi;
import com.WorldOfGoo.Level.Signpost;
import com.WorldOfGoo.Level.Strand;
import com.WorldOfGoo.Level.Targetheight;
import com.WorldOfGoo.Level.Vertex;
import com.WorldOfGoo.Particle.Ambientparticleeffect;
import com.WorldOfGoo.Particle.Axialsinoffset;
import com.WorldOfGoo.Particle.Effects;
import com.WorldOfGoo.Particle.Particleeffect;
import com.WorldOfGoo.Particle._Particle;
import com.WorldOfGoo.Resrc.ResourceManifest;
import com.WorldOfGoo.Resrc.Resources;
import com.WorldOfGoo.Resrc.ResrcImage;
import com.WorldOfGoo.Resrc.SetDefaults;
import com.WorldOfGoo.Resrc.Sound;
import com.WorldOfGoo.Scene.Button;
import com.WorldOfGoo.Scene.Buttongroup;
import com.WorldOfGoo.Scene.Circle;
import com.WorldOfGoo.Scene.Compositegeom;
import com.WorldOfGoo.Scene.Hinge;
import com.WorldOfGoo.Scene.Label;
import com.WorldOfGoo.Scene.Line;
import com.WorldOfGoo.Scene.Linearforcefield;
import com.WorldOfGoo.Scene.Motor;
import com.WorldOfGoo.Scene.Particles;
import com.WorldOfGoo.Scene.Radialforcefield;
import com.WorldOfGoo.Scene.Rectangle;
import com.WorldOfGoo.Scene.Scene;
import com.WorldOfGoo.Scene.SceneLayer;
import com.WorldOfGoo.Scene.Slider;
import com.WorldOfGoo.Text.TextString;
import com.WorldOfGoo.Text.TextStrings;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;

public class EditorObject {

    /** The name that World of Goo assigns to this object.
     * This is the same for every object of the same type. */
    private String realName;
    private static Vertex lastVertex = null;
    public String getRealName() {
        return realName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }


    /** The parent of this object.
     * Every object is required to have a parent except for level/scene/resrc/addin/text root objects. */
    private EditorObject parent;
    public EditorObject getParent() {
        return parent;
    }
    public void setParent(EditorObject parent) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.getChildren().add(this);
        }
    }


    /** The attribute that is used to display the object's name.
     * This gets set to another attribute when the object is created. */
    private EditorAttribute nameAttribute = null;
    public EditorAttribute getObjName() {
        return nameAttribute;
    }
    public void setNameAttribute(EditorAttribute nameAttribute) {
        this.nameAttribute = nameAttribute;
    }


    /** The optional secondary that is used to display the object's name.
     * This gets set to another attribute when the object is created. */
    private EditorAttribute nameAttribute2 = null;
    public EditorAttribute getObjName2() {
        return nameAttribute2;
    }
    public void setNameAttribute2(EditorAttribute nameAttribute2) {
        this.nameAttribute2 = nameAttribute2;
    }


    /** The TreeItem associated with this object. */
    private TreeItem<EditorObject> treeItem;
    public TreeItem<EditorObject> getTreeItem() {
        return treeItem;
    }
    public void setTreeItem(TreeItem<EditorObject> treeItem) {
        this.treeItem = treeItem;
    }
    public void setParent(EditorObject parent, int row) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.getChildren().add(row, this);
        }
    }


    /** The level that this object is inside. */
    private WorldLevel level;
    public WorldLevel getLevel() {
        return level;
    }
    public void setLevel(WorldLevel level) {
        this.level = level;
    }


    /** The EditorAttributes for this object.
     * All of these are added when the object is created. */
    private EditorAttribute[] attributes = new EditorAttribute[0];
    public EditorAttribute[] getAttributes(){
        return this.attributes;
    }
    public String getAttribute(String name) {
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
    public EditorAttribute getAttribute2(String name) {
        for (EditorAttribute attribute : this.attributes) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }
    public void setAttribute(String name, Object value) {
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
        newAttributes[i2] = new EditorAttribute(this, name, value, "", new InputField(name, inputFieldType), required);
        this.attributes = newAttributes;
    }
    public EditorAttribute[] cloneAttributes() {
        ArrayList<EditorAttribute> output = new ArrayList<>();
        for (EditorAttribute attribute : this.attributes) {
            output.add(new EditorAttribute(this, attribute.getName(), attribute.getDefaultValue(), attribute.getValue(), attribute.getInput(), attribute.getRequiredInFile()));
        }
        return output.toArray(new EditorAttribute[0]);
    }


    /** The children of this object. */
    private final ArrayList<EditorObject> children = new ArrayList<>();
    public ArrayList<EditorObject> getChildren() {
        return children;
    }


    /** The meta attributes of the object.
     * These control how attributes are displayed to the user. */
    private ArrayList<MetaEditorAttribute> metaAttributes = new ArrayList<>();
    public ArrayList<MetaEditorAttribute> getMetaAttributes() {
        return metaAttributes;
    }
    public void setMetaAttributes(ArrayList<MetaEditorAttribute> metaAttributes) {
        this.metaAttributes = metaAttributes;
        this.propertiesTreeItem = FXCreator.makePropertiesViewTreeItem(this);
    }

    public void setChangeListener(String attributeName, ChangeListener<String> changeListener) {
        this.getAttribute2(attributeName).setChangeListener(changeListener);
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
            case "ball_particles" -> toAdd = new BallParticles(_parent);
            case "ball_sound" -> toAdd = new BallSound(_parent);
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
        if (_parent != null && toAdd != null) {
            toAdd.setParent(_parent);
            if (!_parent.getChildren().contains(toAdd)) {
                _parent.getChildren().add(toAdd);
            }
        }
        for (EditorAttribute attribute : _attributes){
            toAdd.setAttribute(attribute.getName(), attribute.getDefaultValue());
        }
        toAdd.setTreeItem(new TreeItem<>(toAdd));
        if(toAdd instanceof Vertex){
            ((Vertex) toAdd).setPrevious(lastVertex);
            lastVertex = (Vertex) toAdd;
        }
        // Automatically add to parent
        if (toAdd.getParent() != null) {
            toAdd.getParent().getTreeItem().getChildren().add(toAdd.getTreeItem());
        }
        return toAdd;
    }

    public DragSettings mouseIntersection(double mX2, double mY2) { return new DragSettings(DragSettings.NONE); }

    public DragSettings mouseIntersectingCorners(double mX2, double mY2) {
        return new DragSettings(DragSettings.NONE);
    }

    public DragSettings mouseImageIntersection(double mX2, double mY2) { return new DragSettings(DragSettings.NONE); }

    public static Point2D rotate(Point2D input, double theta, Point2D center){

        double rotatedX = (input.getX() - center.getX()) * Math.cos(theta) - (input.getY() - center.getY()) * Math.sin(theta);
        double rotatedY = (input.getX() - center.getX()) * Math.sin(theta) + (input.getY() - center.getY()) * Math.cos(theta);

        return new Point2D(rotatedX + center.getX(), rotatedY + center.getY());

    }

    public EditorObject deepClone(EditorObject parent) {
        EditorObject clone = create(realName, new EditorAttribute[0], parent);

        for (EditorAttribute attribute : getAttributes()) {
            clone.setAttribute(attribute.getName(), attribute.getValue());
        }

        for (EditorObject child : getChildren()) {
            child.deepClone(clone);
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

    private TreeItem<EditorAttribute> propertiesTreeItem = new TreeItem<>(new EditorAttribute(this, "", "", "", new InputField("", InputField.NULL), false));

    public TreeItem<EditorAttribute> getPropertiesTreeItem() {
        return propertiesTreeItem;
    }

    public boolean isValid() {
        for (EditorAttribute attribute : this.attributes) {
            if (attribute.getValue().equals("")) {
                if (!attribute.getInput().verify(this, attribute.getDefaultValue())) {
                    return false;
                }
            } else if (!attribute.getInput().verify(this, attribute.getValue())) {
                //System.out.println("failed to verify " + attribute.getValue());
                return false;
            }
        }
        return true;
    }

    public double getDouble(String attributeName) {
        if (getAttribute(attributeName) == null) {
            throw new RuntimeException("Could not find attribute " + attributeName + " for " + getRealName());
        } else {
            try {
                return Double.parseDouble(getAttribute(attributeName));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Could not parse double " + attributeName + " for " + getRealName() + ": found value " + getAttribute(attributeName));
            }
        }
    }

    public Position getPosition(String attributeName) {
        if (getAttribute(attributeName) == null) {
            throw new RuntimeException("Could not find attribute " + attributeName + " for " + getRealName());
        } else {
            try {
                return Position.parse(getAttribute(attributeName));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Could not parse position " + attributeName + " for " + getRealName() + ": found value " + getAttribute(attributeName));
            }
        }
    }

    public Color getColor(String attributeName) {
        if (getAttribute(attributeName) == null) {
            throw new RuntimeException("Could not find attribute " + attributeName + " for " + getRealName());
        } else {
            try {
                return Color.parse(getAttribute(attributeName));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Could not parse color " + attributeName + " for " + getRealName() + ": found value " + getAttribute(attributeName));
            }
        }
    }

    public String getString(String attributeName) {
        String attribute = getAttribute(attributeName);
        if (attribute != null) {
            return attribute;
        } else {
            throw new RuntimeException("Could not find string attribute " + attributeName + " for " + getRealName());
        }
    }

    public Image getImage(String attributeName) {
        if (getAttribute(attributeName) == null) {
            throw new RuntimeException("Could not find attribute " + attributeName + " for " + getRealName());
        } else {
            try {
                return GlobalResourceManager.getImage(getAttribute(attributeName), level.getVersion());
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Could not parse image " + attributeName + " for " + getRealName() + ": found value " + getAttribute(attributeName));
            }
        }
    }

    public Boolean getBoolean(String attributeName) {
        if (getAttribute(attributeName) == null) {
            throw new RuntimeException("Could not find attribute " + attributeName + " for " + getRealName());
        } else {
            return Boolean.parseBoolean(getAttribute(attributeName));
        }
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

    public void updateWithAnimation(WoGAnimation animation, float timer){

    }

    public void draw(GraphicsContext graphicsContext, GraphicsContext imageGraphicsContext) {

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
