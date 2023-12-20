package org.edec.utility.pdfViewer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.edec.utility.pdfViewer.model.PdfSignFile;
import org.edec.utility.sign.service.SignService;

@Getter
@AllArgsConstructor
public class SimpleSignModel {
    private final PdfSignFile pdfSignFile;
    private final SignService signService;
}
