package com.danielbohry.sort;

import java.util.Set;

public enum FileCategory {
    IMAGES("images", Set.of("jpg", "jpeg", "png", "gif", "bmp", "svg", "webp", "heic", "tiff", "ico")),
    DOCUMENTS("documents", Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md", "odt", "ods", "odp", "rtf", "csv")),
    VIDEOS("videos", Set.of("mp4", "mkv", "mov", "avi", "wmv", "flv", "webm", "m4v")),
    AUDIO("audio", Set.of("mp3", "wav", "flac", "aac", "ogg", "m4a", "wma")),
    ARCHIVES("archives", Set.of("zip", "rar", "7z", "tar", "gz", "bz2", "xz")),
    CODE("code", Set.of("java", "py", "js", "ts", "c", "cpp", "h", "go", "rs", "rb", "php", "html", "css", "json", "xml", "yml", "yaml", "sh")),
    OTHER("other", Set.of());

    private final String folderName;
    private final Set<String> extensions;

    FileCategory(String folderName, Set<String> extensions) {
        this.folderName = folderName;
        this.extensions = extensions;
    }

    public String folderName() {
        return folderName;
    }

    public static FileCategory fromExtension(String extension) {
        for (FileCategory category : values()) {
            if (category.extensions.contains(extension.toLowerCase())) {
                return category;
            }
        }
        return OTHER;
    }
}
