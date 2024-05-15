package com.WooGLEFX.Structures;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

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
    public String getValue() {
        return value.getValue();
    }
    public void setValue(String value) {
        this.value.setValue(value);
    }
    public void setChangeListener(ChangeListener<String> changeListener) {
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
    public void assertRequired() {
        requiredInFile = true;
    }


    public EditorAttribute(String name, int type) {
        this.name.setValue(name);
        this.type = type;
    }

}
