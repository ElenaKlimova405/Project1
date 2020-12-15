package logger.loggers;

public class MessageStructure {
    private String userName = "";
    private String classReference = "";
    private LogLevel logLevelName = LogLevel.ALL;
    private String message = "";

    public MessageStructure(String userName, String classReference, LogLevel logLevelName, String message) {
        this.userName = userName;
        this.classReference = classReference;
        this.logLevelName = logLevelName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClassReference() {
        return classReference;
    }

    public void setClassReference(String classReference) {
        this.classReference = classReference;
    }

    public LogLevel getLogLevelName() {
        return logLevelName;
    }

    public void setLogLevelName(LogLevel logLevelName) {
        this.logLevelName = logLevelName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAllMessageAsString() {
        return "User name: " + this.userName +
                "; Class reference: " + this.classReference +
                "; Level: " + this.logLevelName.toString() +
                "; Message: " + this.message;
    }
}
