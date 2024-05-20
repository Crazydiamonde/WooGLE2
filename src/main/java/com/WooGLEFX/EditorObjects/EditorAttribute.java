package com.WooGLEFX.EditorObjects;

import com.WooGLEFX.File.ResourceManagers.GlobalResourceManager;
import com.WooGLEFX.Structures.GameVersion;
import com.WooGLEFX.Structures.SimpleStructures.Color;
import com.WooGLEFX.Structures.SimpleStructures.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

public class EditorAttribute {

    public static final EditorAttribute NULL = new EditorAttribute(null, InputField.NULL);


    private final StringProperty name = new SimpleStringProperty();
    public StringProperty getNameProperty() {
        return name;
    }
    public String getName() {
        return name.getValue();
    }


    private final StringProperty value = new SimpleStringProperty("");
    public StringProperty getValueProperty() {
        return value;
    }

    public String actualValue() {
        return value.getValue();
    }

    public String stringValue() {
        if (value.getValue().isEmpty()) return defaultValue;
        else return value.getValue();
    }

    public double doubleValue() {
        return Double.parseDouble(stringValue());
    }

    public int intValue() {
        return Integer.parseInt(stringValue());
    }

    public boolean booleanValue() {
        return (stringValue().equals("true"));
    }

    public Position positionValue() {
        return Position.parse(stringValue());
    }

    public Color colorValue() {
        return Color.parse(stringValue());
    }

    public Image imageValue(GameVersion version) throws FileNotFoundException {
        return GlobalResourceManager.getImage(stringValue(), version);
    }

    public String[] listValue() {
        return stringValue().split(",");
    }

    public void setValue(String value) {
        this.value.setValue(value);
    }


    public void addChangeListener(ChangeListener<String> changeListener) {
        this.value.addListener(changeListener);
    }


    private String defaultValue = "";
    public String getDefaultValue() {
        return defaultValue;
    }
    public EditorAttribute setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }


    private final int type;
    public int getType() {
        return type;
    }


    private boolean requiredInFile;
    public boolean getRequiredInFile() {
        return requiredInFile;
    }
    public EditorAttribute assertRequired() {
        requiredInFile = true;
        return this;
    }


    public EditorAttribute(String name, int type) {
        this.name.setValue(name);
        this.type = type;
    }

}
