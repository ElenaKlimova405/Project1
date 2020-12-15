package logger.loggers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHandler  implements Handler{
    /*
     * сохраненные параметры хендлера
     * */
    private String pathToLogfile;
    private String timePattern;
    private int maxFileSize;
    private String unit;
    private int maxBackupIndex;
    private int currentIndex = 0;

    public FileHandler(String pathToLogfile) {
        this.pathToLogfile = pathToLogfile;
    }

    public void setPathToLogfile(String pathToLogfile) {
        this.pathToLogfile = pathToLogfile;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setMaxBackupIndex(int maxBackupIndex) {
        this.maxBackupIndex = maxBackupIndex;
    }

    private void writeMessageToTheFile(String message, String path) {
        byte[] data = message.getBytes();
        Path filePath = Paths.get(path);
        try (OutputStream outputStream = new BufferedOutputStream(
                Files.newOutputStream(filePath, StandardOpenOption.APPEND))) {
            outputStream.write(data, 0, data.length);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    /**
     * Сначала создается сообщение как
     * ДАТА (в указанном формате из файла настроек) + MESSAGE.
     * Далее необходимо записать это сообщение в файл.
     * Пусть, например, в файле настроек указано имя файла как "file.txt",
     * его размер как 5020 Kb, а максимальное количество созданных файлов 3.
     * Тогда алгоритм будет действовать следующим образом:
     * сообщения будут записываться в конец файла "file.txt" до тех пор, пока его размер не превысит 5020 Kb,
     * после этого создается следующий файл, либо пересоздается, если он существовал,
     * затем сообщения будут записываться в конец файла "file (1).txt" до тех пор, пока его размер не превысит 5020 Kb,
     * после этого создается следующий файл, либо пересоздается, если он существовал,
     * затем сообщения будут записываться в конец файла "file (2).txt" до тех пор, пока его размер не превысит 5020 Kb,
     * после этого создается следующий файл, либо пересоздается, если он существовал,
     * затем сообщения снова будут записываться в конец файла "file.txt" до тех пор, пока его размер не превысит 5020 Kb и т.д.
     */
    public void handle(MessageStructure messageStructure) {
        if (pathToLogfile == null || pathToLogfile == "") {
            return;
        }

        //  дозапись в файл. Проверять что он существует, проверять на размер .
        //  File, File (1), FIle (2) .. File (n) и начинать нумерацию сначала

        // конструируем сообщение с датой по шаблону из log.properties
        String message = messageStructure.getAllMessageAsString();
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(timePattern);
        message = formatForDateNow.format(dateNow) + " " + message + "\n";

        // конструируем имя файла.

        String currentFileName = pathToLogfile;
        if (currentIndex != 0) {
            int dotIndex;
            dotIndex = pathToLogfile.lastIndexOf('.');
            currentFileName = pathToLogfile.substring(0, dotIndex) +
                    " (" + currentIndex + ")" + pathToLogfile.substring(dotIndex);
        } else
            currentFileName = pathToLogfile;

        File file = new File(currentFileName);

        if (file.exists()) {
            double fileSize;

            switch (this.unit) {
                case "B": // если размер был указан в байтах
                    fileSize = getFileSizeBytes(file);
                    break;
                case "Kb": // если размер был указан в килобайтах
                    fileSize = getFileSizeKiloBytes(file);
                    break;
                case "Mb": // если размер был указан в мегабайтах
                    fileSize = getFileSizeMegaBytes(file);
                    break;
                case "Gb": // если размер был указан в гигабайтах
                    fileSize = getFileSizeGigaBytes(file);
                    break;
                default:
                    fileSize = 0;
                    break;
            }

            if (fileSize > this.maxFileSize) {
                currentIndex = (currentIndex + 1) % this.maxBackupIndex;
                if (currentIndex != 0) {
                    int dotIndex;
                    dotIndex = pathToLogfile.lastIndexOf('.');
                    currentFileName = pathToLogfile.substring(0, dotIndex) +
                            " (" + currentIndex + ")" + pathToLogfile.substring(dotIndex);
                } else
                    currentFileName = pathToLogfile;

                // получение следующего имени файла и очистка этого файла
                file = new File(currentFileName);
                try {
                    Files.deleteIfExists(Paths.get(currentFileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                try {
                    Files.createFile(Paths.get(currentFileName));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } else {
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                Path createdFile = Files.createFile(Paths.get(currentFileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // теперь дописываем message в конец нужного файла
        writeMessageToTheFile(message, currentFileName);
    }

    // метод возвращает размер файла в гигабайтах
    // длину файла делим на 1 гигабайт (1024 * 1024 * 1024 байт) и узнаем количество гигабайт
    private static double getFileSizeGigaBytes(File file) {
        return 1.0 * file.length() / (1024*1024*1024);
    }

    // метод возвращает размер файла в мегабайтах
    // длину файла делим на 1 мегабайт (1024 * 1024 байт) и узнаем количество мегабайт
    private static double getFileSizeMegaBytes(File file) {
        return 1.0 * file.length() / (1024*1024);
    }

    // метод возвращает размер файла в килобайтах
    // длину файла делим на 1 килобайт (1024 байт) и узнаем количество килобайт
    private static double getFileSizeKiloBytes(File file) {
        return 1.0 * file.length() / 1024;
    }

    // просто вызываем метод length() и получаем размер файла в байтах
    private static double getFileSizeBytes(File file) {
        return 1.0 * file.length();
    }
}
