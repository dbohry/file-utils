package com.danielbohry.sort;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUnsorter {

    public void unsort(Path targetDir) throws IOException {
        if (!Files.isDirectory(targetDir)) {
            throw new IllegalArgumentException("Not a directory: " + targetDir);
        }

        for (FileCategory category : FileCategory.values()) {
            Path categoryDir = targetDir.resolve(category.folderName());
            if (!Files.isDirectory(categoryDir)) {
                continue;
            }
            moveUpAndRemove(categoryDir, targetDir);
        }
    }

    private void moveUpAndRemove(Path categoryDir, Path targetDir) throws IOException {
        boolean allMoved = true;

        try (DirectoryStream<Path> entries = Files.newDirectoryStream(categoryDir)) {
            for (Path entry : entries) {
                if (Files.isDirectory(entry)) {
                    allMoved = false;
                    continue;
                }

                Path destination = targetDir.resolve(entry.getFileName());
                if (Files.exists(destination)) {
                    System.out.println("Skipping (already exists at destination): " + entry.getFileName());
                    allMoved = false;
                    continue;
                }

                Files.move(entry, destination, StandardCopyOption.REPLACE_EXISTING);
                System.out.println(categoryDir.getFileName() + "/" + entry.getFileName() + " -> " + entry.getFileName());
            }
        }

        if (allMoved) {
            Files.delete(categoryDir);
        }
    }
}
