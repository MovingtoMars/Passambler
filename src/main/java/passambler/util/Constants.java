package passambler.util;

import java.util.logging.Logger;

public class Constants {
    public static final String VERSION = "DEV";
    public static final Logger LOGGER = Logger.getLogger("Passambler");

    public static final boolean DEBUG = true;

    public static final String EXTENSION = "psm";
    public static final String TEST_EXTENSION = "psmt";

    public static final String PATH_ENV_KEY = "PSMPATH";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    static {
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new LogHandler());
    }
}
