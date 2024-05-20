module com.example.WOGAnniversaryEditor {
  requires transitive javafx.controls;
  requires java.desktop;
  requires javafx.fxml;
  requires javafx.swing;
  requires transitive javafx.graphics;
  requires org.bouncycastle.provider;
  requires java.xml;
  requires org.slf4j;
  requires ch.qos.logback.classic;
  requires ch.qos.logback.core;

  exports com.WorldOfGoo.Scene;
  exports com.WorldOfGoo.Level;
  exports com.WorldOfGoo.Resrc;
  exports com.WorldOfGoo.Ball;
  exports com.WorldOfGoo.Particle;
  exports com.WorldOfGoo.Text;
  exports com.WooGLEFX.GUI;
  exports com.WooGLEFX.Structures;
  exports com.WooGLEFX.File;
  exports com.WooGLEFX.EditorObjects;
  exports com.WooGLEFX.Structures.UserActions;
  exports com.WooGLEFX.Structures.SimpleStructures;
  exports com.WooGLEFX.Structures.Resources;
  exports com.WooGLEFX.Engine;
  exports com.WooGLEFX.Engine.FX;
  exports com.WooGLEFX.EditorObjects.Components;
    exports com.WooGLEFX.EditorObjects.ObjectCollision;
    exports com.WooGLEFX.File.export;

}
