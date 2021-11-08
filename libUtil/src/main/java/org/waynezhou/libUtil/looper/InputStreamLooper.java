package org.waynezhou.libUtil.input_stream_looper;

import org.waynezhou.libUtil.LogHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public class InputStreamLooper {
    private final InputStream inputStream;

    public InputStreamLooper(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private InputStreamLooperAvailableListener availableListener = ($)->{};
    public InputStreamLooper setAvailableListener(InputStreamLooperAvailableListener listener){
        availableListener = listener;
        return this;
    }
    private InputStreamLooperErrorListener errorListener = ($)->{};
    public InputStreamLooper setErrorListener(InputStreamLooperErrorListener listener){
        errorListener = listener;
        return this;
    }
    private InputStreamLooperCondition loopCondition = ()->true;
    public InputStreamLooper setLoopCondition(InputStreamLooperCondition condition){
        loopCondition = condition;
        return this;
    }
    private InputStreamLooperInputLengthGetter<IOException> inputLengthGetter = InputStream::available;
    public InputStreamLooper setInputLengthGetter(InputStreamLooperInputLengthGetter<IOException> getter){
        inputLengthGetter = getter;
        return this;
    }
    public AtomicBoolean loopSwitch = new AtomicBoolean(false);
    public void loop(){
        loopSwitch.set(true);
        try {
            while (loopSwitch.get()){
                if(!loopCondition.isAlive())throw new IOException();
                int bufSize = inputLengthGetter.getLength(inputStream);
                LogHelper.d("bufSize: "+ bufSize);
                LogHelper.d("bufSize: "+ inputStream.available());
                int off = 0;
                byte[] buf = new byte[bufSize];
                while (off<bufSize){
                    int available = inputStream.available();
                    int $ = inputStream.read(buf, off, available);
                    off+=available;
                }


                availableListener.invoke(buf);
            }
        }catch (IOException e){
            //LogHelper.e("InputStreamLooper throw Exception", e);
            errorListener.invoke(e);
        }
    }
}
