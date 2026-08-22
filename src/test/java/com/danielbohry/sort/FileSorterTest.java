package com.danielbohry.sort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSorterTest {

    @Test
    void movesFilesIntoCategoryFolders(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("photo.jpg"), "img");
        Files.writeString(dir.resolve("report.pdf"), "doc");
        Files.writeString(dir.resolve("unknown.xyz"), "?");

        new FileSorter().sort(dir);

        assertTrue(Files.exists(dir.resolve("images/photo.jpg")));
        assertTrue(Files.exists(dir.resolve("documents/report.pdf")));
        assertTrue(Files.exists(dir.resolve("other/unknown.xyz")));
        assertFalse(Files.exists(dir.resolve("photo.jpg")));
    }

    @Test
    void leavesExistingSubfoldersUntouched(@TempDir Path dir) throws IOException {
        Files.createDirectory(dir.resolve("images"));
        Files.writeString(dir.resolve("images/existing.jpg"), "img");
        Files.writeString(dir.resolve("new.jpg"), "img2");

        new FileSorter().sort(dir);

        assertTrue(Files.exists(dir.resolve("images/existing.jpg")));
        assertTrue(Files.exists(dir.resolve("images/new.jpg")));
    }

    @Test
    void skipsFileWhenDestinationAlreadyExists(@TempDir Path dir) throws IOException {
        Files.createDirectory(dir.resolve("documents"));
        Files.writeString(dir.resolve("documents/report.pdf"), "original");
        Files.writeString(dir.resolve("report.pdf"), "new");

        new FileSorter().sort(dir);

        assertEquals("original", Files.readString(dir.resolve("documents/report.pdf")));
        assertTrue(Files.exists(dir.resolve("report.pdf")));
    }
}
