import logger.LoggerManager;
import logger.loggers.Logger;

public class Main {
    public static Logger logger1 = LoggerManager
            .getLogger("ELENA", "src.main.java.Main");
    public static Logger logger2 = LoggerManager
            .getLogger("ANMI", "src.main.java.LogClass");

    public static void main(String[] args){

        logger1.all("logger1 - Some massage for all.");
        logger1.trace("logger1 - Some massage for trace.");
        logger1.debug("logger1 - Some massage for debug.");
        logger1.info("logger1 - Some massage for info.");
        logger1.warn("logger1 - Some massage for warn.");
        logger1.error("logger1 - Some message for error.", new Exception("This is a new exception"));
        logger1.fatal("logger1 - Some massage for fatal.", new Throwable("This is a new throwable object"));

        logger2.all("logger3 - Some massage for all.");
        logger2.trace("logger3 - Some massage for trace.");
        logger2.debug("logger3 - Some massage for debug.");
        logger2.info("logger3 - Some massage for info.");
        logger2.warn("logger3 - Some massage for warn.");
        logger2.error("logger3 - Some message for error.", new Exception("This is a new exception"));
        logger2.fatal("logger3 - Some massage for fatal.", new Throwable("This is a new throwable object"));

        System.out.println("Done!");
    }
}
