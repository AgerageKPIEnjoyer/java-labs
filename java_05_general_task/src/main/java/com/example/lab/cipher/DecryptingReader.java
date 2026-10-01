package main.java.com.example.lab.cipher;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Decrypts a character stream previously encrypted by
 * EncryptingWriter, by subtracting the same key character's code value
 * from every character read. Wraps an underlying Reader, per the
 * FilterWriter/FilterReader requirement.
 *
 * IMPORTANT: FilterReader's default read(char[],int,int) delegates
 * straight to the wrapped Reader WITHOUT going through read() - so
 * overriding only read() would silently fail to decrypt whenever a
 * caller reads into a char[] (which is exactly what BufferedReader
 * normally does internally). Both overloads are therefore overridden here.
 */
public class DecryptingReader extends FilterReader {

    private final int key;

    public DecryptingReader(Reader in, char keyChar) {
        super(in);
        this.key = keyChar;
    }

    @Override
    public int read() throws IOException {
        int c = in.read();
        if (c == -1) {
            return -1;
        }
        return unshift(c);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int n = in.read(cbuf, off, len);
        for (int i = 0; i < n; i++) {
            cbuf[off + i] = (char) unshift(cbuf[off + i]);
        }
        return n;
    }

    private int unshift(int c) {
        return (char) (c - key);
    }
}
