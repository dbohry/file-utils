package com.danielbohry.sort;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileDeduplicator {

    private static final String DUPLICATED_FOLDER = "duplicated";

    public void deduplicate(Path targetDir) throws IOException {
        if (!Files.isDirectory(targetDir)) {
            throw new IllegalArgumentException("Not a directory: " + targetDir);
        }

        Path duplicatedDir = targetDir.resolve(DUPLICATED_FOLDER);
        Map<String, List<Path>> filesByHash = new HashMap<>();
        Map<Path, FileTime> lastModified = new HashMap<>();

        try (Stream<Path> walk = Files.walk(targetDir)) {
            walk.filter(Files::isRegularFile)
                    .filter(path -> !path.startsWith(duplicatedDir))
                    .forEach(path -> {
                        try {
                            String hash = sha256(path);
                            filesByHash.computeIfAbsent(hash, k -> new ArrayList<>()).add(path);
                            lastModified.put(path, Files.getLastModifiedTime(path));
                        } catch (IOException e) {
                            System.err.println("Could not hash " + path + ": " + e.getMessage());
                        }
                    });
        }

        for (List<Path> duplicates : filesByHash.values()) {
            if (duplicates.size() < 2) {
                continue;
            }
            duplicates.sort(Comparator.comparing(lastModified::get));
            for (int i = 1; i < duplicates.size(); i++) {
                moveToDuplicated(duplicates.get(i), duplicatedDir);
            }
        }
    }

    private void moveToDuplicated(Path file, Path duplicatedDir) throws IOException {
        Files.createDirectories(duplicatedDir);
        Path destination = uniqueDestination(duplicatedDir, file.getFileName().toString());
        Files.move(file, destination, StandardCopyOption.REPLACE_EXISTING);
        IO.println(file + " -> duplicated/" + destination.getFileName());
    }

    private Path uniqueDestination(Path dir, String fileName) {
        Path candidate = dir.resolve(fileName);
        if (!Files.exists(candidate)) {
            return candidate;
        }

        String base = fileName;
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            base = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }

        int counter = 1;
        Path unique;
        do {
            unique = dir.resolve(base + "_" + counter + extension);
            counter++;
        } while (Files.exists(unique));
        return unique;
    }

    private String sha256(Path file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 not available", e);
        }

        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }

        StringBuilder hex = new StringBuilder();
        for (byte b : digest.digest()) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
