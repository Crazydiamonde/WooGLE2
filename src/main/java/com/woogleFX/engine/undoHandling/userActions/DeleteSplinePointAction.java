package com.woogleFX.engine.undoHandling.userActions;

public class DeleteSplinePointAction extends UserAction {

    private final double x;
    public double getX() {
        return x;
    }


    private final double y;
    public double getY() {
        return y;
    }


    private final int index;
    public int getIndex() {
        return index;
    }


    public DeleteSplinePointAction(double x, double y, int index) {
        super(null);
        this.x = x;
        this.y = y;
        this.index = index;
    }


    @Override
    public UserAction getInverse() {
        return new CreateSplinePointAction(x, y, index);
    }


    @Override
    public void execute() {

    }

}
