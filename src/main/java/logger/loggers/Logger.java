package logger.loggers;

import logger.LoggerManager;

import java.util.List;

public class Logger {
    private String userName;
    private String classReference;
    private List<Handler> handlers;

    public Logger(String userName, String classReference) {
        this.userName = userName;
        this.classReference = classReference;
    }

    public Logger(String userName, String classReference, List<Handler> handlers) {
        this.userName = userName;
        this.classReference = classReference;
        this.handlers = handlers;
    }

    public String getUserName() {
        return userName;
    }

    public String getClassReference() {
        return classReference;
    }

    public void all(String message) {
        LoggerProperty property = LoggerManager.getProperty(this);
        if (property != null) {
            for (LogLevel level : LoggerManager.getProperty(this).getLevels()) {
                for (Handler handler : this.handlers) {
                    handler.handle(new MessageStructure(
                            this.userName,
                            this.classReference,
                            LogLevel.ALL,
                            message)
                    );
                }
                return;
            }
        }
    }

    public void trace(String message) {
        LoggerProperty property = LoggerManager.getProperty(this);
        if (property != null) {
            for (LogLevel level : LoggerManager.getProperty(this).getLevels()) {
                if (LogLevel.TRACE.equals(level) || LogLevel.ALL.equals(level)) {
                    for (Handler handler : this.handlers) {
                        handler.handle(new MessageStructure(
                                this.userName,
                                this.classReference,
                                LogLevel.TRACE,
                                message)
                        );
                    }
                    return;
                }
            }
        }
    }

    public void debug(String message) {
        LoggerProperty property = LoggerManager.getProperty(this);
        if (property != null) {
            for (LogLevel level : LoggerManager.getProperty(this).getLevels()) {
                if (LogLevel.DEBUG.equals(level) || LogLevel.ALL.equals(level)) {
                    for (Handler handler : this.handlers) {
                        handler.handle(new MessageStructure(
                                this.userName,
                                this.classReference,
                                LogLevel.DEBUG,
                                message)
                        );
                    }
                    return;
                }
            }
        }
    }

    public void info(String message) {
        LoggerProperty property = LoggerManager.getProperty(this);
        if (property != null) {
            for (LogLevel level : LoggerManager.getProperty(this).getLevels()) {
                if (LogLevel.INFO.equals(level) || LogLevel.ALL.equals(level)) {
                    for (Handler handler : this.handlers) {
                        handler.handle(new MessageStructure(
                                this.userName,
                                this.classReference,
                                LogLevel.INFO,
                                message)
                        );
                    }
                    return;
                }
            }
        }
    }

    public void warn(String message) {
        LoggerProperty property = LoggerManager.getProperty(this);
        if (property != null) {
            for (LogLevel level : LoggerManager.getProperty(this).getLevels()) {
                if (LogLevel.WARN.equals(level) || LogLevel.ALL.equals(level)) {
                    for (Handler handler : this.handlers) {
                        handler.handle(new MessageStructure(
                                this.userName,
                                this.classReference,
                                LogLevel.WARN,
                                message)
                        );
                    }
                    return;
                }
            }
        }
    }

    public void error(String message, Throwable throwable) {
        LoggerProperty property = LoggerManager.getProperty(this);
        if (property != null /*&& property.isEnabled()*/) {
            for (LogLevel level : LoggerManager.getProperty(this).getLevels()) {
                if (LogLevel.ERROR.equals(level) || LogLevel.ALL.equals(level)) {
                    for (Handler handler : this.handlers) {
                        handler.handle(new MessageStructure(
                                this.userName,
                                this.classReference,
                                LogLevel.ERROR,
                                message + "Throwable: " + throwable.getMessage())
                        );
                    }
                    return;
                }
            }
        }
    }

    public void fatal(String message, Throwable throwable) {
        LoggerProperty property = LoggerManager.getProperty(this);
        if (property != null) {
            for (LogLevel level : LoggerManager.getProperty(this).getLevels()) {
                if (LogLevel.FATAL.equals(level) || LogLevel.ALL.equals(level)) {
                    for (Handler handler : this.handlers) {
                        handler.handle(new MessageStructure(
                                this.userName,
                                this.classReference,
                                LogLevel.FATAL,
                                message + "Throwable: " + throwable.getMessage())
                        );
                    }
                    return;
                }
            }
        }
    }
}
