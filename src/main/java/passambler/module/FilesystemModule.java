package passambler.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import passambler.exception.EngineException;
import passambler.lexer.Lexer;
import passambler.parser.Parser;
import passambler.util.Constants;
import static passambler.util.Constants.LOGGER;
import passambler.value.Property;
import passambler.value.Value;

public class FilesystemModule implements Module {
    private final Path file;

    public FilesystemModule(String name) {
        if (Files.exists(Paths.get(name))) {
            this.file = Paths.get(name);
        } else {
            this.file = Paths.get(name + "." + Constants.EXTENSION);
        }
    }

    @Override
    public String getId() {
        return purify(file);
    }

    @Override
    public Module[] getChildren() {
        if (!Files.isDirectory(file)) {
            return null;
        }

        List<Module> modules = new ArrayList<>();

        try {
            Files.newDirectoryStream(file).forEach(f -> modules.add(new FilesystemModule(f.toString().replace("." + Constants.EXTENSION, ""))));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not get child modules from module", e);
        }

        return modules.toArray(new Module[modules.size()]);
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        try {
            applyNested(file, symbols);
        } catch (EngineException | IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load module", e);
        }
    }

    private void applyNested(Path file, Map<String, Value> symbols) throws IOException, EngineException {
        if (Files.isDirectory(file)) {
            for (Path fileInDirectory : Files.newDirectoryStream(file)) {
                Map<String, Value> map = new HashMap();

                applyNested(fileInDirectory, map);

                Value value = new Value();

                map.entrySet().stream().forEach(entry -> value.setProperty(entry.getKey(), new Property(entry.getValue())));

                symbols.put(purify(fileInDirectory), value);
            }
        } else {
            Parser parser = new Parser();

            parser.parse(new Lexer(String.join("\n", Files.readAllLines(file))));

            for (Map.Entry<String, Value> symbol : parser.getScope().getSymbols().entrySet()) {
                if (parser.getScope().isVisible(symbol.getKey())) {
                    symbols.put(symbol.getKey(), symbol.getValue());
                }
            }
        }
    }

    private String purify(Path path) {
        return path.getFileName().toString().replace("." + Constants.EXTENSION, "");
    }
}
