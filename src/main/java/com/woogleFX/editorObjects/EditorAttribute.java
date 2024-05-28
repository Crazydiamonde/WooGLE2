package com.woogleFX.editorObjects;

import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.simpleStructures.Color;
import com.woogleFX.structures.simpleStructures.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class EditorAttribute {

    public static final EditorAttribute NULL = new EditorAttribute(null, null);


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
        try {
            return Double.parseDouble(stringValue());
        } catch (NumberFormatException e) {
            return 0;
        }
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

    public Image imageValue(ArrayList<EditorObject> resources, GameVersion version) throws FileNotFoundException {
        return ResourceManager.getImage(resources, stringValue(), version);
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


    private final InputField type;
    public InputField getType() {
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


    public EditorAttribute(String name, InputField type) {
        this.name.setValue(name);
        this.type = type;
    }

}
