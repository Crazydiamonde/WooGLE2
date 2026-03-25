package com.woogleFX.engine.undoHandling;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.engine.undoHandling.userActions.UserAction;

public record UndoState(EditorObject[] selectedObjects, ObjectComponent[][] objectComponents, ObjectComponent[] selectedComponents, UserAction[] userActions) {
}
