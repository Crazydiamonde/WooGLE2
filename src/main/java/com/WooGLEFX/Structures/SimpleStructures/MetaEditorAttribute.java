package com.WooGLEFX.Structures.SimpleStructures;

import java.util.ArrayList;

public class MetaEditorAttribute {

    private final String name;
    private final MetaEditorAttribute parent;
    private final ArrayList<MetaEditorAttribute> children = new ArrayList<>();

    private final boolean hasAttributes;
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

    public boolean getHasAttributes() {
        return hasAttributes;
    }

    public boolean getOpenByDefault() {
        return openByDefault;
    }

    public MetaEditorAttribute(String name, MetaEditorAttribute parent, boolean hasAttributes, boolean openByDefault) {
        this.name = name;
        this.parent = parent;
        this.hasAttributes = hasAttributes;
        this.openByDefault = openByDefault;
    }

    static boolean parsingChildren = false;
    static ArrayList<MetaEditorAttribute> output = new ArrayList<>();
    static String currentAttribute = "";
    static MetaEditorAttribute currentAttributeObject = null;
    static String currentChildAttribute = "";

    //a<b,c,d>e<f,g,h>i<k>,k<l>
    public static ArrayList<MetaEditorAttribute> parse(String str) {

        output = new ArrayList<>();

        parsingChildren = false;

        for (int i = 0; i < str.length(); i++) {

            char c = str.charAt(i);

            if (c == '<') {
                parsingChildren = true;
                if (currentAttribute.charAt(0) == '?') {
                    currentAttributeObject = new MetaEditorAttribute(currentAttribute.substring(1), null, false, false);
                } else {
                    currentAttributeObject = new MetaEditorAttribute(currentAttribute, null, false, true);
                }
                currentAttribute = "";
            } else if (c == ',') {
                if (parsingChildren) {
                    currentAttributeObject.getChildren().add(new MetaEditorAttribute(currentChildAttribute, currentAttributeObject, true, false));
                    currentChildAttribute = "";
                } else {
                    output.add(new MetaEditorAttribute(currentAttribute, null, true, false));
                    currentAttribute = "";
                }
            } else if (c == '>') {
                currentAttributeObject.getChildren().add(new MetaEditorAttribute(currentChildAttribute, currentAttributeObject, true, false));
                currentChildAttribute = "";
                output.add(currentAttributeObject);
                currentAttributeObject = null;
                parsingChildren = false;
            } else {
                if (parsingChildren) {
                    currentChildAttribute += c;
                } else {
                    currentAttribute += c;
                }
            }

        }
        return output;
    }
}
