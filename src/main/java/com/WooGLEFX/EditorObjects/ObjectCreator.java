package com.WooGLEFX.EditorObjects;

import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WorldOfGoo.Addin.*;
import com.WorldOfGoo.Ball.*;
import com.WorldOfGoo.Level.*;
import com.WorldOfGoo.Particle.*;
import com.WorldOfGoo.Resrc.*;
import com.WorldOfGoo.Scene.*;
import com.WorldOfGoo.Resrc.Font;
import com.WorldOfGoo.Text.TextString;
import com.WorldOfGoo.Text.TextStrings;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectCreator {

    private static final Logger logger = LoggerFactory.getLogger(ObjectCreator.class);


    private static Vertex lastVertex = null;


    public static EditorObject create(String _name, EditorObject _parent) {
        EditorObject toAdd = null;
        switch (_name) {
            case "addin", "Addin_addin" -> toAdd = new Addin(_parent);
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
            case "ball" -> toAdd = new Ball(_parent);
            case "BallInstance" -> toAdd = new BallInstance(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "ball_particles" -> toAdd = new BallParticles(_parent);
            case "ball_sound" -> toAdd = new BallSound(_parent);
            case "button" -> toAdd = new Button(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "buttongroup" -> toAdd = new Buttongroup(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "camera" -> toAdd = new Camera(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "circle" -> toAdd = new Circle(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "compositegeom" -> toAdd = new Compositegeom(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "detachstrand" -> toAdd = new Detachstrand(_parent);
            case "effects" -> toAdd = new Effects(_parent);
            case "endoncollision" -> toAdd = new Endoncollision(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "endonmessage" -> toAdd = new Endonmessage(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "endonnogeom" -> toAdd = new Endonnogeom(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "fire" -> toAdd = new Fire(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "font" -> toAdd = new Font(_parent == null ? LevelManager.getLevel().getResourcesObject() : null);
            case "hinge" -> toAdd = new Hinge(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "label" -> toAdd = new Label(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "level" -> toAdd = new Level(null);
            case "levelexit" -> toAdd = new Levelexit(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "line" -> toAdd = new Line(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "linearforcefield" -> toAdd = new Linearforcefield(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "loopsound" -> toAdd = new Loopsound(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "marker" -> toAdd = new Marker(_parent);
            case "motor" -> toAdd = new Motor(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "music" -> toAdd = new Music(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "part" -> toAdd = new Part(_parent);
            case "particleeffect" -> toAdd = new Particleeffect(_parent);
            case "particle" -> toAdd = new _Particle(null);
            case "particles" -> toAdd = new Particles(null);
            case "pipe" -> toAdd = new Pipe(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "poi" -> toAdd = new Poi(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "rectangle" -> toAdd = new Rectangle(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "radialforcefield" -> toAdd = new Radialforcefield(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "ResourceManifest" -> toAdd = new ResourceManifest(null);
            case "Resources" -> toAdd = new Resources(_parent);
            case "Image" -> toAdd = new ResrcImage(_parent);
            case "scene" -> toAdd = new Scene(null);
            case "SceneLayer" -> toAdd = new SceneLayer(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "SetDefaults" -> toAdd = new SetDefaults(_parent);
            case "shadow" -> toAdd = new Shadow(_parent);
            case "signpost" -> toAdd = new Signpost(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "sinanim" -> toAdd = new Sinanim(_parent);
            case "sinvariance" -> toAdd = new Sinvariance(_parent);
            case "slider" -> toAdd = new Slider(_parent == null ? LevelManager.getLevel().getSceneObject() : null);
            case "sound", "Sound" -> toAdd = new Sound(_parent);
            case "splat" -> toAdd = new Splat(_parent);
            case "Strand" -> toAdd = new Strand(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "strand" -> toAdd = new BallStrand(_parent);
            case "string" -> toAdd = new TextString(_parent);
            case "strings" -> toAdd = new TextStrings(_parent);
            case "targetheight" -> toAdd = new Targetheight(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            case "Vertex" -> toAdd = new Vertex(_parent == null ? LevelManager.getLevel().getLevelObject() : null);
            default -> logger.error("Could not find object " + _name + " to create");
        }
        if (_parent != null && toAdd != null) {
            toAdd.setParent(_parent);
            if (!_parent.getChildren().contains(toAdd)) {
                _parent.getChildren().add(toAdd);
            }
        }
        toAdd.setTreeItem(new TreeItem<>(toAdd));
        if (toAdd instanceof Vertex vertex){
            vertex.setPrevious(lastVertex);
            lastVertex = vertex;
        }
        // Automatically add to parent
        if (toAdd.getParent() != null) {
            toAdd.getParent().getTreeItem().getChildren().add(toAdd.getTreeItem());
        }
        return toAdd;
    }

}
