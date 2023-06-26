package org.example;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private static final int MAX_FILE_SIZE = 1048576; // 1 Мб

    private static class Section {
        private final String name;
        private final int level;
        private final int lineIndex;

        public Section(String name, int level, int lineIndex) {
            this.name = name;
            this.level = level;
            this.lineIndex = lineIndex;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public int getLineIndex() {
            return lineIndex;
        }
    }

    @PostMapping("/parse")
    public String parseFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Invalid file");
        }

        String[] lines = IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8).split("\\R");

        List<String> result = new ArrayList<>();
        List<Section> sections = new ArrayList<>();

        for (int i = 0; i <lines.length; i++) {
            String line = lines[i].trim();
            int level = 0;
            while (line.charAt(level) == '#') {
                level++;
            }
            if (level > 0) {
                sections.add(new Section(line.substring(level).trim(), level, i));
            }
            result.add(line);
        }

        StringBuilder sb = new StringBuilder();
        for (Section section : sections) {
            sb.append(section.getLevel()).append(";")
                    .append(section.getName()).append(";")
                    .append(section.getLineIndex()).append("\n");
        }
        sb.append("##\n"); // разделитель структуры и содержимого файла
        for (String line : result) {
            sb.append(line).append("\n");
        }

        return sb.toString();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }

}