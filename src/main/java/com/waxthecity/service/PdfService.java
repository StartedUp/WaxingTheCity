package com.waxthecity.service;

import com.waxthecity.model.RegBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Balaji on 8/12/17.
 */
@Service
public class PdfService {
    @Value("pdf.generated.path")
    private String copyPdfDir;
    @Value("pdf.source.path")
    private String srcPdfDir;

    public void generatePdf(RegBean regBean) {
        // TODO: 8/12/17 copy source pdf to dest 
        // TODO: 8/12/17 read the copied pdf 
        // TODO: 8/12/17 edit the copied pdf and save 

    }
}
