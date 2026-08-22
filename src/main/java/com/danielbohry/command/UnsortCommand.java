package com.danielbohry.command;

import com.danielbohry.sort.FileUnsorter;

import java.nio.file.Path;

public class UnsortCommand implements Command {

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: unsort <directory>");
        }

        Path targetDir = Path.of(args[0]);
        new FileUnsorter().unsort(targetDir);
    }
}
