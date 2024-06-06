package com.woogleFX.engine.undoHandling.userActions;

public class MoveSplinePointAction extends UserAction {

    private final double fromX;
    public double getFromX() {
        return fromX;
    }


    private final double fromY;
    public double getFromY() {
        return fromY;
    }


    private final double toX;
    public double getToX() {
        return toX;
    }


    private final double toY;
    public double getToY() {
        return toY;
    }


    private final double index;
    public double getIndex() {
        return index;
    }


    public MoveSplinePointAction(double fromX, double fromY, double toX, double toY, double index) {
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

    }

}
