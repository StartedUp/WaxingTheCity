package com.waxthecity.service;

import com.waxthecity.model.RegBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Balaji on 8/12/17.
 */
@Service
public class PdfService {
    private static final Logger LOGGER= LoggerFactory.getLogger(PdfService.class.getName());
    @Value("${pdf.generated.path}")
    private String copyPdfDir;
    @Value("${pdf.source.path}")
    private String srcPdfDir;

    public void generatePdf(RegBean regBean) {
        LOGGER.info("generating pdf. Source pdf {}", srcPdfDir);
        // TODO: 8/12/17 copy source pdf to dest 
        // TODO: 8/12/17 read the copied pdf 
        // TODO: 8/12/17 edit the copied pdf and save
        try {
            if (copySourceFile(regBean)){

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean copySourceFile(RegBean regBean) throws IOException {
        File source = new File(srcPdfDir);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String dateString = sdf.format(date);
        File file=new File(copyPdfDir);
        if (!file.isDirectory())
            file.mkdir();
        File destination = new File(copyPdfDir+ dateString + regBean.getName()+".pdf");
        FileChannel src = new FileInputStream(source).getChannel();
        FileChannel dest = new FileOutputStream(destination).getChannel();
        dest.transferFrom(src, 0, src.size());
        return true;
    }

    public File fillDetails(RegBean bean){

    }
}
