package com.dev.quikkkk.parser.infrastructure.io;

import com.dev.quikkkk.parser.domain.model.SearchResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResultCsvWriter {
    public static void save(Collection<SearchResult> links, int limitPerFile) throws IOException {
        List<SearchResult> list = new ArrayList<>(links);
        int fileIndex = 1;

        for (int start = 0; start < list.size(); start += limitPerFile) {
            int end = Math.min(start + limitPerFile, list.size());
            List<SearchResult> chunk = list.subList(start, end);

            String filename = "results_" + fileIndex + ".csv";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write("Address;Description");
                writer.newLine();

                for (SearchResult r : chunk) {
                    writer.write(r.address() + ";\"" + r.description().replace("\"", "\"\"") + "\"");
                    writer.newLine();
                }
            }

            fileIndex++;
        }
    }
}