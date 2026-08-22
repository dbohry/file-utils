package com.danielbohry.command;

import com.danielbohry.sort.FileSorter;

import java.nio.file.Path;

public class SortCommand implements Command {

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: sort <directory>");
        }

        Path targetDir = Path.of(args[0]);
        new FileSorter().sort(targetDir);
    }
}
