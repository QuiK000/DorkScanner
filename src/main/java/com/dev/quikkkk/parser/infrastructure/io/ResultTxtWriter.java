package com.dev.quikkkk.parser.infrastructure.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class ResultTxtWriter {
    public static void save(String filename, Collection<String> links) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String link : links) {
                writer.write(link);
                writer.newLine();
            }
        }
    }
}
