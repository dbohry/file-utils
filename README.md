# file-utils

CLI tool for organizing files in a directory.

## Commands

- `sort <directory>` — move files into type-based subfolders (images, documents, videos, audio, archives, code, other)
- `unsort <directory>` — move files back from those subfolders into `<directory>` and remove them
- `deduplicate <directory>` — find duplicate files (recursively) by content and move all but the oldest copy into `<directory>/duplicated`

## Usage

```
./gradlew run --args="sort /path/to/directory"
./gradlew run --args="unsort /path/to/directory"
./gradlew run --args="deduplicate /path/to/directory"
```
