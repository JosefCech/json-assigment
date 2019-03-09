package org.cechjoe.work.assigment.repository;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.*;


@Component
public class FileRepository {

    private String FILE_NAME;

    public FileRepository() {
        this("data.out");
    }

    public FileRepository(@NotNull String sourceFile) {
        FILE_NAME = sourceFile;
    }

    private File getFileInstance() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private int getCountLines() throws IOException {
        FileReader input = new FileReader(FILE_NAME);
        LineNumberReader count = new LineNumberReader(input);
        while (count.skip(Long.MAX_VALUE) > 0) {
        }
        return count.getLineNumber();
    }

    public int appendLine(@NotNull String line) {
        int updatedLine = -1;
        try {
            File file = getFileInstance();
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true), "UTF-8"));
            writer.append(line);
            writer.flush();
            updatedLine = getCountLines();
            writer.append("\n");
            writer.flush();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return updatedLine;
    }

    public String getLine(int rowNum) {
        String line = "";
        try (Stream<String> lines = Files.lines(Paths.get(FILE_NAME))) {
            line = lines.skip(rowNum).findFirst().get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public Stream<String> getAllLines() {
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                return Files.lines(Paths.get(FILE_NAME));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Stream.<String>builder().build();
    }
}
