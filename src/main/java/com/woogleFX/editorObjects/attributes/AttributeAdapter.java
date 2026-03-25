package com.woogleFX.editorObjects.attributes;

import com.woogleFX.editorObjects.EditorObject;

public abstract class AttributeAdapter {

    public final String name;
    public AttributeAdapter(String name) {
        this.name = name;
    }


    public abstract EditorAttribute getValue();

    public abstract void setValue(String value);


    public static AttributeAdapter pointAttributeAdapter(EditorObject object, String realName, String displayName) {

        return new AttributeAdapter(displayName) {

            @Override
            public EditorAttribute getValue() {
                return object.getAttribute2(realName);
            }

            @Override
            public void setValue(String value) {
                EditorObject pos = object.getChildren(realName).get(0);
                String x = value.substring(0, value.indexOf(","));
                String y = value.substring(value.indexOf(",") + 1);
                pos.setAttribute("x", x);
                pos.setAttribute("y", y);
            }

        };

    }


    public static AttributeAdapter childAttributeAdapter(EditorObject object, String realName, String displayName, InputField type) {

        return new AttributeAdapter(displayName) {

            private final EditorAttribute editorAttribute = new EditorAttribute(displayName, type, object);

            @Override
            public EditorAttribute getValue() {
                if (!object.getChildren(realName).isEmpty())
                    editorAttribute.setValue(object.getChildren(realName).get(0).getAttributes()[0].stringValue());
                return editorAttribute;
            }

            @Override
            public void setValue(String value) {
                object.getChildren(realName).get(0).getAttributes()[0].setValue(value);
                editorAttribute.setValue(value);
            }

        };

    }

}
