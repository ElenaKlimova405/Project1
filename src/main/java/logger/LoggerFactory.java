package logger;

import logger.loggers.*;
import java.util.ArrayList;
import java.util.List;

public class LoggerFactory {
    String fileKey = ".logger.fileLogger.file";
    String maxFileSizeKey = ".logger.fileLogger.maxFileSize";
    String maxBackupIndexKey = ".logger.fileLogger.maxBackupIndex";
    String timePatternKey = ".time.pattern";

    String connection = ".logger.dataBaseLogger.connection";
    String userNameDataBase = ".logger.dataBaseLogger.userName";
    String password = ".logger.dataBaseLogger.password";
    String tableName = ".logger.dataBaseLogger.tableName";

    public void checkingNameForNull(String userName) {
        try {
            if (userName == null)
            {
                throw new Exception("UserName must be not null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Handler> getFileHandlers(String userName, String timePattern) {
        int step = 0;
        List<Handler> handlers = new ArrayList<>();

        while (true) {
            String currentFileKey = userName + fileKey;
            String currentMaxFileSizeKey = userName + maxFileSizeKey;
            String currentMaxBackupIndexKey = userName + maxBackupIndexKey;

            if (step != 0) {
                currentFileKey = currentFileKey + step;
                currentMaxFileSizeKey = currentMaxFileSizeKey + step;
                currentMaxBackupIndexKey = currentMaxBackupIndexKey + step;
            }
            step++;

            // fileKey
            PropertyService.setPathToTheLogPropertiesFile(LoggerManager.getPathToTheLogPropertiesFile());
            String gettingFile = PropertyService.getProperty(currentFileKey);

            if (gettingFile == null || gettingFile.isEmpty()) {
                if (step == 0 || step == 1) {
                    continue;
                }
                break;
            }

            // maxFileSizeKey
            String[] split = PropertyService.getProperty(currentMaxFileSizeKey).split(" ");
            int gettingMaxFileSize;
            String gettingUnit;
            if (split.length == 2) {
                gettingMaxFileSize = Integer.parseInt(split[0].trim());
                gettingUnit = split[1].trim();
            }
            else {
                gettingMaxFileSize = Integer.MAX_VALUE;
                gettingUnit = "Gb";
            }

            // maxBackupIndexKey
            int gettingMaxBackupIndex = 0;
            try {
                gettingMaxBackupIndex = Integer.valueOf(PropertyService.getProperty(currentMaxBackupIndexKey).trim());
            } catch (Exception exception) {
                // nothing
            }
            if (gettingMaxBackupIndex == 0) {
                gettingMaxBackupIndex = Integer.MAX_VALUE;
            }

            if (gettingFile != null && !gettingFile.isEmpty()) {
                FileHandler fileHandler = new FileHandler(gettingFile);
                fileHandler.setMaxFileSize(gettingMaxFileSize);
                fileHandler.setUnit(gettingUnit);
                fileHandler.setMaxBackupIndex(gettingMaxBackupIndex);
                fileHandler.setTimePattern(timePattern);

                handlers.add(fileHandler);
            }
        }

        return handlers;
    }

    public Handler getDataBaseHandler(String userName, String timePattern) {
        // connection
        String gettingConnection = PropertyService.getProperty(userName + connection);

        // userNameDataBase
        String gettingUserNameDataBase = PropertyService.getProperty(userName + userNameDataBase);

        // password
        String gettingPassword = PropertyService.getProperty(userName + password);

        // tableName
        String gettingTableName = PropertyService.getProperty(userName + tableName);

        if (gettingConnection != null && !gettingConnection.isEmpty()) {
            DataBaseHandler dataBaseHandler = new DataBaseHandler(gettingConnection);
            dataBaseHandler.setTimePattern(timePattern);
            dataBaseHandler.setUserName(gettingUserNameDataBase);
            dataBaseHandler.setPassword(gettingPassword);
            dataBaseHandler.setTableName(gettingTableName);
            return dataBaseHandler;
        }

        return null;
    }

    public Logger getLogger(String userName, String classReference) {

        checkingNameForNull(userName);

        List<Handler> handlers = new ArrayList<>();

        // timePatternKey
        String gettingPattern = PropertyService.getProperty(userName + timePatternKey).trim();

        List<Handler> fileHandlers = new ArrayList<>();
        fileHandlers = getFileHandlers(userName, gettingPattern);
        if (fileHandlers != null) {
            handlers.addAll(fileHandlers);
        }

        Handler dataBaseHandler;
        dataBaseHandler = getDataBaseHandler(userName, gettingPattern);
        if (dataBaseHandler != null) {
            handlers.add(dataBaseHandler);
        }

        Logger logger = new Logger(userName, classReference, handlers);
        return logger;
    }


    public List<LoggerProperty> getProperties(String userName) {
        checkingNameForNull(userName);

        int step = 0;
        List<LoggerProperty> loggerProperties = new ArrayList<>();
        while (true) {
            String currentProperty = userName + ".classReference";
            if (step != 0)
                currentProperty = currentProperty + step;
            step++;

            PropertyService.setPathToTheLogPropertiesFile(LoggerManager.getPathToTheLogPropertiesFile());
            String classReference = PropertyService.getProperty(currentProperty);
            if (classReference == null || classReference.isEmpty()) {
                if (step == 0 || step == 1) {
                    continue;
                }
                break;
            }

            String[] split = classReference.split(",", 2);

            String reference = "";
            String[] logLevelsSplit = null;
            if (split.length == 2) {
                if (split[0].length() > 0)
                    reference = split[0].trim();
                if (split[1].length() > 0)
                    logLevelsSplit = split[1].split(",");
            }

            List<LogLevel> logLevels = new ArrayList<>();

            if (logLevelsSplit.length > 0)
                for (String s : logLevelsSplit) {
                    logLevels.add(LogLevel.valueOf(s.trim()));
                }

            if (reference != null && !reference.isEmpty()) {
                LoggerProperty loggerProperty = new LoggerProperty(reference, logLevels);
                loggerProperties.add(loggerProperty);
            }
        }
        return loggerProperties;
    }
}
