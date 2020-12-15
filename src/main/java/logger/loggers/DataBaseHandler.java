package logger.loggers;

import java.sql.*;
import org.h2.Driver;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataBaseHandler  implements Handler{
    /*
     * сохраненные параметры хендлера
     * */
    String connection;
    String userName;
    String password;
    String tableName;
    private String timePattern;

    public DataBaseHandler(String connection) {
        this.connection = connection;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void handle(MessageStructure message) {
        // конструируем сообщение с датой по шаблону из log.properties
        String dateAsString = "";
        if (!timePattern.isEmpty() && timePattern != null) {
            Date dateNow = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat(timePattern);
            dateAsString = formatForDateNow.format(dateNow);
        }

        try {
            DriverManager.registerDriver(Driver.load());
            try (Connection connection = DriverManager.getConnection(this.connection, this.userName, this.password);) {
                createTable(connection);
                try (Statement statement = connection.createStatement();) {
                    StringBuilder query = new StringBuilder();
                    query.append("INSERT INTO ");
                    query.append(this.tableName);
                    query.append("(DATE, USERNAME, CLASSREFERENCE, LEVEL, MESSAGE) VALUES ('");
                    query.append(dateAsString);
                    query.append("', '");
                    query.append(message.getUserName());
                    query.append("', '");
                    query.append(message.getClassReference());
                    query.append("', '");
                    query.append(message.getLogLevelName());
                    query.append("', '");
                    query.append(message.getMessage());
                    query.append("')");

                    statement.executeUpdate(query.toString());
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void createTable(Connection connection) {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ");
        query.append(this.tableName);
        query.append(" (");
        query.append("id int NOT NULL AUTO_INCREMENT,");
        query.append("date varchar(200),");
        query.append("userName varchar(200) NOT NULL,");
        query.append("classReference varchar(1000),");
        query.append("level varchar(10),");
        query.append("message varchar(1000),");
        query.append("PRIMARY KEY (id)");
        query.append(");");

        try (Statement statement = connection.createStatement();)
        {
            statement.executeUpdate(query.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
