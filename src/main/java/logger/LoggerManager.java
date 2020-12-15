package logger;

import logger.loggers.Logger;
import logger.loggers.LoggerProperty;
import logger.loggers.PropertyService;

import java.util.*;

public class LoggerManager {
    private static LoggerManager loggerManager = new LoggerManager();
    public static final LoggerFactory LOGGER_FACTORY = new LoggerFactory();
    private static Map<String, HashMap<String, Logger>> loggers =
            new HashMap<String, HashMap<String, Logger>>(); // <userName, <classReference, logger>>
    private static Map<String, HashMap<String, LoggerProperty>> loggerProperties =
            new HashMap<String, HashMap<String, LoggerProperty>>(); // <userName, <classReference, loggerProperty>>

    private static String pathToTheLogPropertiesFile = "log.properties"; // можно установить и другой путь
    public static String getPathToTheLogPropertiesFile() {
        return LoggerManager.pathToTheLogPropertiesFile;
    }

    public static void setPathToTheLogPropertiesFile(String pathToTheLogPropertiesFile) {
        LoggerManager.pathToTheLogPropertiesFile = pathToTheLogPropertiesFile;
    }

    // если файл log.properties обновляся, то нужно обновить все свойства в loggerProperties:
    private static void updateLoggerProperties(String... pathToTheLogPropertiesFile) {
        if (pathToTheLogPropertiesFile.length == 1) {
            LoggerManager.setPathToTheLogPropertiesFile(pathToTheLogPropertiesFile[0]);
        }

        PropertyService.setPathToTheLogPropertiesFile(LoggerManager.getPathToTheLogPropertiesFile());
        if (PropertyService.refresh()) {
            Set<String> names = new HashSet<>(loggerProperties.keySet());
            loggerProperties.clear();
            Iterator<String> namesIterator = names.iterator();
            while (namesIterator.hasNext()) {
                String userName = namesIterator.next();
                List<LoggerProperty> properties = LOGGER_FACTORY.getProperties(userName);

                HashMap<String, LoggerProperty> addedMap = new HashMap<>();
                for (LoggerProperty property : properties) {
                    addedMap.put(property.getClassReference(), property);
                }
                loggerProperties.put(userName, addedMap);
            }
        }
    }

    // т.к. loggerManager только один, то достаточно только один раз (или вообще не указывать, тогда pathToTheLogPropertiesFile == "log.properties")
    // при первом вызове метода getLogger() указать путь pathToTheLogPropertiesFile к файлу log.properties,
    // тогда логгеры, позже объявленные без этого параметра, будут получать настройки из этого же property-файла.
    // Если в коде через некоторое время будет указан другой путь, то ВСЕ логгеры будут работать с новым property-файлом, имеющим указанный новый путь.
    // Но при этом в нем учитываются только изменения уровня логирования. Если там будут, например,
    // другие пути сохранения файла "USERNAME.logger.file1 = <другой путь файла, куда сохраняем отчет>", то они не будут актуальны, останутся
    // прежние настройки из первого property-файла.
    public static Logger getLogger(String userName, String classReference, String... pathToTheLogPropertiesFile) {
        loggerManager.updateLoggerProperties(pathToTheLogPropertiesFile);

        Logger logger = null;

        // логгеры конкретно данного юзера:
        HashMap<String, Logger> hashMap = loggers.get(userName);

        // ищем подходящий логгер по classReference либо по пакетам выше
        String substringOfTheClassReference = classReference;
        int ind = substringOfTheClassReference.lastIndexOf(".");
        if (hashMap != null) {
            while (logger == null && ind != -1) {
                logger = hashMap.get(substringOfTheClassReference);
                substringOfTheClassReference = substringOfTheClassReference.substring(0, ind);
                ind = substringOfTheClassReference.lastIndexOf(".");
                substringOfTheClassReference+="*";
            }
        }

        if (logger == null) {
            Logger logger1 = LOGGER_FACTORY.getLogger(userName, classReference);
            ArrayList<LoggerProperty> loggerPropertiesList =
                    (ArrayList<LoggerProperty>) LOGGER_FACTORY.getProperties(logger1.getUserName());

            hashMap = loggers.get(userName);
            if (hashMap != null) {
                hashMap.put(logger1.getClassReference(), logger1);
            } else {
                loggers.put(userName, new HashMap<String, Logger>());
                loggers.get(userName).put(logger1.getClassReference(), logger1);
            }

            HashMap<String, LoggerProperty> loggerPropertyMap;
            if (loggerProperties.get(userName) != null)
                loggerPropertyMap = new HashMap<>(loggerProperties.get(userName));
            else
                loggerPropertyMap = new HashMap<>();

            for (LoggerProperty loggerProperty : loggerPropertiesList) {
                if (loggerProperty.getClassReference().equals(classReference)) {
                    loggerPropertyMap.put(loggerProperty.getClassReference(), loggerProperty);
                    break;
                }
            }

            loggerProperties.put(userName, loggerPropertyMap);
            logger = logger1;
        }

        return logger;
    }

    public static LoggerManager getLoggerManager() {
        return loggerManager;
    }

    public static Map<String, LoggerProperty> getProperties(String userName) {
        return loggerProperties.get(userName);
    }

    public static LoggerProperty getProperty(Logger logger) {
        loggerManager.updateLoggerProperties();

        LoggerProperty loggerProperty = loggerProperties.get(logger.getUserName()).get(logger.getClassReference());

        // ищем подходящее property по classReference либо по пакетам выше
        String substringOfTheClassReference = logger.getClassReference();
        int ind = substringOfTheClassReference.lastIndexOf(".");

        while (loggerProperty == null && ind != -1) {
            loggerProperty = loggerProperties.get(logger.getUserName()).get(substringOfTheClassReference);
            substringOfTheClassReference = substringOfTheClassReference.substring(0, ind);
            ind = substringOfTheClassReference.lastIndexOf(".");
            substringOfTheClassReference+="*";
        }

        return loggerProperty;
    }
}
