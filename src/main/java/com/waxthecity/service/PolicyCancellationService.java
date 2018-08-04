package com.waxthecity.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import com.waxthecity.model.CancellationBean;
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
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;

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
    private String ACCESS_TOKEN;

    public void cancelPolicy(CancellationBean bean, String dateValue) {
        LOGGER.info("cancellation pdf Source pdf {}", srcPdfDir);
        PdfStamper stamper = null;
        PdfReader reader = null;
        try {
            createSignature(bean, dateValue);
        } catch (Exception e) {
            LOGGER.warn("Error creating image -->", e);
        }


        String pdfName = dateValue + "cancel" + bean.getFirstName() + bean.getLastName() + ".pdf";

        try {
            File file = copySourceFile(bean, dateValue);
            reader = new PdfReader(srcPdfDir);
            stamper = new PdfStamper(reader, new FileOutputStream(copyPdfDir + pdfName));
            AcroFields form = stamper.getAcroFields();
            //Setting acroform fields.

            String creDate = "Text1";
            String print = "Print";
            //String field=form.getField(fieldName);
            form.setGenerateAppearances(true);
            form.setFieldProperty(creDate, "textsize", new Float(0), null);
            form.setFieldProperty(print, "textsize", new Float(0), null);


            form.setField(creDate, dateValue);
            form.setField(print, bean.getFirstName()+bean.getLastName());


            LOGGER.info("date value : {}", dateValue);

            LOGGER.info("Pdf pages {}", reader.getNumberOfPages());
            com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(signImagePath + dateValue + "cancel" +bean.getFirstName() + bean.getLastName() + ".png");
            PdfImage stream = new PdfImage(image, "", null);
            stream.put(new PdfName("Sign"), new PdfName(dateValue + "cancel" + bean.getFirstName() + bean.getLastName() + ".pdf"));
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

    private void createSignature(CancellationBean bean, String dateValue) throws Exception {
        File imageFile = new File(
                signImagePath + dateValue + "cancel" + bean.getFirstName() + bean.getLastName() + ".png");
        if (!imageFile.exists()) {
            imageFile.getParentFile().mkdirs();
        }
        byte[] imagedata = DatatypeConverter.parseBase64Binary(
                bean.getImageData().substring(
                        bean.getImageData().indexOf(",") + 1)
        );
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        ImageIO.write(newBufferedImage, "png", imageFile);

    }


    public File copySourceFile(CancellationBean bean, String dateValue) throws IOException {
        File source = new File(srcPdfDir);
        LOGGER.error("Source file : {} ", source.exists());
        if (!source.exists()) {
            source.getParentFile().mkdirs();
        }
        File file = new File(copyPdfDir);
        if (!file.isDirectory())
            file.mkdirs();

        File destination = new File(copyPdfDir + dateValue + "cancel" + bean.getFirstName() + bean.getLastName() + ".pdf");
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
        try (InputStream in = new FileInputStream(copyPdfDir + pdfName)) {
            FileMetadata metadata = client.files().uploadBuilder("/Waxing the City-Victor/Intake Forms/" + pdfName)
                    .uploadAndFinish(in);
            LOGGER.info(" path of local {} ", ("/Waxing the City-Victor/Intake Forms/" + pdfName));
        }
        // List of files in the dropbox...
        ListFolderResult result = client.files().listFolder("/Waxing the City-Victor/Intake Forms/");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                LOGGER.info("PathLower {} ", metadata.getPathLower());
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }
    }

}
