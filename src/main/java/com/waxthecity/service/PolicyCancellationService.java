package com.waxthecity.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.waxthecity.model.RegBean;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Balaji on 11/3/18.
 */
@Service
public class PolicyCancellationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyCancellationService.class.getName());

    @Value("${pdf.cancellation.source.path}")
    private String srcPdfDir;
    @Value("${pdf.cancellation.generated.path}")
    private String copyPdfDir;
    @Value("${pdf.cancellation.sign.image.path}")
    private String signImagePath;
    @Value("${dropbox.access.token}")
    private String ACCESS_TOKEN ;

    public void cancelPolicy(String imageData){
        LOGGER.info("cancellation pdf Source pdf {}", srcPdfDir);
        PdfStamper stamper = null;
        PdfReader reader = null;
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        String systemDate =new Date().toString(); //IST Time
        LOGGER.info("IST time {} ",systemDate);
        Date date = null;
        try {
            date = dateTimeFormat.parse(systemDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("America/New_York"));
        String dateValue = dateFormat.format(date);
        LOGGER.info("EST date {} ",dateValue);

        //For logger information
        DateFormat da=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        da.setTimeZone(java.util.TimeZone.getTimeZone("America/New_York"));
        LOGGER.info("EST time {} ",da.format(date));

        //Time stamp
        long timeStamp=new Timestamp(System.currentTimeMillis()).getTime();



        try {
            createSignature(imageData, dateValue, timeStamp);
        } catch (Exception e) {
            LOGGER.warn("Error creating image -->", e);
        }


        String pdfName=dateValue + timeStamp + ".pdf";

        try {
            File file = copySourceFile(timeStamp, dateValue);
            reader = new PdfReader(srcPdfDir);
            stamper = new PdfStamper(reader, new FileOutputStream(copyPdfDir + pdfName));
            AcroFields form = stamper.getAcroFields();

            String creDate = "Date";
            String print = "Print";
            //String field=form.getField(fieldName);
            form.setGenerateAppearances(true);
            form.setFieldProperty(creDate, "setfflags", PdfFormField.TEXT_UNICODE, null);
            form.setFieldProperty(creDate, "textsize", new Float(0), null);
            form.setFieldProperty(print, "textsize", new Float(0), null);
            form.setField(creDate, dateValue);
            form.setField(print, dateValue);
            LOGGER.info("date value : {}", dateValue);

            LOGGER.info("Pdf pages {}",reader.getNumberOfPages());
            com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(signImagePath + dateValue + timeStamp + ".png");
            PdfImage stream = new PdfImage(image, "", null);
            stream.put(new PdfName("Sign"), new PdfName(dateValue + timeStamp + ".pdf"));
            PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
            image.setDirectReference(ref.getIndirectReference());
            image.setAbsolutePosition(218, 512);
            image.scaleAbsolute(85, 19);
            PdfContentByte over = stamper.getOverContent(1);
            over.addImage(image);
            stamper.setFormFlattening(true);
            stamper.close();
            // getting pdf field names for our reference
            PDDocument pdDocument = PDDocument.load(new File(srcPdfDir));
            PDDocumentCatalog pdCatalog = pdDocument.getDocumentCatalog();
            PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
            LOGGER.info("Printing the form names {}", pdAcroForm.getFields());
            for (PDField pdField : pdAcroForm.getFields()) {
                System.out.println(pdField);
            }
            // to upload into dropbox automatically
            putInDropbox(pdfName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stamper != null) {
                try {
                    stamper.close();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                reader.close();
            }
        }

    }

    private void createSignature(String imageData, String dateValue,long timeStamp) throws Exception{
        File imageFile = new File(
                signImagePath+dateValue+timeStamp+".png");
        if (!imageFile.exists()) {
            imageFile.getParentFile().mkdir();
        }
        byte[] imagedata = DatatypeConverter.parseBase64Binary(
                imageData.substring(
                        imageData.indexOf(",") + 1)
        );
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.BLUE, null);
        ImageIO.write(bufferedImage, "png", imageFile);

    }

    public File copySourceFile(long timeStamp, String dateValue) throws IOException {
        File source = new File(srcPdfDir);
        LOGGER.error("Source file : {} ",source.exists());
        if (!source.exists()) {
            source.getParentFile().mkdir();
        }
        File file = new File(copyPdfDir);
        if (!file.isDirectory())
            file.mkdir();

        File destination = new File(copyPdfDir + dateValue + timeStamp + ".pdf");
        FileChannel src = new FileInputStream(source).getChannel();
        FileChannel dest = new FileOutputStream(destination).getChannel();
        dest.transferFrom(src, 0, src.size());
        return destination;
    }

    public void putInDropbox(String pdfName) throws DbxException, IOException {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/Waxing the City-Victo/Intake Forms", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        // Get current account info
        FullAccount account = client.users().getCurrentAccount();
        LOGGER.info("drop box account info {} ", account.getName().getDisplayName());

        // Upload "test.txt" to Dropbox
        try (InputStream in = new FileInputStream(copyPdfDir+pdfName)) {
            FileMetadata metadata = client.files().uploadBuilder("/Waxing the City-Victor/Intake Forms/"+pdfName)
                    .uploadAndFinish(in);
            LOGGER.info(" path of local {} ",("/Waxing the City-Victor/Intake Forms/"+pdfName));
        }
        // List of files in the dropbox...
        ListFolderResult result = client.files().listFolder("/Waxing the City-Victor/Intake Forms/");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                LOGGER.info("PathLower {} ",metadata.getPathLower());
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }
    }

}
