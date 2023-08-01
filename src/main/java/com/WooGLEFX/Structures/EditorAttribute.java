package com.WooGLEFX.Structures;

import com.WooGLEFX.Structures.InputField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

public class EditorAttribute {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty value = new SimpleStringProperty("");
    private final String defaultValue;
    private final InputField input;

    private final boolean requiredInFile;

    public static final EditorAttribute NULL = new EditorAttribute("", "", "", new InputField("", InputField.NULL), false);

    public boolean getRequiredInFile() {
        return requiredInFile;
    }

    public StringProperty valueProperty() {
        return value;
    }
    public StringProperty nameProperty() {
        return name;
    }

    public void setChangeListener(ChangeListener<String> changeListener) {
        this.value.addListener(changeListener);
    }

    public String getName() {
        return name.getValue();
    }

    public String getValue() {
        return value.getValue();
    }

    public void setValue(String value) {
        this.value.setValue(value);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public InputField getInput() {
        return input;
    }


    public EditorAttribute(String name, String defaultValue, String initialValue, InputField inputField, boolean requiredInFile) {
        this.name.setValue(name);
        this.defaultValue = defaultValue;
        this.value.setValue(initialValue);
        this.input = inputField;
        this.requiredInFile = requiredInFile;
    }
}
