package org.waynezhou.androidplayground.layout;

private interface LayoutExecutor{
    void layout(LayoutExecutionArgs args);
}
private static class LayoutTemplate{

}
private static class LayoutExecutionArgs{
    public final boolean withAnimation;
    public final int duration;
    public final Runnable postAction;



    private LayoutExecutionArgs(boolean withAnimation, int duration, Runnable postAction) {
        this.withAnimation = withAnimation;
        this.duration = duration;
        this.postAction = postAction;
    }
}