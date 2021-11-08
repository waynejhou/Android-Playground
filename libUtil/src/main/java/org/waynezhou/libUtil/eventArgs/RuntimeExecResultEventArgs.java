package org.waynezhou.libUtil.eventArgs;

@Deprecated
public class RuntimeExecResultEventArgs {
    public final String stdout;
    public final String stderr;
    public final int returnCode;

    public RuntimeExecResultEventArgs(String stdout, String stderr, int returnCode) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.returnCode = returnCode;
    }
}
