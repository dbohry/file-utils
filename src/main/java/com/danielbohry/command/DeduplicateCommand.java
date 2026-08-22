package com.danielbohry.command;

import com.danielbohry.sort.FileDeduplicator;

import java.nio.file.Path;

public class DeduplicateCommand implements Command {

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: deduplicate <directory>");
        }

        Path targetDir = Path.of(args[0]);
        new FileDeduplicator().deduplicate(targetDir);
    }
}
