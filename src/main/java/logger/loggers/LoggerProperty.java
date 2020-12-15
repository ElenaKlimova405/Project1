package logger.loggers;

import java.util.List;

public class LoggerProperty {
    private String classReference;
    private List<LogLevel> levels;

    public LoggerProperty(String classReference, List<LogLevel> levels) {
        this.classReference = classReference;
        this.levels = levels;
    }

    public String getClassReference() {
        return classReference;
    }

    public List<LogLevel> getLevels() {
        return levels;
    }
}
