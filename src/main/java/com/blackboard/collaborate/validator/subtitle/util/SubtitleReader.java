/*
 * Title: SubtitleReader
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.util;

import com.blackboard.collaborate.validator.subtitle.model.ParsePositionProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;


/**
 * Reader with line:column position.
 */
public class SubtitleReader extends LineNumberReader implements ParsePositionProvider {
    private int column; // current column (char in line)
    private int markedColumn;

    public SubtitleReader(Reader in) {
        super(in);
    }

    public SubtitleReader(InputStream is, Charset charset) {
        super(new InputStreamReader(is, charset));
        setLineNumber(1); // start with line 1
    }

    @Override
    public int read() throws IOException {
        synchronized (lock) {
            int c = super.read();
            if (c == '\n') {
                column = 0;
            } else {
                column++;
            }
            return c;
        }
    }

    @Override
    public int read(char cbuf[], int off, int len) throws IOException {
        synchronized (lock) {
            int n = super.read(cbuf, off, len);

            for (int i = off; i < off + n; i++) {
                if (cbuf[i] == '\n') {
                    column = 0;
                } else {
                    column++;
                }
            }

            return n;
        }
    }

    /**
     * @return Current read position within the line.
     */
    @Override
    public int getColumn() {
        return column;
    }

    public void mark(int readAheadLimit) throws IOException {
        synchronized (lock) {
            super.mark(readAheadLimit);
            markedColumn = column;
        }
    }

    public void reset() throws IOException {
        synchronized (lock) {
            super.reset();
            column = markedColumn;
        }
    }

    /**
     *
     * @return Next character, but does not change position.
     * @throws IOException when an IO exception occur
     */
    public int lookNext() throws IOException {
        synchronized (lock) {
            mark(1);
            int c = read();
            reset();
            return c;
        }
    }

    public CharBuffer lookNext(int len) throws IOException {
        synchronized (lock) {
            mark(len);

            CharBuffer cb = CharBuffer.allocate(len);
            int read = this.read(cb);
            reset();
            return cb.asReadOnlyBuffer();
        }
    }
}
