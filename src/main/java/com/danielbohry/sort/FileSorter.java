package com.danielbohry.sort;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileSorter {

    public void sort(Path targetDir) throws IOException {
        if (!Files.isDirectory(targetDir)) {
            throw new IllegalArgumentException("Not a directory: " + targetDir);
        }

        try (DirectoryStream<Path> entries = Files.newDirectoryStream(targetDir)) {
            for (Path entry : entries) {
                if (Files.isDirectory(entry)) {
                    continue;
                }
                moveToCategory(entry, targetDir);
            }
        }
    }

    private void moveToCategory(Path file, Path targetDir) throws IOException {
        String extension = extensionOf(file.getFileName().toString());
        FileCategory category = FileCategory.fromExtension(extension);
        Path categoryDir = targetDir.resolve(category.folderName());
        Files.createDirectories(categoryDir);

        Path destination = categoryDir.resolve(file.getFileName());
        if (Files.exists(destination)) {
            System.out.println("Skipping (already exists at destination): " + file.getFileName());
            return;
        }

        Files.move(file, destination, StandardCopyOption.REPLACE_EXISTING);
        System.out.println(file.getFileName() + " -> " + category.folderName() + "/");
    }

    private String extensionOf(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
