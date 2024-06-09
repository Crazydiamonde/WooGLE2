package com.woogleFX.engine.undoHandling.userActions;

import com.woogleFX.editorObjects.splineGeom.SplineManager;

public class MoveSplinePointAction extends UserAction {

    private final double fromX;
    private final double fromY;
    private final double toX;
    private final double toY;
    private final int index;
    public MoveSplinePointAction(double fromX, double fromY, double toX, double toY, int index) {
        super(null);
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.index = index;
    }


    @Override
    public UserAction getInverse() {
        return new MoveSplinePointAction(toX, toY, fromX, fromY, index);
    }


    @Override
    public void execute() {
        SplineManager.setSplinePoint(index, toX, toY);
    }

}
