package com.dev.quikkkk.parser.infrastructure.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResultCsvWriter {
    public static void save(Collection<String> links, int limitPerFile) throws IOException {
        List<String> list = new ArrayList<>(links);
        int fileIndex = 1;

        for (int start = 0; start < list.size(); start += limitPerFile) {
            int end = Math.min(start + limitPerFile, list.size());
            List<String> chunk = list.subList(start, end);

            String filename = "results_" + fileIndex + ".csv";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (String link : chunk) {
                    writer.write(link);
                    writer.newLine();
                }
            }

            fileIndex++;
        }
    }
}
