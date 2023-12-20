package org.edec.utility.pdfViewer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@AllArgsConstructor
public class PdfSignSystemFile implements PdfSignFile {

    private File file;

    @Override
    public byte[] generateContent() {
        try {
            return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
