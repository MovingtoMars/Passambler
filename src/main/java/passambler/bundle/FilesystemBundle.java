package passambler.bundle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import passambler.lexer.Lexer;
import passambler.parser.Parser;
import passambler.exception.EngineException;
import passambler.value.Value;

public class FilesystemBundle implements Bundle {
    private Path path;

    public FilesystemBundle(Path path) {
        this.path = path;
    }

    @Override
    public String getId() {
        return path.getFileName().toString();
    }

    @Override
    public Bundle[] getChildren() {
        if (!Files.isDirectory(path)) {
            return null;
        }

        try {
            List<Bundle> bundles = new ArrayList<>();

            for (Path file : Files.newDirectoryStream(path)) {
                if (Files.isDirectory(file)) {
                    bundles.add(new FilesystemBundle(file));
                }
            }

            return bundles.isEmpty() ? null : bundles.toArray(new Bundle[bundles.size()]);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void apply(Map<String, Value> symbols) {
        if (Files.isDirectory(path)) {
            try {
                for (Path file : Files.newDirectoryStream(path)) {
                    if (!Files.isDirectory(file)) {
                        applyFromFile(file, symbols);
                    }
                }
            } catch (IOException e) {
            }
        } else {
            applyFromFile(path, symbols);
        }
    }

    private void applyFromFile(Path path, Map<String, Value> symbols) {
        Parser parser = new Parser();

        try {
            parser.parse(new Lexer(String.join("\n", Files.readAllLines(path))));
        } catch (IOException | EngineException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, Value> symbol : parser.getScope().getSymbols().entrySet()) {
            if (parser.getScope().isVisible(symbol.getKey())) {
                symbols.put(symbol.getKey(), symbol.getValue());
            }
        }
    }
}
