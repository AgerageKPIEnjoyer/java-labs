package main.java.com.example.lab.cipher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

/**
 * Encapsulates file-level encryption/decryption using
 * EncryptingWriter and DecryptingReader.
 */
public class CipherFileService {

    public void encryptFile(Path plainInput, Path encryptedOutput, char key) throws IOException {
        try (Reader reader = new BufferedReader(new FileReader(plainInput.toFile()));
             Writer writer = new EncryptingWriter(new BufferedWriter(new FileWriter(encryptedOutput.toFile())), key)) {
            copy(reader, writer);
        }
    }

    public void decryptFile(Path encryptedInput, Path plainOutput, char key) throws IOException {
        try (Reader reader = new DecryptingReader(new BufferedReader(new FileReader(encryptedInput.toFile())), key);
             Writer writer = new BufferedWriter(new FileWriter(plainOutput.toFile()))) {
            copy(reader, writer);
        }
    }

    private void copy(Reader reader, Writer writer) throws IOException {
        char[] buffer = new char[1024];
        int n;
        while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
        }
    }
}
