package io.ballerina.source.writer;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static io.ballerina.source.writer.BalWriterConstants.DEFAULT_FILENAME;

public class BalSourceWriter {

    public static String getSourceContent(SyntaxTree syntaxTree) {
        try {
            return Formatter.format(syntaxTree).toSourceCode();
        } catch (FormatterException e) {
            throw new RuntimeException(e);
        }

    }

    public static void writeBalSource(List<BalSourceFile> sourceFileList, Path outputPath) {
        for (BalSourceFile file : sourceFileList) {
            try {
                Path filePath = Paths.get(outputPath.resolve(file.getFileName()).toFile().getCanonicalPath());
                String fileContent = file.getContent();
                writeFile(filePath, fileContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Writes a file with content to specified {@code filePath}.
     *
     * @param filePath valid file path to write the content
     * @param content  content of the file
     * @throws IOException when a file operation fails
     */
    private static void writeFile(Path filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath.toString(), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

    public static void writeSingleBalSource(Path outputPath, SyntaxTree syntaxTree) {
        try {
            Path filePath = Paths.get(outputPath.resolve(DEFAULT_FILENAME).toFile().getCanonicalPath());
            writeFile(filePath, getSourceContent(syntaxTree));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
