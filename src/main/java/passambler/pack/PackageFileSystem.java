package passambler.pack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import passambler.lexer.Lexer;
import passambler.lexer.LexerException;
import passambler.parser.Parser;
import passambler.parser.ParserException;
import passambler.value.Value;

public class PackageFileSystem implements Package {
    private Path path;

    public PackageFileSystem(Path path) {
        this.path = path;
    }

    @Override
    public String getId() {
        return path.getFileName().toString();
    }

    @Override
    public Package[] getChildren() {
        if (!Files.isDirectory(path)) {
            return null;
        }

        try {
            List<Package> packages = new ArrayList<>();

            for (Path file : Files.newDirectoryStream(path)) {
                if (Files.isDirectory(file)) {
                    packages.add(new PackageFileSystem(file));
                }
            }

            return packages.isEmpty() ? null : packages.toArray(new Package[packages.size()]);
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
        } catch (IOException | LexerException | ParserException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, Value> symbol : parser.getScope().getSymbols().entrySet()) {
            if (Lexer.isPublic(symbol.getKey())) {
                symbols.put(symbol.getKey(), symbol.getValue());
            }
        }
    }
}
