package com.waxthecity.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
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
import java.awt.image.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Balaji on 8/12/17.
 */
@Service
public class PdfService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfService.class.getName());
    @Value("${pdf.generated.path}")
    private String copyPdfDir;
    @Value("${pdf.source.path}")
    private String srcPdfDir;
    @Value("${pdf.field.names}")
    private List<String> fieldNames;
    @Value("${pdf.sign.image.path}")
    private String signImagePath;
    @Value("${dropbox.access.token}")
    private String ACCESS_TOKEN ;

    public void generatePdf(RegBean regBean) {
        LOGGER.info("generating pdf. Source pdf {}", srcPdfDir);
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


        try {
            createSignature(regBean, dateValue);
        } catch (Exception e) {
            LOGGER.warn("Error creating image -->", e);
        }


        String pdfName=dateValue + regBean.getName() + ".pdf";

        try {
            File file = copySourceFile(regBean, dateValue);
            reader = new PdfReader(srcPdfDir);
            stamper = new PdfStamper(reader, new FileOutputStream(copyPdfDir + pdfName));
            AcroFields form = stamper.getAcroFields();
            String fieldName = "Client Name";
            String list1 = "Please list";
            String time1 = "How longhow often";
            String list2 = "Please list_2";
            String time2 = "How longhow often_2";
            String illness = "Any other illnesscondition a medical professional is treating you for";
            String alergy = "Do you have any allergic reactions or allergies to flowerfruit extracts talcum powder or aspirin";
            String limitation = "Note any limitations on";
            String creDate = "Date.0";
            // String field=form.getField(fieldName);
            form.setGenerateAppearances(true);
            LOGGER.info("List {}", fieldNames);

            form.setFieldProperty(fieldName, "textsize", new Float(0), null);
            form.setFieldProperty(list1, "textsize", new Float(0), null);
            form.setFieldProperty(time1, "textsize", new Float(0), null);
            form.setFieldProperty(list2, "textsize", new Float(0), null);
            form.setFieldProperty(time2, "textsize", new Float(0), null);
            form.setFieldProperty(illness, "textsize", new Float(0), null);
            form.setFieldProperty(alergy, "textsize", new Float(0), null);
            form.setFieldProperty(limitation, "textsize", new Float(0), null);
            form.setFieldProperty(creDate, "textsize", new Float(0), null);

            form.setField(fieldName, regBean.getName());
            form.setField(list1, regBean.getAcneMedicines());
            form.setField(time1, regBean.getAcnePeriod());
            form.setField(list2, regBean.getBleachingAgent());
            form.setField(time2, regBean.getBleachingAgentPeriod());
            form.setField(illness, regBean.getIllness());
            form.setField(alergy, regBean.getAlergy());
            form.setField(limitation, regBean.getLimitation());
            form.setField(creDate, dateValue);
            String diabetic[] = form.getAppearanceStates("Are you diabetic Yes");
            LOGGER.info("Diabetic yes state {}", diabetic);
            form.setField("Are you diabetic Yes", regBean.isDiabetic()?"On":"Off");
            form.setField("No", regBean.isDiabetic()?"Off":"On");
            form.setField("re youcould you be pregnant Yes",
                    regBean.isPregnant() ?"On":"Off");
            form.setField("No_2",
                    regBean.isPregnant()?"Off":"On");

            form.setField("Brow Shaping", regBean.isBrowShaping() ?"On":"Off");
            form.setField("Nose", regBean.isNose() ?"On":"Off");
            form.setField("Full Leg", regBean.isFullLeg() ?"On":"Off");
            form.setField("Bikini", regBean.isBikini() ?"On":"Off");
            form.setField("Full Back", regBean.isFullBack() ?"On":"Off");
            form.setField("Basic Brazilian men", regBean.isBasicBrazilianMen() ?"On":"Off");
            form.setField("Brow Shaping  Lip", regBean.isBrowShapingLip() ?"On":"Off");

            form.setField("Mens Masculine Tailoring", regBean.isMenMasculineTailoring() ?"On":"Off");

            form.setField("Lip", regBean.isLip()  ?"On":"Off");
            form.setField("Ears", regBean.isEars() ?"On":"Off");
            form.setField("Half Leg", regBean.isHalfLeg()  ?"On":"Off");
            form.setField("Modified Bikini", regBean.isModifiedBikini() ?"On":"Off");
            form.setField("Half Back", regBean.isHalfBack() ?"On":"Off");
            form.setField("Modified Brazilian men", regBean.isModifiedBrazilianMen() ?"On":"Off");
            form.setField("Full Leg  Bikini", regBean.isFullLegBikini() ?"On":"Off");
            form.setField("Mens Maintenance Brow", regBean.isMenMaintenanceBrow() ?"On":"Off");
            form.setField("Chin", regBean.isChin() ?"On":"Off");
            form.setField("Lash Tint", regBean.isLashTint() ?"On":"Off");
            form.setField("Full Arm", regBean.isFullArm() ?"On":"Off");
            form.setField("Brazilian Bikini", regBean.isBrazilianBikini() ?"On":"Off");
            form.setField("Chest", regBean.isChest() ?"On":"Off");
            form.setField("Full Brazilian men", regBean.isFullBrazilianMen() ?"On":"Off");
            form.setField("Lash Brow  Tint", regBean.isLashBrowTint() ?"On":"Off");
            form.setField("Mens Facial Grooming Combo", regBean.isMenFacialGroomingCombo() ?"On":"Off");
            form.setField("Sideburns", regBean.isSideburns() ?"On":"Off");
            form.setField("Brow Tint", regBean.isBrowTint() ?"On":"Off");
            form.setField("Half Arm", regBean.isHalfArm() ?"On":"Off");
            form.setField("Buttocks", regBean.isButtocks() ?"On":"Off");
            form.setField("Abdomen", regBean.isAbdomen() ?"On":"Off");
            form.setField("Other", regBean.isOther() ?"On":"Off");
            form.setField("Full Face no brows", regBean.isFullFace() ?"On":"Off");
            form.setField("Tween Brow Shaping", regBean.isTweenBrowShaping() ?"On":"Off");
            form.setField("Under Arm", regBean.isUnderArm() ?"On":"Off");
            form.setField("Inner Backside", regBean.isInnerBackSide() ?"On":"Off");
            form.setField("Neck", regBean.isNeck() ?"On":"Off");

            LOGGER.info("Pdf pages {}",reader.getNumberOfPages());
            Image image = Image.getInstance(signImagePath + dateValue + regBean.getName() + ".png");
            PdfImage stream = new PdfImage(image, "", null);
            stream.put(new PdfName("Sign"), new PdfName(dateValue + regBean.getName() + ".pdf"));
            PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
            image.setDirectReference(ref.getIndirectReference());
            image.setAbsolutePosition(100, 437);
            image.scaleAbsolute(200, 20);
            PdfContentByte over = stamper.getOverContent(2);
            over.addImage(image);
            stamper.setFormFlattening(true);
            stamper.close();
            // getting pdf field names for our reference
            PDDocument pdDocument = PDDocument.load(new File(srcPdfDir));
            PDDocumentCatalog pdCatalog = pdDocument.getDocumentCatalog();
            PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
            LOGGER.info("Printing the form names {}", pdAcroForm.getFields());
            LOGGER.info("Printing the form names {}", pdAcroForm.getFields());
            LOGGER.info("Printing the form names {}", pdAcroForm.getFields());
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

    private void createSignature(RegBean regBean, String dateValue) throws Exception{
        File imageFile = new File(
                signImagePath+dateValue+regBean.getName()+".png");
        if (!imageFile.exists()) {
            imageFile.getParentFile().mkdir();
        }
        byte[] imagedata = DatatypeConverter.parseBase64Binary(
                regBean.getImageData().substring(
                        regBean.getImageData().indexOf(",") + 1)
        );
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        ImageIO.write(newBufferedImage, "png", imageFile);

    }

    public File copySourceFile(RegBean regBean, String dateValue) throws IOException {
        File source = new File(srcPdfDir);
        if (!source.exists()) {
            source.getParentFile().mkdir();
        }
        File file = new File(copyPdfDir);
        if (!file.isDirectory())
            file.mkdir();

        File destination = new File(copyPdfDir + dateValue + regBean.getName() + ".pdf");
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

