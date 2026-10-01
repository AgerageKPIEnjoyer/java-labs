package main.java.com.example.lab.html;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

/**
 * All file operations for TagFrequencyReport are encapsulated here,
 * using ObjectOutputStream/ObjectInputStream.
 * The destination/source path is a parameter,
 * not hard-coded, so the caller can specify location and filename.
 */
public class ReportFileManager {

    public void saveReport(TagFrequencyReport report, Path destination) throws IOException {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(destination.toFile())))) {
            oos.writeObject(report);
        }
    }

    public TagFrequencyReport loadReport(Path source) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new BufferedInputStream(new FileInputStream(source.toFile())))) {
            Object obj = ois.readObject();
            if (!(obj instanceof TagFrequencyReport)) {
                throw new InvalidObjectException(
                        source + " does not contain a TagFrequencyReport.");
            }
            return (TagFrequencyReport) obj;
        }
    }
}
