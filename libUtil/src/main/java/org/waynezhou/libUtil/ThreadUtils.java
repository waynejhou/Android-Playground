package org.waynezhou.libUtil;

public final class ThreadUtils {
    private ThreadUtils(){}

    public static Thread run(Runnable runnable){
        Thread thd = new Thread(runnable);
        thd.start();
        return thd;
    }

    public static Thread timeOut(long millis, Runnable runnable){
        return run(()->{
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                LogHelper.e("", e);
            }
            runnable.run();
        });
    }
    public static Thread interval(long millis, Runnable runnable){
        return run(()->{
            while (true){
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    //LogHelper.e("", e);
                    break;
                }
                runnable.run();
            }
        });
    }
}
