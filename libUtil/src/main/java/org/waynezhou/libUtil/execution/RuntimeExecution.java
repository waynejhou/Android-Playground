package org.waynezhou.libUtil.execution;

import androidx.annotation.NonNull;

import org.waynezhou.libUtil.event.BaseEventGroup;
import org.waynezhou.libUtil.eventArgs.RuntimeExecResultEventArgs;
import org.waynezhou.libUtil.eventGroup.RuntimeExecBaseEventGroup;
import org.waynezhou.libUtil.log.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class RuntimeExecution /*extends EventAction<RuntimeExec, RuntimeExecEventGroup>*/ {

    private final _RuntimeExecBaseEventGroup eventGroup = new _RuntimeExecBaseEventGroup();
    private final BaseEventGroup<RuntimeExecBaseEventGroup>.Invoker invoker;

    private static class _RuntimeExecBaseEventGroup extends RuntimeExecBaseEventGroup {
        @NonNull
        public BaseEventGroup<RuntimeExecBaseEventGroup>.Invoker getInvoker() {
            return super.getInvoker();
        }
    }

    public RuntimeExecBaseEventGroup getEventGroup() {
        return eventGroup;
    }



    private final String command;
    public RuntimeExecution(String command/*, RuntimeExecEventGroup eventPack*/) {
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
