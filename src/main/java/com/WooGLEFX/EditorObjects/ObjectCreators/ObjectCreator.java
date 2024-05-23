package com.WooGLEFX.EditorObjects.ObjectCreators;

import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.EditorObjects.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Addin.*;
import com.WorldOfGoo.Ball.*;
import com.WorldOfGoo.Level.*;
import com.WorldOfGoo.Particle.*;
import com.WorldOfGoo.Resrc.*;
import com.WorldOfGoo.Scene.*;
import com.WorldOfGoo.Text.TextString;
import com.WorldOfGoo.Text.TextStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectCreator {

    private static final Logger logger = LoggerFactory.getLogger(ObjectCreator.class);


    public static EditorObject create(String _name, EditorObject _parent) {

        WorldLevel level = LevelManager.getLevel();

        EditorObject parent = (_parent != null || level == null) ? _parent : switch(_name) {

            case "linearforcefield", "radialforcefield", "particles",
                    "SceneLayer", "buttongroup", "button", "circle",
                    "rectangle", "hinge", "compositegeom", "label",
                    "line", "motor", "slider" -> level.getSceneObject();

            case "BallInstance", "camera", "endoncollision",
                    "endonmessage", "endonnogeom", "fire", "levelexit",
                    "loopsound", "music", "pipe", "poi", "signpost",
                    "Strand", "targetheight" -> level.getLevelObject();

            case "font" -> level.getResourcesObject();

            default -> null;

        };

        EditorObject toAdd = switch (_name) {
            case "addin", "Addin_addin" -> new Addin(parent);
            case "Addin_id" -> new AddinID(parent);
            case "Addin_name" -> new AddinName(parent);
            case "Addin_type" -> new AddinType(parent);
            case "Addin_version" -> new AddinVersion(parent);
            case "Addin_description" -> new AddinDescription(parent);
            case "Addin_author" -> new AddinAuthor(parent);
            case "Addin_levels" -> new AddinLevels(parent);
            case "Addin_level" -> new AddinLevel(parent);
            case "Addin_dir" -> new AddinLevelDir(parent);
            case "Addin_wtf_name" -> new AddinLevelName(parent);
            case "Addin_subtitle" -> new AddinLevelSubtitle(parent);
            case "Addin_ocd" -> new AddinLevelOCD(parent);
            case "ambientparticleeffect" -> new Ambientparticleeffect(parent);
            case "axialsinoffset" -> new Axialsinoffset(parent);
            case "ball" -> new Ball(parent);
            case "BallInstance" -> new BallInstance(parent);
            case "ball_particles" -> new BallParticles(parent);
            case "ball_sound" -> new BallSound(parent);
            case "button" -> new Button(parent);
            case "buttongroup" -> new Buttongroup(parent);
            case "camera" -> new Camera(parent);
            case "circle" -> new Circle(parent);
            case "compositegeom" -> new Compositegeom(parent);
            case "detachstrand" -> new Detachstrand(parent);
            case "effects" -> new Effects(parent);
            case "endoncollision" -> new Endoncollision(parent);
            case "endonmessage" -> new Endonmessage(parent);
            case "endonnogeom" -> new Endonnogeom(parent);
            case "fire" -> new Fire(parent);
            case "font" -> new Font(parent);
            case "hinge" -> new Hinge(parent);
            case "label" -> new Label(parent);
            case "level" -> new Level(parent);
            case "levelexit" -> new Levelexit(parent);
            case "line" -> new Line(parent);
            case "linearforcefield" -> new Linearforcefield(parent);
            case "loopsound" -> new Loopsound(parent);
            case "marker" -> new Marker(parent);
            case "motor" -> new Motor(parent);
            case "music" -> new Music(parent);
            case "part" -> new Part(parent);
            case "particleeffect" -> new Particleeffect(parent);
            case "particle" -> new _Particle(parent);
            case "particles" -> new Particles(parent);
            case "pipe" -> new Pipe(parent);
            case "poi" -> new Poi(parent);
            case "rectangle" -> new Rectangle(parent);
            case "radialforcefield" -> new Radialforcefield(parent);
            case "ResourceManifest" -> new ResourceManifest(parent);
            case "Resources" -> new Resources(parent);
            case "Image" -> new ResrcImage(parent);
            case "scene" -> new Scene(parent);
            case "SceneLayer" -> new SceneLayer(parent);
            case "SetDefaults" -> new SetDefaults(parent);
            case "shadow" -> new Shadow(parent);
            case "signpost" -> new Signpost(parent);
            case "sinanim" -> new Sinanim(parent);
            case "sinvariance" -> new Sinvariance(parent);
            case "slider" -> new Slider(parent);
            case "sound", "Sound" -> new Sound(parent);
            case "splat" -> new Splat(parent);
            case "Strand" -> new Strand(parent);
            case "strand" -> new BallStrand(parent);
            case "string" -> new TextString(parent);
            case "strings" -> new TextStrings(parent);
            case "targetheight" -> new Targetheight(parent);
            case "Vertex" -> new Vertex(parent);
            default -> null;
        };

        if (toAdd == null) {
            logger.error("Attempted to create an invalid object: \"" + _name + "\"");
            return null;
        }

        if (parent != null) toAdd.setParent(parent);

        return toAdd;

    }

}
