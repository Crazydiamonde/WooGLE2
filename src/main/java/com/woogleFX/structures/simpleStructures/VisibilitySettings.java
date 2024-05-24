package com.woogleFX.structures.simpleStructures;

import com.woogleFX.engine.fx.FXEditorButtons;
import com.woogleFX.file.FileManager;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

public class VisibilitySettings {

    private static Image showHideAnim0;
    private static Image showHideAnim;
    private static Image showHideCam0;
    private static Image showHideCam1;
    private static Image showHideForcefields0;
    private static Image showHideForcefields1;
    private static Image showHideGeometry0;
    private static Image showHideGeometry1;
    private static Image showHideGoobs0;
    private static Image showHideGoobs1;
    private static Image showHideGoobs2;
    private static Image showHideImages0;
    private static Image showHideImages1;
    private static Image showHideLabels0;
    private static Image showHideLabels1;
    private static Image showHideParticles0;
    private static Image showHideParticles1;
    private static Image showHideBGColor0;
    private static Image showHideBGColor1;

    static {
        try {
            showHideAnim0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_anim_disabled.png");
            showHideAnim = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_anim.png");
            showHideCam0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_cam_disabled.png");
            showHideCam1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_cam.png");
            showHideForcefields0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_forcefields_disabled.png");
            showHideForcefields1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_forcefields.png");
            showHideGeometry0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_geometry_disabled.png");
            showHideGeometry1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_geometry.png");
            showHideGoobs0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs_disabled.png");
            showHideGoobs1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs_wireframe.png");
            showHideGoobs2 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs.png");
            showHideImages0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_images_disabled.png");
            showHideImages1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_images.png");
            showHideLabels0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_labels_disabled.png");
            showHideLabels1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_labels.png");
            showHideParticles0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_particles_disabled.png");
            showHideParticles1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_particles.png");
            showHideBGColor0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_scenebgcolor_disabled.png");
            showHideBGColor1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_scenebgcolor.png");
        } catch (FileNotFoundException ignored) {

        }
    }

    private boolean showCameras = false;
    public boolean isShowCameras() {
        return showCameras;
    }
    public void setShowCameras(boolean showCameras) {
        this.showCameras = showCameras;
        FXEditorButtons.cameraGraphic(showCameras ? showHideCam1 : showHideCam0);
    }

    private boolean showForcefields = true;
    public boolean isShowForcefields() {
        return showForcefields;
    }
    public void setShowForcefields(boolean showForcefields) {
        this.showForcefields = showForcefields;
        FXEditorButtons.forcefieldsGraphic(showForcefields ? showHideForcefields1 : showHideForcefields0);
    }

    private int showGeometry = 2;
    public int getShowGeometry() {
        return showGeometry;
    }
    public void setShowGeometry(int showGeometry) {
        this.showGeometry = showGeometry;
        switch (showGeometry) {
            case 0 -> FXEditorButtons.geometryGraphic(showHideGeometry0);
            case 1 -> FXEditorButtons.geometryGraphic(showHideGeometry1);
            case 2 -> FXEditorButtons.geometryGraphic(showHideImages1);
        }
    }

    private boolean showGraphics = true;
    public boolean isShowGraphics() {
        return showGraphics;
    }
    public void setShowGraphics(boolean showGraphics) {
        this.showGraphics = showGraphics;
        FXEditorButtons.graphicsGraphic(showGraphics ? showHideImages1 : showHideImages0);
    }

    private int showGoos = 2;
    public int getShowGoos() {
        return showGoos;
    }
    public void setShowGoos(int showGoos) {
        this.showGoos = showGoos;
        switch (showGoos) {
            case 0 -> FXEditorButtons.goosGraphic(showHideGoobs0);
            case 1 -> FXEditorButtons.goosGraphic(showHideGoobs1);
            case 2 -> FXEditorButtons.goosGraphic(showHideGoobs2);
        }
    }

    private boolean showParticles = true;
    public boolean isShowParticles() {
        return showParticles;
    }
    public void setShowParticles(boolean showParticles) {
        this.showParticles = showParticles;
        FXEditorButtons.particlesGraphic(showParticles ? showHideParticles1 : showHideParticles0);
    }

    private boolean showLabels = true;
    public boolean isShowLabels() {
        return showLabels;
    }
    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
        FXEditorButtons.labelsGraphic(showLabels ? showHideLabels1 : showHideLabels0);
    }

    private boolean showAnimations = true;
    public boolean isShowAnimations() {
        return showAnimations;
    }
    public void setShowAnimations(boolean showAnimations) {
        this.showAnimations = showAnimations;
        FXEditorButtons.animGraphic(showAnimations ? showHideAnim : showHideAnim0);
    }

    private boolean showSceneBGColor = false;
    public boolean isShowSceneBGColor() {
        return showSceneBGColor;
    }
    public void setShowSceneBGColor(boolean showSceneBGColor) {
        this.showSceneBGColor = showSceneBGColor;
        FXEditorButtons.sceneBGGraphic(showSceneBGColor ? showHideBGColor1 : showHideBGColor0);
    }


    public void updateButtons() {
        FXEditorButtons.cameraGraphic(showCameras ? showHideCam1 : showHideCam0);
        FXEditorButtons.forcefieldsGraphic(showForcefields ? showHideForcefields1 : showHideForcefields0);
        switch (showGeometry) {
            case 0 -> FXEditorButtons.geometryGraphic(showHideGeometry0);
            case 1 -> FXEditorButtons.geometryGraphic(showHideGeometry1);
            case 2 -> FXEditorButtons.geometryGraphic(showHideImages1);
        }
        FXEditorButtons.graphicsGraphic(showGraphics ? showHideImages1 : showHideImages0);
        switch (showGoos) {
            case 0 -> FXEditorButtons.goosGraphic(showHideGoobs0);
            case 1 -> FXEditorButtons.goosGraphic(showHideGoobs1);
            case 2 -> FXEditorButtons.goosGraphic(showHideGoobs2);
        }
        FXEditorButtons.particlesGraphic(showParticles ? showHideParticles1 : showHideParticles0);
        FXEditorButtons.labelsGraphic(showLabels ? showHideLabels1 : showHideLabels0);
        FXEditorButtons.animGraphic(showAnimations ? showHideAnim : showHideAnim0);
        FXEditorButtons.sceneBGGraphic(showSceneBGColor ? showHideBGColor1 : showHideBGColor0);
    }

}
