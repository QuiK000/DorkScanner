package com.dev.quikkkk.parser.infrastructure.io;

import com.dev.quikkkk.parser.domain.model.SearchResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class ResultTxtWriter {
    public static void save(String filename, Collection<SearchResult> results) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            results.stream()
                    .map(SearchResult::address)
                    .distinct()
                    .forEach(link -> {
                        try {
                            writer.write(link);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
