package com.woogleFX.editorObjects.attributes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class MetaEditorAttribute {

    @JacksonXmlElementWrapper(localName = "name")
    private String name;
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    public String getName() {
        return name;
    }
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    public void setName(String name) {
        this.name = name;
    }

    private final StringProperty description = new SimpleStringProperty("");
    @JacksonXmlProperty(localName = "description", isAttribute = true)
    public String getDescription() {
        return description.getValue();
    }
    @JacksonXmlProperty(localName = "description", isAttribute = true)
    public void setDescription(String description) {
        this.description.setValue(description);
    }

    @JacksonXmlElementWrapper(localName = "children", useWrapping = false)
    @JacksonXmlProperty(localName = "MetaEditorAttribute")
    private ArrayList<MetaEditorAttribute> children = new ArrayList<>();
    @JacksonXmlProperty(localName = "MetaEditorAttribute")
    public ArrayList<MetaEditorAttribute> getChildren() {
        return children;
    }
    @JacksonXmlProperty(localName = "MetaEditorAttribute")
    public void setChildren(ArrayList<MetaEditorAttribute> children) {
        this.children = children;
    }

    @JacksonXmlElementWrapper(localName = "openByDefault")
    private boolean openByDefault;
    @JacksonXmlProperty(localName = "openByDefault", isAttribute = true)
    public boolean getOpenByDefault() {
        return openByDefault;
    }
    @JacksonXmlProperty(localName = "openByDefault", isAttribute = true)
    public void setOpenByDefault(boolean openByDefault) {
        this.openByDefault = openByDefault;
    }

    public MetaEditorAttribute(String name, boolean openByDefault) {
        this.name = name;
        this.openByDefault = openByDefault;
    }

    public MetaEditorAttribute() {

    }










    //a<b,c,d>e<f,g,h>i<k>,k<l>
    public static ArrayList<MetaEditorAttribute> parse(String str) {

        boolean parsingChildren = false;
        ArrayList<MetaEditorAttribute> output = new ArrayList<>();
        MetaEditorAttribute currentAttributeObject = null;
        StringBuilder currentAttribute = new StringBuilder();
        StringBuilder currentChildAttribute = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {

            char c = str.charAt(i);

            if (c == '<') {
                parsingChildren = true;
                if (currentAttribute.charAt(0) == '?') {
                    currentAttributeObject = new MetaEditorAttribute(currentAttribute.substring(1), false);
                } else {
                    currentAttributeObject = new MetaEditorAttribute(currentAttribute.toString(), true);
                }
                currentAttribute = new StringBuilder();
            } else if (c == ',') {
                if (parsingChildren) {
                    currentAttributeObject.getChildren().add(new MetaEditorAttribute(currentChildAttribute.toString(), false));
                    currentChildAttribute = new StringBuilder();
                } else {
                    output.add(new MetaEditorAttribute(currentAttribute.toString(), false));
                    currentAttribute = new StringBuilder();
                }
            } else if (c == '>') {
                if (currentAttributeObject != null) {
                    currentAttributeObject.getChildren().add(new MetaEditorAttribute(currentChildAttribute.toString(), false));
                }
                currentChildAttribute = new StringBuilder();
                output.add(currentAttributeObject);
                currentAttributeObject = null;
                parsingChildren = false;
            } else {
                if (parsingChildren) {
                    currentChildAttribute.append(c);
                } else {
                    currentAttribute.append(c);
                }
            }

        }
        return output;
    }
}
