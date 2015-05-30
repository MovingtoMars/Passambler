package passambler.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TimerTask;
import java.util.logging.Level;
import static passambler.util.Constants.LOGGER;

public abstract class PathWatcher extends TimerTask {
    private long lastChangeTime;

    private Path path;

    public PathWatcher(Path file) {
        this.path = file;
    }

    @Override
    public void run() {
        try {
            long time = Files.getLastModifiedTime(path).toMillis();

            if (lastChangeTime != time) {
                lastChangeTime = time;

                onChange();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to get last file modification date", e);
        }
    }

    public abstract void onChange();
}
