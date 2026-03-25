package com.woogleFX.editorObjects;

import com.woogleFX.assets.Asset;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.dataTypes.Position;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.assets.GameVersion;
import com.worldOfGoo2.misc.Point;

public abstract class _2_Positionable extends EditorObject {

    public _2_Positionable(EditorObject parent, GameVersion version) {
        super(parent, version);
        
        addAttributeAdapter("pos", AttributeAdapter.pointAttributeAdapter(this, "pos", "pos"));
    }
    
    @Override
    public void onLoaded(Asset asset) {
        super.onLoaded(asset);
        
        EditorObject pos = getChild("pos");
        setAttribute2("pos", pos.getAttribute("x").stringValue() +
                "," + pos.getAttribute("y").stringValue());
        pos.getAttribute("x").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("pos", newValue + "," + getAttribute2("pos").positionValue().getY()));
        pos.getAttribute("y").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("pos", getAttribute2("pos").positionValue().getX() + "," + newValue));
    }
    
    public void createPosition() {
        EditorObject pos = ObjectCreator.create(Point.class, this, "pos", GameVersion.VERSION_WOG2);
        pos.setAttribute("x", 0);
        pos.setAttribute("y", 0);
    }

    public Position getPosition() {
        EditorObject posObject = getChild("pos");
        return new Position(
            posObject.getAttribute("x").doubleValue(),
            posObject.getAttribute("y").doubleValue()
        );
    }
    
    public void setPosition(double x, double y) {
        setAttribute("pos", x + "," + y);
    }
    
}
