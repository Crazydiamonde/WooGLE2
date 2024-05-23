module com.example.WOGAnniversaryEditor {
  requires transitive javafx.controls;
  requires java.desktop;
  requires javafx.swing;
  requires transitive javafx.graphics;
  requires org.bouncycastle.provider;
  requires java.xml;
  requires org.slf4j;

  exports com.worldOfGoo.scene;
  exports com.worldOfGoo.level;
  exports com.worldOfGoo.resrc;
  exports com.worldOfGoo.ball;
  exports com.worldOfGoo.particle;
  exports com.worldOfGoo.text;
  exports com.woogleFX.engine.gui;
  exports com.woogleFX.structures;
  exports com.woogleFX.file;
  exports com.woogleFX.editorObjects;
  exports com.woogleFX.functions.undoHandling.userActions;
  exports com.woogleFX.structures.simpleStructures;
  exports com.woogleFX.file.resourceManagers.resources;
  exports com.woogleFX.engine;
  exports com.woogleFX.engine.fx;
  exports com.woogleFX.file.fileExport;
  exports com.woogleFX.editorObjects.objectCreators;
  exports com.woogleFX.file.fileImport;
  exports com.woogleFX.file.resourceManagers;
  exports com.woogleFX.editorObjects.objectComponents;

}
