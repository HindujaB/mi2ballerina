package io.ballerina.source.writer;

public class BalSourceFile {
    private final String content;
    private final String fileName;
    private final String pkgName;

    public BalSourceFile(String content, String fileName, String pkgName) {
        this.content = content;
        this.fileName = fileName;
        this.pkgName = pkgName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }
}
