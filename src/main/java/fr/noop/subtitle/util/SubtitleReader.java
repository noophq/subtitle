package fr.noop.subtitle.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;


/**
 * Created by jdvorak on 06/02/2017.
 */
public class SubtitleReader extends LineNumberReader {
    private int column; // curent column (char in line)

    public SubtitleReader(Reader in) {
        super(in);
    }

    public SubtitleReader(InputStream is, Charset charset) {
        super(new InputStreamReader(is, charset));
    }

    @Override
    public int read() throws IOException {
        int c = super.read();
        if (c == '\n') {
            column = 0;
        }
        else {
            column++;
        }
        return c;
    }

    public int read(char cbuf[], int off, int len) throws IOException {
        int n = super.read(cbuf, off, len);

        for (int i = off; i < off + n; i++) {
            int c = cbuf[i];
            if (c == '\n') {
                column = 0;
            }
            else {
                column++;
            }
        }

        return n;
    }

    public int getColumn() {
        return column;
    }

    public int lookNext() throws IOException {
        mark(1);
        int c = read();
        reset();
        return c;
    }

    public CharBuffer lookNext(int len) throws IOException {
        mark(len);

        CharBuffer cb = CharBuffer.allocate(len);
        int read = this.read(cb);
        reset();
        return cb.asReadOnlyBuffer();
    }
}
