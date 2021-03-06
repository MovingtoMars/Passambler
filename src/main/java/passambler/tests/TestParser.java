package passambler.tests;

import passambler.exception.TestException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestParser {
    private List<String> lines;
    private Map<String, StringBuilder> sections = new HashMap();

    public TestParser(Path file) throws IOException {
        this.lines = Files.readAllLines(file, Charset.forName("UTF-8"));
    }

    public Test parse() throws TestException {
        String section = null;

        for (String line : lines) {
            if (line.startsWith("--") && line.endsWith("--")) {
                section = line.substring(2, line.length() - 2);

                sections.put(section, new StringBuilder());
            } else if (section != null) {
                sections.get(section).append(line);

                if (lines.indexOf(line) != lines.size() - 1) {
                    sections.get(section).append("\n");
                }
            }
        }

        return new Test(getSection("TEST"), getSection("INPUT"), getSection("OUTPUT"), getSection("RESULT"));
    }

    public String getSection(String name) {
        return sections.containsKey(name) ? sections.get(name).toString() : null;
    }
}
