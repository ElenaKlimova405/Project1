package logger.loggers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;

public class PropertyService {
    private static Properties properties = new Properties();
    private static String pathToTheLogPropertiesFile = "log.properties"; // можно установить и другой путь
    private static byte[] digest;

    public static boolean refresh() {
        //  messageDigest;
        // проверяем, не изменялся ли файл log.properties
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            try (InputStream inputStream = Files.newInputStream(Paths.get(pathToTheLogPropertiesFile));
                 DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)
            ) {
                //nothing
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] digestLocal = messageDigest.digest();
            if (!Arrays.equals(digest, digestLocal) && pathToTheLogPropertiesFile != null) {
                properties.load(new FileInputStream(pathToTheLogPropertiesFile));
                return true;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getProperty(String propertyName) {
        refresh();
        return properties.getProperty(propertyName, "");
    }

    public static Properties getProperties() {
        return properties;
    }

    public static String getPathToTheLogPropertiesFile() {
        return pathToTheLogPropertiesFile;
    }

    public static void setPathToTheLogPropertiesFile(String pathToTheLogPropertiesFile) {
        PropertyService.pathToTheLogPropertiesFile = pathToTheLogPropertiesFile;
    }
}
