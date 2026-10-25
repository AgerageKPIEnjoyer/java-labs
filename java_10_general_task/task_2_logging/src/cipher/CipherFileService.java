package cipher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsulates file-level encryption/decryption using
 * EncryptingWriter and DecryptingReader.
 */
public class CipherFileService {

    private static final Logger LOG = Logger.getLogger(CipherFileService.class.getName());

    /** @return the number of characters processed */
    public long encryptFile(Path plainInput, Path encryptedOutput, char key) throws IOException {
        prepare("Encrypting", plainInput, encryptedOutput, key);
        try (Reader reader = new BufferedReader(new FileReader(plainInput.toFile()));
             Writer writer = new EncryptingWriter(new BufferedWriter(new FileWriter(encryptedOutput.toFile())), key)) {
            long count = copy(reader, writer);
            LOG.info("Encryption finished: " + count + " characters");
            return count;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Encryption failed: " + e, e);
            throw e;
        }
    }

    /** @return the number of characters processed */
    public long decryptFile(Path encryptedInput, Path plainOutput, char key) throws IOException {
        prepare("Decrypting", encryptedInput, plainOutput, key);
        try (Reader reader = new DecryptingReader(new BufferedReader(new FileReader(encryptedInput.toFile())), key);
             Writer writer = new BufferedWriter(new FileWriter(plainOutput.toFile()))) {
            long count = copy(reader, writer);
            LOG.info("Decryption finished: " + count + " characters");
            return count;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Decryption failed: " + e, e);
            throw e;
        }
    }

    /** Validates the paths before anything is opened, so the input file can never be truncated by mistake. */
    private void prepare(String action, Path input, Path output, char key) throws IOException {
        LOG.info(action + " " + input + " -> " + output);
        LOG.fine(() -> "Key character code: " + (int) key);

        if (!Files.isRegularFile(input)) {
            LOG.severe("Input file not found: " + input);
            throw new NoSuchFileException(input.toString());
        }
        if (input.toAbsolutePath().normalize().equals(output.toAbsolutePath().normalize())) {
            LOG.severe("Input and output point to the same file: " + input);
            throw new IOException("Input and output must be different files");
        }
        if (Files.exists(output)) {
            LOG.warning("Output file already exists and will be overwritten: " + output);
        }
    }

    private long copy(Reader reader, Writer writer) throws IOException {
        char[] buffer = new char[1024];
        long total = 0;
        int n;
        while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
            total += n;
            final int chunk = n;
            LOG.finer(() -> "Copied a chunk of " + chunk + " characters");
        }
        return total;
    }
}
