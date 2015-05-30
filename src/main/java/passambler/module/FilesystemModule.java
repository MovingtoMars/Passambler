package passambler.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import passambler.exception.EngineException;
import passambler.lexer.Lexer;
import passambler.parser.Parser;
import static passambler.util.Constants.LOGGER;
import passambler.value.Value;

public class FilesystemModule implements Module {
    private Path path;

    public FilesystemModule(Path path) {
        this.path = path;
    }

    @Override
    public String getId() {
        return path.getFileName().toString();
    }

    @Override
    public Module[] getChildren() {
        if (!Files.isDirectory(path)) {
            return null;
        }

        try {
            List<Module> modules = new ArrayList<>();

            for (Path file : Files.newDirectoryStream(path)) {
                if (Files.isDirectory(file)) {
                    modules.add(new FilesystemModule(file));
                }
            }

            return modules.isEmpty() ? null : modules.toArray(new Module[modules.size()]);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        Parser parser = new Parser();

        try {
            parser.parse(new Lexer(String.join("\n", Files.readAllLines(path))));
        } catch (IOException | EngineException e) {
            LOGGER.log(Level.SEVERE, "Error while loading module", e);
        }

        symbols.putAll(parser.getScope().getSymbols());
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
