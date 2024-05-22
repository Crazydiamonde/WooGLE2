module com.example.WOGAnniversaryEditor {
  requires transitive javafx.controls;
  requires java.desktop;
  requires javafx.swing;
  requires transitive javafx.graphics;
  requires org.bouncycastle.provider;
  requires java.xml;
  requires org.slf4j;

  exports com.WorldOfGoo.Scene;
  exports com.WorldOfGoo.Level;
  exports com.WorldOfGoo.Resrc;
  exports com.WorldOfGoo.Ball;
  exports com.WorldOfGoo.Particle;
  exports com.WorldOfGoo.Text;
  exports com.WooGLEFX.Engine.GUI;
  exports com.WooGLEFX.Structures;
  exports com.WooGLEFX.File;
  exports com.WooGLEFX.EditorObjects;
  exports com.WooGLEFX.Functions.UndoHandling.UserActions;
  exports com.WooGLEFX.Structures.SimpleStructures;
  exports com.WooGLEFX.File.ResourceManagers.Resources;
  exports com.WooGLEFX.Engine;
  exports com.WooGLEFX.Engine.FX;
    exports com.WooGLEFX.File.fileexport;
  exports com.WooGLEFX.EditorObjects.ObjectCreators;
  exports com.WooGLEFX.File.fileimport;
  exports com.WooGLEFX.File.ResourceManagers;
    exports com.WooGLEFX.EditorObjects.objectcomponents;

}
