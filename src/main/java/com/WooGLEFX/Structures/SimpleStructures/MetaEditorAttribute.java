package com.WooGLEFX.Structures.SimpleStructures;

import java.util.ArrayList;

public class MetaEditorAttribute {

    private final String name;
    private final MetaEditorAttribute parent;
    private final ArrayList<MetaEditorAttribute> children = new ArrayList<>();

    private final boolean openByDefault;

    public String getName() {
        return name;
    }

    public MetaEditorAttribute getParent() {
        return parent;
    }

    public ArrayList<MetaEditorAttribute> getChildren() {
        return children;
    }

    public boolean getOpenByDefault() {
        return openByDefault;
    }

    public MetaEditorAttribute(String name, MetaEditorAttribute parent, boolean hasAttributes, boolean openByDefault) {
        this.name = name;
        this.parent = parent;
        this.openByDefault = openByDefault;
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
                    currentAttributeObject = new MetaEditorAttribute(currentAttribute.substring(1), null, false, false);
                } else {
                    currentAttributeObject = new MetaEditorAttribute(currentAttribute.toString(), null, false, true);
                }
                currentAttribute = new StringBuilder();
            } else if (c == ',') {
                if (parsingChildren) {
                    currentAttributeObject.getChildren().add(new MetaEditorAttribute(currentChildAttribute.toString(), currentAttributeObject, true, false));
                    currentChildAttribute = new StringBuilder();
                } else {
                    output.add(new MetaEditorAttribute(currentAttribute.toString(), null, true, false));
                    currentAttribute = new StringBuilder();
                }
            } else if (c == '>') {
                if (currentAttributeObject != null) {
                    currentAttributeObject.getChildren().add(new MetaEditorAttribute(currentChildAttribute.toString(), currentAttributeObject, true, false));
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
