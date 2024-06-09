package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.splineGeom.SplineManager;
import com.woogleFX.engine.SelectionManager;

public class DeleteSplinePointAction extends UserAction {


    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;
    private final double x3;
    private final double y3;
    private final int index;
    public DeleteSplinePointAction(double x1, double y1, double x2, double y2, double x3, double y3, int index) {
        super(null);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.index = index;
    }


    @Override
    public UserAction getInverse() {
        return new CreateSplinePointAction(x1, y1, x2, y2, x3, y3, index);
    }


    @Override
    public void execute() {
        SplineManager.removePoint(index);
        SelectionManager.geometryMode();
    }

}
