package com.danielbohry.sort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUnsorterTest {

    @Test
    void movesFilesBackAndRemovesEmptyCategoryFolders(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("photo.jpg"), "img");
        Files.writeString(dir.resolve("report.pdf"), "doc");
        new FileSorter().sort(dir);

        new FileUnsorter().unsort(dir);

        assertTrue(Files.exists(dir.resolve("photo.jpg")));
        assertTrue(Files.exists(dir.resolve("report.pdf")));
        assertFalse(Files.exists(dir.resolve("images")));
        assertFalse(Files.exists(dir.resolve("documents")));
    }

    @Test
    void keepsCategoryFolderWhenDestinationCollides(@TempDir Path dir) throws IOException {
        Files.createDirectory(dir.resolve("images"));
        Files.writeString(dir.resolve("images/photo.jpg"), "in folder");
        Files.writeString(dir.resolve("photo.jpg"), "already at root");

        new FileUnsorter().unsort(dir);

        assertEquals("already at root", Files.readString(dir.resolve("photo.jpg")));
        assertTrue(Files.exists(dir.resolve("images/photo.jpg")));
    }
}
