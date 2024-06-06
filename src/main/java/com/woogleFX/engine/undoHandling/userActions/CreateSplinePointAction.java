package com.woogleFX.engine.undoHandling.userActions;

public class CreateSplinePointAction extends UserAction {

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


    public CreateSplinePointAction(double x, double y, int index) {
        super(null);
        this.x = x;
        this.y = y;
        this.index = index;
    }


    @Override
    public UserAction getInverse() {
        return new DeleteSplinePointAction(x, y, index);
    }


    @Override
    public void execute() {



    }

}
