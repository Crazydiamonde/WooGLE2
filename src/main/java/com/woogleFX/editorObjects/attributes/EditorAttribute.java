package com.woogleFX.editorObjects.attributes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.dataTypes.Color;
import com.woogleFX.editorObjects.attributes.dataTypes.Position;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo.resrc.Resources;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;

public class EditorAttribute {

    public static final EditorAttribute NULL = new EditorAttribute(null, null, null);


    @JsonIgnore
    private EditorObject object;
    public EditorObject getObject() {
        return object;
    }
    public void setObject(EditorObject object) {
        this.object = object;
    }


    private final StringProperty name = new SimpleStringProperty();
    public StringProperty getNameProperty() {
        return name;
    }
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    public String getName() {
        return name.getValue();
    }
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    public void setName(String name) {
        this.name.setValue(name);
    }


    private String description = "";
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    private final StringProperty value = new SimpleStringProperty("");
    public StringProperty getValueProperty() {
        return value;
    }

    public String actualValue() {
        return value.getValue();
    }

    public String stringValue() {
        if (value.getValue().isEmpty()) return defaultValue == null ? "" : defaultValue;
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

    public Image imageValue(Resources resources, GameVersion version) {
        return ResourceManager.getImage(resources, stringValue(), version);
    }

    public String[] listValue() {
        if (stringValue().isEmpty()) return new String[0];
        return stringValue().split(",");
    }

    public void setValue(String value) {
        this.value.setValue(value);
    }


    public void addChangeListener(ChangeListener<String> changeListener) {
        this.value.addListener(changeListener);
    }


    private String defaultValue = null;
    public String getDefaultValue() {
        return defaultValue;
    }
    public EditorAttribute setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }


    private InputField type;
    public InputField getType() {
        return type;
    }

    public void setType(InputField type) {
        this.type = type;
    }
    public void setType(String type) {
        this.type = InputField.valueOf(type);
    }

    private boolean required;
    public boolean getRequired() {
        return required;
    }
    public EditorAttribute assertRequired() {
        required = true;
        return this;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    private Class<? extends EditorObject> childAlias = null;
    public Class<? extends EditorObject> getChildAlias() {
        return childAlias;
    }
    public EditorAttribute setChildAlias(Class<? extends EditorObject> childAlias) {
        this.childAlias = childAlias;
        return this;
    }
    @SuppressWarnings("unchecked")
    public EditorAttribute setChildAlias(String childAlias) {
        try {
            Object object = Class.forName(childAlias);
            this.childAlias = (Class<? extends EditorObject>) object;
        } catch (ClassNotFoundException e) {
            return this;
        }
        return this;
    }


    private Runnable onClick = null;
    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
    public void onClick() {
        if (onClick != null) onClick.run();
    }


    public EditorAttribute(String name, InputField type, EditorObject object) {
        this.name.setValue(name);
        this.type = type;
        this.object = object;
    }

    public EditorAttribute() {
        this.object = null;
    }

}
