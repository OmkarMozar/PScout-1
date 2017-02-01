package pscout.core;

import com.google.inject.Inject;
import pscout.models.Config;

import java.io.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExtractJarFile {
    private final Logger LOGGER = Logger.getLogger(ExtractJarFile.class.getName());

    private final Config config;

    @Inject
    public ExtractJarFile(Config config) {
        this.config = config;
    }

    public void execute() {
        try {
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(new File(config.jarFilePath))));
            try {
                ZipEntry ze;
                int count;
                byte[] buffer = new byte[8192];
                while ((ze = zis.getNextEntry()) != null) {
                    File file = new File(config.classDumpPath, ze.getName());
                    File dir = ze.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                dir.getAbsolutePath());
                    if (ze.isDirectory())
                        continue;
                    FileOutputStream fout = new FileOutputStream(file);
                    try {
                        while ((count = zis.read(buffer)) != -1)
                            fout.write(buffer, 0, count);
                    } finally {
                        fout.close();
                    }
                }
            } finally {
                zis.close();
            }
        } catch (IOException e) {

        }
    }
}
