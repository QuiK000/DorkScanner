package com.dev.quikkkk.parser.infrastructure.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DorkLoader {
    public static List<String> load(String filePath) throws IOException {
        List<String> dorks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    dorks.add(line.trim());
                }
            }
        }

        return dorks;
    }
}
