package com.danielbohry;

import com.danielbohry.command.Command;
import com.danielbohry.command.DeduplicateCommand;
import com.danielbohry.command.SortCommand;
import com.danielbohry.command.UnsortCommand;

import java.util.Map;

public class Main {

    private static final Map<String, Command> COMMANDS = Map.of(
        "sort", new SortCommand(),
        "unsort", new UnsortCommand(),
        "deduplicate", new DeduplicateCommand()
    );

    static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        String commandName = args[0];
        Command command = COMMANDS.get(commandName);
        if (command == null) {
            System.err.println("Unknown command: " + commandName);
            printUsage();
            System.exit(1);
        }

        String[] commandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, commandArgs, 0, commandArgs.length);

        try {
            command.execute(commandArgs);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.err.println("Usage: file-utils <command> [args]");
        System.err.println("Commands:");
        System.err.println("  sort <directory>     Sort files in <directory> into type-based subfolders");
        System.err.println("  unsort <directory>        Move files back from type-based subfolders into <directory> and remove them");
        System.err.println("  deduplicate <directory>   Find duplicate files (recursively) and move copies into <directory>/duplicated");
    }
}
