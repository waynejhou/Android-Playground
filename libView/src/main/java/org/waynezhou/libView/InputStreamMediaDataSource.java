package org.waynezhou.libView;

import android.media.MediaDataSource;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamMediaDataSource extends MediaDataSource {
    private final InputStream stream;
    public InputStreamMediaDataSource(InputStream stream){
        this.stream = stream;
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        stream.reset();
        long skipped = stream.skip(position);
        return stream.read(buffer, offset, size);
    }

    @Override
    public long getSize() throws IOException {
        stream.reset();
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
