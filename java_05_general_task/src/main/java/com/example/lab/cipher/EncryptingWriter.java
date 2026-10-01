package main.java.com.example.lab.cipher;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Encrypts a character stream by adding the code value of a key
 * character to every character written through it. Wraps an underlying
 * Writer, per the FilterWriter/FilterReader requirement.
 *
 * IMPORTANT: FilterWriter's default write(char[],int,int) and
 * write(String,int,int) simply delegate straight to the wrapped Writer
 * WITHOUT going through write(int) - so overriding only write(int) would
 * silently fail to encrypt whenever a caller writes a char[] or a String
 * (which is exactly what BufferedWriter/PrintWriter normally do). All
 * three write(...) overloads are therefore overridden here.
 */
public class EncryptingWriter extends FilterWriter {

    private final int key;

    public EncryptingWriter(Writer out, char keyChar) {
        super(out);
        this.key = keyChar;
    }

    @Override
    public void write(int c) throws IOException {
        out.write(shift(c));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] shifted = new char[len];
        for (int i = 0; i < len; i++) {
            shifted[i] = shift(cbuf[off + i]);
        }
        out.write(shifted, 0, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        char[] shifted = new char[len];
        for (int i = 0; i < len; i++) {
            shifted[i] = shift(str.charAt(off + i));
        }
        out.write(shifted, 0, len);
    }

    /** (char) truncation wraps around modulo 65536, so this is always a valid char. */
    private char shift(int c) {
        return (char) (c + key);
    }
}
