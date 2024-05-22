package com.woogleFX.file.resourceManagers.resources;

import com.worldOfGoo.text.TextString;

public class TextResource extends Resource {

    private TextString text;

    public TextString getText() {
        return text;
    }

    public void setText(TextString text) {
        this.text = text;
    }

    public TextResource(String id, TextString text) {
        super(id);
        this.text = text;
    }
}
