package passambler.tests;

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

    public Test parse(String[] requiredSections) throws TestException {
        String section = null;

        for (String line : lines) {
            if (line.startsWith("--") && line.endsWith("--")) {
                section = line.substring(2, line.length() - 2);

                sections.put(section, new StringBuilder());
            } else if (section != null) {
                sections.get(section).append(line);

                if (!line.equals(lines.get(lines.size() - 1))) {
                    sections.get(section).append("\n");
                }
            }
        }

        for (String requiredSection : requiredSections) {
            if (!sections.containsKey(requiredSection)) {
                throw new TestException("missing section '%s'", requiredSection);
            }
        }

        return new Test(sections.get("desc").toString(), sections.get("input").toString(), sections.get("result").toString());
    }
}
