package com.woogleFX.gameData.level;

import com.woogleFX.engine.fx.FXEditorButtons;
import com.woogleFX.file.FileManager;
import javafx.scene.image.Image;

public class VisibilitySettings {

    private static final Image showHideAnim0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_anim_disabled.png");
    private static final Image showHideAnim = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_anim.png");
    private boolean showAnimations = true;
    public boolean isShowAnimations() {
        return showAnimations;
    }
    public void setShowAnimations(boolean showAnimations) {
        this.showAnimations = showAnimations;
        FXEditorButtons.animGraphic(showAnimations ? showHideAnim : showHideAnim0);
    }


    private static final Image showHideCam0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_cam_disabled.png");
    private static final Image showHideCam1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_cam.png");
    private boolean showCameras = false;
    public boolean isShowCameras() {
        return showCameras;
    }
    public void setShowCameras(boolean showCameras) {
        this.showCameras = showCameras;
        FXEditorButtons.cameraGraphic(showCameras ? showHideCam1 : showHideCam0);
    }


    private static final Image showHideForcefields0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_forcefields_disabled.png");
    private static final Image showHideForcefields1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_forcefields.png");
    private boolean showForcefields = true;
    public boolean isShowForcefields() {
        return showForcefields;
    }
    public void setShowForcefields(boolean showForcefields) {
        this.showForcefields = showForcefields;
        FXEditorButtons.forcefieldsGraphic(showForcefields ? showHideForcefields1 : showHideForcefields0);
    }


    private static final Image showHideGeometry0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_geometry_disabled.png");
    private static final Image showHideGeometry1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_geometry.png");
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


    private static final Image showHideImages0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_images_disabled.png");
    private static final Image showHideImages1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_images.png");
    private boolean showGraphics = true;
    public boolean isShowGraphics() {
        return showGraphics;
    }
    public void setShowGraphics(boolean showGraphics) {
        this.showGraphics = showGraphics;
        FXEditorButtons.graphicsGraphic(showGraphics ? showHideImages1 : showHideImages0);
    }


    private static final Image showHideGoobs0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs_disabled.png");
    private static final Image showHideGoobs1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs_wireframe.png");
    private static final Image showHideGoobs2 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_goobs.png");
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


    private static final Image showHideParticles0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_particles_disabled.png");
    private static final Image showHideParticles1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_particles.png");
    private boolean showParticles = true;
    public boolean isShowParticles() {
        return showParticles;
    }
    public void setShowParticles(boolean showParticles) {
        this.showParticles = showParticles;
        FXEditorButtons.particlesGraphic(showParticles ? showHideParticles1 : showHideParticles0);
    }


    private static final Image showHideLabels0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_labels_disabled.png");
    private static final Image showHideLabels1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_labels.png");
    private boolean showLabels = true;
    public boolean isShowLabels() {
        return showLabels;
    }
    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
        FXEditorButtons.labelsGraphic(showLabels ? showHideLabels1 : showHideLabels0);
    }


    private static final Image showHideBGColor0 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_scenebgcolor_disabled.png");
    private static final Image showHideBGColor1 = FileManager.getIcon("ButtonIcons\\ShowHide\\showhide_scenebgcolor.png");
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
