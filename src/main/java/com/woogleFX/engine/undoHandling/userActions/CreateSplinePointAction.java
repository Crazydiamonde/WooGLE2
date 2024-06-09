package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.splineGeom.SplineManager;
import com.woogleFX.engine.SelectionManager;

public class CreateSplinePointAction extends UserAction {

    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;
    private final double x3;
    private final double y3;
    private final int splineSegmentIndex;
    public CreateSplinePointAction(double x1, double y1, double x2, double y2, double x3, double y3, int splineSegmentIndex) {
        super(null);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.splineSegmentIndex = splineSegmentIndex;
    }


    @Override
    public UserAction getInverse() {
        return new DeleteSplinePointAction(x1, y1, x2, y2, x3, y3, splineSegmentIndex);
    }


    @Override
    public void execute() {
        SplineManager.addPoint(x1, y1, x2, y2, x3, y3, splineSegmentIndex);
        SelectionManager.geometryMode();
    }

}
