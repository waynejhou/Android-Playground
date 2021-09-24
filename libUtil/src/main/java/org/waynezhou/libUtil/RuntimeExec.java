package org.waynezhou.libUtil;

import org.waynezhou.libUtil.event.EventGroup;
import org.waynezhou.libUtil.eventArgs.RuntimeExecResultEventArgs;
import org.waynezhou.libUtil.eventGroup.RuntimeExecEventGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RuntimeExec /*extends EventAction<RuntimeExec, RuntimeExecEventGroup>*/ {

    private final _RuntimeExecEventGroup eventGroup = new _RuntimeExecEventGroup();
    private final EventGroup<RuntimeExecEventGroup>.Invoker invoker;

    private static class _RuntimeExecEventGroup extends RuntimeExecEventGroup {
        public EventGroup<RuntimeExecEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public RuntimeExecEventGroup getEventGroup() {
        return eventGroup;
    }



    private final String command;
    public RuntimeExec(String command/*, RuntimeExecEventGroup eventPack*/) {
        //super(eventPack);
        this.invoker = eventGroup.getInvoker();
        this.command = command;
    }

    //@Override
    public void fire() {
        new Thread(() -> {
            try {
                LogHelper.d(command);
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader stdoutReader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stderrReader =
                        new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String stdout = readAll(stdoutReader);
                String stderr = readAll(stderrReader);
                process.waitFor();
                LogHelper.d(process.exitValue());
                LogHelper.d(stdout);
                LogHelper.d(stderr);
                if (process.exitValue() == 0){
                    invoker.invoke(g->g.execSuccess, new RuntimeExecResultEventArgs(
                            stdout, stderr, process.exitValue()
                    ));
                }else{
                    invoker.invoke(g->g.execFailure, new RuntimeExecResultEventArgs(
                            stdout, stderr, process.exitValue()
                    ));
                }
            } catch (IOException | InterruptedException e) {
                LogHelper.e("runtime exec error", e);
            }
        }).start();
    }


    private String readAll(BufferedReader bufReader) throws IOException {
        int read;
        char[] buffer = new char[4096];
        StringBuilder output = new StringBuilder();
        while ((read = bufReader.read(buffer)) > 0) {
            LogHelper.d(read);
            LogHelper.d((int)buffer[0]);
            output.append(buffer, 0, read);
        }
        bufReader.close();
        return output.toString();
    }
}
