package com.danielbohry.sort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDeduplicatorTest {

    @Test
    void movesNewerDuplicateAndKeepsOlderOne(@TempDir Path dir) throws IOException {
        Path older = dir.resolve("older.jpg");
        Path newer = dir.resolve("newer.jpg");
        Files.writeString(older, "same content");
        Files.writeString(newer, "same content");
        touch(older, Instant.now().minus(2, ChronoUnit.DAYS));
        touch(newer, Instant.now());

        new FileDeduplicator().deduplicate(dir);

        assertTrue(Files.exists(older));
        assertFalse(Files.exists(newer));
        assertTrue(Files.exists(dir.resolve("duplicated/newer.jpg")));
    }

    @Test
    void findsDuplicatesInSubfolders(@TempDir Path dir) throws IOException {
        Files.createDirectory(dir.resolve("images"));
        Path original = dir.resolve("images/photo.jpg");
        Path duplicate = dir.resolve("photo_copy.jpg");
        Files.writeString(original, "same content");
        Files.writeString(duplicate, "same content");
        touch(original, Instant.now().minus(1, ChronoUnit.DAYS));
        touch(duplicate, Instant.now());

        new FileDeduplicator().deduplicate(dir);

        assertTrue(Files.exists(original));
        assertTrue(Files.exists(dir.resolve("duplicated/photo_copy.jpg")));
    }

    @Test
    void leavesUniqueFilesUntouched(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("a.txt"), "content A");
        Files.writeString(dir.resolve("b.txt"), "content B");

        new FileDeduplicator().deduplicate(dir);

        assertTrue(Files.exists(dir.resolve("a.txt")));
        assertTrue(Files.exists(dir.resolve("b.txt")));
        assertFalse(Files.exists(dir.resolve("duplicated")));
    }

    @Test
    void givesUniqueNameWhenTwoDuplicateSetsShareAFileName(@TempDir Path dir) throws IOException {
        Files.createDirectory(dir.resolve("a"));
        Files.createDirectory(dir.resolve("b"));
        Files.writeString(dir.resolve("photo.jpg"), "set one");
        Files.writeString(dir.resolve("a/photo.jpg"), "set one");
        Files.writeString(dir.resolve("b/photo.jpg"), "set two");
        Path setTwoOriginal = dir.resolve("set-two-original.jpg");
        Files.writeString(setTwoOriginal, "set two");
        touch(dir.resolve("photo.jpg"), Instant.now().minus(2, ChronoUnit.DAYS));
        touch(dir.resolve("a/photo.jpg"), Instant.now());
        touch(setTwoOriginal, Instant.now().minus(2, ChronoUnit.DAYS));
        touch(dir.resolve("b/photo.jpg"), Instant.now());

        new FileDeduplicator().deduplicate(dir);

        try (var entries = Files.list(dir.resolve("duplicated"))) {
            assertEquals(2, entries.count());
        }
    }

    private void touch(Path file, Instant instant) throws IOException {
        Files.setLastModifiedTime(file, FileTime.from(instant));
    }
}
