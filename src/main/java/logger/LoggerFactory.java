package logger;

import logger.loggers.*;

import java.util.ArrayList;
import java.util.List;

public class LoggerFactory {
    public Logger getLogger(String userName, String classReference) {
        try {
            if (userName == null)
            {
                throw new Exception("UserName must be not null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Handler> handlers = new ArrayList<>();

        // здесь только FileHandler.
        String fileKey = userName+".logger.file";
        String maxFileSizeKey = userName+".logger.maxFileSize";
        String maxBackupIndexKey = userName+".logger.maxBackupIndex";
        String timePatternKey = userName+".time.pattern";

        // timePatternKey
        String gettingPattern = PropertyService.getProperty(timePatternKey).trim();

        int step = 0;
        while (true) {
            String currentFileKey = fileKey;
            String currentMaxFileSizeKey = maxFileSizeKey;
            String currentMaxBackupIndexKey = maxBackupIndexKey;
            if (step != 0) {
                currentFileKey = currentFileKey + step;
                currentMaxFileSizeKey = currentMaxFileSizeKey + step;
                currentMaxBackupIndexKey = currentMaxBackupIndexKey + step;
            }
            step++;

            // fileKey
            PropertyService.setPathToTheLogPropertiesFile(LoggerManager.getPathToTheLogPropertiesFile());
            String gettingFile = PropertyService.getProperty(currentFileKey);

            if (gettingFile == null) {
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
                gettingMaxFileSize = Integer.valueOf(split[0].trim());
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

            if (gettingFile != null && !gettingFile.equals("")) {
                FileHandler fileHandler = new FileHandler(gettingFile);
                fileHandler.setMaxFileSize(gettingMaxFileSize);
                fileHandler.setUnit(gettingUnit);
                fileHandler.setMaxBackupIndex(gettingMaxBackupIndex);
                fileHandler.setTimePattern(gettingPattern);

                handlers.add(fileHandler);
            }
        }


        // здесь только DataBaseHandler.
        String connection = userName+".logger.dataBaseLogger.connection";
        String userNameDataBase = userName+".logger.dataBaseLogger.userName";
        String password = userName+".logger.dataBaseLogger.password";
        String tableName = userName+".logger.dataBaseLogger.tableName";

        // connection
        String gettingConnection = PropertyService.getProperty(connection);

        // userNameDataBase
        String gettingUserNameDataBase = PropertyService.getProperty(userNameDataBase);

        // password
        String gettingPassword = PropertyService.getProperty(password);

        // tableName
        String gettingTableName = PropertyService.getProperty(tableName);

        if (gettingConnection != null && !gettingConnection.equals("")) {
            DataBaseHandler dataBaseHandler = new DataBaseHandler(gettingConnection);
            dataBaseHandler.setTimePattern(gettingPattern);
            dataBaseHandler.setUserName(gettingUserNameDataBase);
            dataBaseHandler.setPassword(gettingPassword);
            dataBaseHandler.setTableName(gettingTableName);
            handlers.add(dataBaseHandler);
        }


        Logger logger = new Logger(userName, classReference, handlers);
        return logger;
    }


    public List<LoggerProperty> getProperties(String userName) {
        try {
            if (userName == null)
            {
                throw new Exception("UserName must be not null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int step = 0;
        List<LoggerProperty> loggerProperties = new ArrayList<>();
        while (true) {
            String currentProperty = userName + ".classReference";
            if (step != 0)
                currentProperty = currentProperty + step;
            step++;

            PropertyService.setPathToTheLogPropertiesFile(LoggerManager.getPathToTheLogPropertiesFile());
            String classReference = PropertyService.getProperty(currentProperty);
            if (classReference == null || classReference.equals("")) {
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

            if (reference != null && ! reference.equals("")) {
                LoggerProperty loggerProperty = new LoggerProperty(reference, logLevels);
                loggerProperties.add(loggerProperty);
            }
        }
        return loggerProperties;
    }
}
