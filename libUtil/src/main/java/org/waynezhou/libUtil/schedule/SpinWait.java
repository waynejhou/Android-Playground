package org.waynezhou.libUtil.schedule;


public class SpinWait {
    private static final int step = 10;
    private static final int maxTime = 10;
    private static final boolean isSingleCpu = Runtime.getRuntime().availableProcessors() == 1;

    public static <T extends Exception> void spinUntil(Condition<T> condition) throws T {
        SpinWait sw = new SpinWait();
        while (!condition.isPass()) {
            sw.spinOnce();
        }
    }

    private int time = 0;

    private void spinOnce() {
        time += 1;
        if (nextSpinWillYield()) {
            Thread.yield();
        } else {
            spin(Math.max(time, maxTime) * 2);
        }
    }

    private void reset() {
        time = 0;
    }

    private boolean nextSpinWillYield() {
        return isSingleCpu || time % step == 0;
    }

    private int count() {
        return time;
    }

    private void spin(int iterations) {
        if (iterations < 0) return;
        int iter = iterations;
        while (iter-- > 0) {
            assert (true);
        }
    }

    @FunctionalInterface
    public interface Condition<T extends Exception>{
        boolean isPass() throws T;
    }
}


