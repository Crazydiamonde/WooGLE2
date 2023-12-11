module com.example.WOGAnniversaryEditor {
  requires transitive javafx.controls;
  requires java.desktop;
  requires annotations;
  requires javafx.swing;
  requires org.bouncycastle.provider;

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
}
