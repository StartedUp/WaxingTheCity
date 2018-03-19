/**
 * 
 */
package com.waxthecity.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.waxthecity.model.NewClientBean;

/**
 * @author balaji
 *
 */
@Service
public class NewClientService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NewClientService.class.getName());
    @Value("${pdf.newclient.generated.path}")
    private String copyPdfDir;
    @Value("${pdf.newclient.source.path}")
    private String srcPdfDir;
    @Value("${pdf.newclient.sign.image.path}")
    private String signImagePath;
    @Value("${dropbox.access.token}")
    private String ACCESS_TOKEN ;

	
	public void register(NewClientBean bean) {
		
		LOGGER.info("registering new client pdf. Source pdf {}", srcPdfDir);
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
        LOGGER.info("New client : {} ", bean);


        try {
            createSignature(bean, dateValue);
        } catch (Exception e) {
            LOGGER.warn("Error creating image -->", e);
        }


        String pdfName=dateValue + bean.getFirstName() + ".pdf";

        try {
            File file = copySourceFile(bean, dateValue);
            reader = new PdfReader(srcPdfDir);
            stamper = new PdfStamper(reader, new FileOutputStream(copyPdfDir + pdfName));
            AcroFields form = stamper.getAcroFields();


            String today="Text";
            String firstName="Text11";
            String lastName="Text12";
            String email = "Text13";
            String phone = "Text14";
            String contact = "Text15";
            String service = "Text16";
            String dob = "Text17";
            String address = "Text18";
            String city = "Text19";
            String state = "20";
            String pin = "21";
            String paper = "22";
            String thankfulPerson = "23";
            String illness = ""; //32 signature field
            String acneList = "24";
            String acnePeriod = "25";
            String bleachingList = "26";
            String bleachingPeriod = "27";
            String allergyYes = "28";
            String aspirin = "29";
            String minor = "30";
            String limitation = "31";
            String newDate = "33";

            form.setFieldProperty(firstName, "textsize", new Float(0), null);
            form.setFieldProperty(lastName, "textsize", new Float(0), null);
            form.setFieldProperty(email, "textsize", new Float(0), null);
            form.setFieldProperty(phone, "textsize", new Float(0), null);
            form.setFieldProperty(contact, "textsize", new Float(0), null);
            form.setFieldProperty(service, "textsize", new Float(0), null);
            form.setFieldProperty(dob, "textsize", new Float(0), null);
            form.setFieldProperty(address, "textsize", new Float(0), null);
            form.setFieldProperty(city, "textsize", new Float(0), null);
            form.setFieldProperty(state, "textsize", new Float(0), null);
            form.setFieldProperty(pin, "textsize", new Float(0), null);
            form.setFieldProperty(paper, "textsize", new Float(0), null);
            form.setFieldProperty(thankfulPerson, "textsize", new Float(0), null);
            form.setFieldProperty(allergyYes, "textsize", new Float(0), null);
            form.setFieldProperty(acneList, "textsize", new Float(0), null);
            form.setFieldProperty(acnePeriod, "textsize", new Float(0), null);
            form.setFieldProperty(bleachingList, "textsize", new Float(0), null);
            form.setFieldProperty(bleachingPeriod, "textsize", new Float(0), null);
            form.setFieldProperty(illness, "textsize", new Float(0), null);
            form.setFieldProperty(aspirin, "textsize", new Float(0), null);
            form.setFieldProperty(minor, "textsize", new Float(0), null);
            form.setFieldProperty(limitation, "textsize", new Float(0), null);
            form.setFieldProperty(today, "textsize", new Float(0), null);

            form.setField(firstName, bean.getFirstName());
            form.setField(lastName, bean.getLastName());
            form.setField(email, bean.getEmail());
            form.setField(phone, bean.getPhoneNo());
            form.setField(contact, bean.getPreferredContact());
            form.setField(service, bean.getServiceProvider());
            form.setField(dob, bean.getDob());
            form.setField(address, bean.getAddress());
            form.setField(city, bean.getCity());
            form.setField(state, bean.getState());
            form.setField(pin, bean.getZip());
            form.setField(paper, bean.getMagazine());
            form.setField(thankfulPerson, bean.getThanfulPerson());
            form.setField(allergyYes, bean.getOtherAllergy());
            form.setField(acnePeriod, bean.getAcnePeriod());
            form.setField(acneList, bean.getAcneMedicines());
            form.setField(bleachingList, bean.getBleachingAgent());
            form.setField(bleachingPeriod, bean.getBleachingAgentPeriod());
            form.setField(illness, bean.getIllness());
            form.setField(aspirin, bean.getAspirin());
            form.setField(minor, dateValue);
            form.setField(limitation, bean.getLimitation());
            form.setField(today, dateValue);
            form.setField(newDate, dateValue);

            form.setField("Check Box211", bean.isActivateEmail()?"On":"Off");
            form.setField("Check Box11", bean.isActivateEmail()?"Off":"On");
            form.setField("Check 12", bean.isActivateSMS()?"On":"Off");
            form.setField("Check Box13", bean.isActivateSMS()?"Off":"On");
            form.setField("Check 14", bean.isGender()?"On":"Off");
            form.setField("Check Box1231", bean.isGender()?"Off":"On");

            form.setField("Check Box14", bean.isWalkIn()?"On":"Off");
            form.setField("Check Box15", bean.isCoupon()?"On":"Off");
            form.setField("Check Box16", bean.isPrintAd()?"On":"Off");
            form.setField("Check Box17", bean.isReferral()?"On":"Off");
            form.setField("Check Box18", bean.isInternet()?"On":"Off");
            form.setField("Check Box19", bean.isFlyer()?"On":"Off");
            form.setField("Check Box1813613", bean.isRadio()?"On":"Off");
            form.setField("Check Box152121", bean.isReturnCustomer()?"On":"Off");
            form.setField("Check Box1921210", bean.isDirectMail()?"On":"Off");
            form.setField("Check Box18521521", bean.isOther()?"On":"Off");

            form.setField("Check Box20", bean.isIngrownHairs()?"On":"Off");
            form.setField("Check Box21", bean.isIngrownHairs()?"Off":"On");
            form.setField("Check Box22", bean.isScarring()?"On":"Off");
            form.setField("Check Box23", bean.isScarring()?"Off":"On");
            form.setField("Check Box24", bean.isBumps()?"On":"Off");
            form.setField("Check Box25", bean.isBumps()?"Off":"On");
            form.setField("Check Box26", bean.isHyperpigmentation()?"On":"Off");
            form.setField("Check Box27", bean.isHyperpigmentation()?"Off":"On");
            form.setField("Check 28", bean.isBruising()?"On":"Off");
            form.setField("Check Box29", bean.isBruising()?"Off":"On");
            form.setField("Check Box30", bean.isAllergy()?"On":"Off");
            form.setField("Check Box31", bean.isAllergy()?"Off":"On");
            form.setField("Check Box32", bean.isAllergy()?"On":"Off");
            form.setField("Check Box33", bean.isAllergy()?"Off":"On");
            form.setField("Check Box34", bean.isDiabetic()?"On":"Off");
            form.setField("Check Box35", bean.isDiabetic()?"Off":"On");
            form.setField("Check Box36", bean.isPregnant()?"On":"Off");
            form.setField("Check Box37", bean.isPregnant()?"Off":"On");

            form.setField("Check Box40", bean.isSunburned()?"On":"Off");
            form.setField("Check Box41", bean.isTanningBooth()?"On":"Off");
            form.setField("Check Box42", bean.isHerpesSimplex()?"On":"Off");
            form.setField("Check Box43", bean.isLaserSkin()?"On":"Off");
            form.setField("Check Box44", bean.isPhysicianAdministeredPeel()?"On":"Off");
            form.setField("Check Box45", bean.isExtraPrecaution()?"On":"Off");
            form.setField("Check Box46", bean.isWaitingTime()?"On":"Off");
            form.setField("Check Box47", bean.isNoHotTubs()?"On":"Off");
            form.setField("Check Box48", bean.isNoAbrasives()?"On":"Off");
            form.setField("Check Box49", bean.isNoDeodorants()?"On":"Off");
            form.setField("Check Box50", bean.isExtraSensitivity()?"On":"Off");

            form.setField("Check Box60", bean.isBrowShaping()?"On":"Off");
            form.setField("Check Box61", bean.isLip()?"On":"Off");
            form.setField("Check 62", bean.isChin()?"On":"Off");
            form.setField("Check 63", bean.isSideburns()?"On":"Off");
            form.setField("Check 64", bean.isNose()?"On":"Off");
            form.setField("Check 65", bean.isEars()?"On":"Off");
            form.setField("Check B65", bean.isLashTint()?"On":"Off");
            form.setField("Check 66", bean.isBrowTint()?"On":"Off");
            form.setField("Check 67", bean.isFullLeg()?"On":"Off");
            form.setField("Check 68", bean.isHalfLeg()?"On":"Off");
            form.setField("Check 69", bean.isFullArm()?"On":"Off");
            form.setField("Check 70", bean.isHalfArm()?"On":"Off");
            form.setField("Check 71", bean.isBikini()?"On":"Off");
            form.setField("Check 72", bean.isModifiedBikini()?"On":"Off");
            form.setField("Check 73", bean.isBrazilianBikini()?"On":"Off");
            form.setField("Check74", bean.isButtocks()?"On":"Off");
            form.setField("Check 75", bean.isInnerBackSide()?"On":"Off");
            form.setField("Check76", bean.isUnderArm()?"On":"Off");
            form.setField("Check 77", bean.isFullBack()?"On":"Off");
            form.setField("Check 78", bean.isHalfBack()?"On":"Off");
            form.setField("Check 79", bean.isChest()?"On":"Off");
            form.setField("Check 80", bean.isAbdomen()?"On":"Off");
            form.setField("Check 81", bean.isNeck()?"On":"Off");
            form.setField("Check 82", bean.isBasicBrazilianMen()?"On":"Off");
            form.setField("Check 83", bean.isModifiedBrazilianMen()?"On":"Off");
            form.setField("Check 84", bean.isFullBrazilianMen()?"On":"Off");
            form.setField("Check 85", bean.isBrowShapingLip()?"On":"Off");
            form.setField("Check 86", bean.isFullLegBikini()?"On":"Off");
            form.setField("Check 88", bean.isLashBrowTint()?"On":"Off");
            form.setField("Check 89", bean.isFullFace()?"On":"Off");
            form.setField("Check 90", bean.isMenMasculineTailoring()?"On":"Off");
            form.setField("Check 91", bean.isMenMaintenanceBrow()?"On":"Off");
            form.setField("Check 92", bean.isMenFacialGroomingCombo()?"On":"Off");



            // String field=form.getField(fieldName);
            form.setGenerateAppearances(true);


            form.setFieldProperty(illness, "textsize", new Float(0), null);
            form.setFieldProperty(limitation, "textsize", new Float(0), null);

            /*form.setField(fieldName, regBean.getName());
            form.setField(list1, regBean.getAcneMedicines());
            form.setField(time1, regBean.getAcnePeriod());
            form.setField(list2, regBean.getBleachingAgent());
            form.setField(time2, regBean.getBleachingAgentPeriod());
            form.setField(illness, regBean.getIllness());
            form.setField(alergy, regBean.getAlergy());
            form.setField(limitation, bean.getLimitation());*/
            String diabetic[] = form.getAppearanceStates("Are you diabetic Yes");
            LOGGER.info("Diabetic yes state {}", diabetic);
            form.setField("Are you diabetic Yes", bean.isDiabetic()?"On":"Off");
            form.setField("No", bean.isDiabetic()?"Off":"On");
            form.setField("re youcould you be pregnant Yes",
                    bean.isPregnant() ?"On":"Off");
            form.setField("No_3",
                    bean.isPregnant()?"Off":"On");

            form.setField("Brow Shaping", bean.isBrowShaping() ?"On":"Off");
            form.setField("Nose", bean.isNose() ?"On":"Off");
            form.setField("Full Leg", bean.isFullLeg() ?"On":"Off");
            form.setField("Bikini", bean.isBikini() ?"On":"Off");
            form.setField("Full Back", bean.isFullBack() ?"On":"Off");
            form.setField("Basic Brazilian men", bean.isBasicBrazilianMen() ?"On":"Off");
            form.setField("Brow Shaping  Lip", bean.isBrowShapingLip() ?"On":"Off");

            form.setField("Mens Masculine Tailoring", bean.isMenMasculineTailoring() ?"On":"Off");

            form.setField("Lip", bean.isLip()  ?"On":"Off");
            form.setField("Ears", bean.isEars() ?"On":"Off");
            form.setField("Half Leg", bean.isHalfLeg()  ?"On":"Off");
            form.setField("Modified Bikini", bean.isModifiedBikini() ?"On":"Off");
            form.setField("Half Back", bean.isHalfBack() ?"On":"Off");
            form.setField("Modified Brazilian men", bean.isModifiedBrazilianMen() ?"On":"Off");
            form.setField("Full Leg  Bikini", bean.isFullLegBikini() ?"On":"Off");
            form.setField("Mens Maintenance Brow", bean.isMenMaintenanceBrow() ?"On":"Off");
            form.setField("Chin", bean.isChin() ?"On":"Off");
            form.setField("Lash Tint", bean.isLashTint() ?"On":"Off");
            form.setField("Full Arm", bean.isFullArm() ?"On":"Off");
            form.setField("Brazilian Bikini", bean.isBrazilianBikini() ?"On":"Off");
            form.setField("Chest", bean.isChest() ?"On":"Off");
            form.setField("Full Brazilian men", bean.isFullBrazilianMen() ?"On":"Off");
            form.setField("Lash Brow  Tint", bean.isLashBrowTint() ?"On":"Off");
            form.setField("Mens Facial Grooming Combo", bean.isMenFacialGroomingCombo() ?"On":"Off");
            form.setField("Sideburns", bean.isSideburns() ?"On":"Off");
            form.setField("Brow Tint", bean.isBrowTint() ?"On":"Off");
            form.setField("Half Arm", bean.isHalfArm() ?"On":"Off");
            form.setField("Buttocks", bean.isButtocks() ?"On":"Off");
            form.setField("Abdomen", bean.isAbdomen() ?"On":"Off");
            form.setField("Full Face no brows", bean.isFullFace() ?"On":"Off");
            form.setField("Tween Brow Shaping", bean.isTweenBrowShaping() ?"On":"Off");
            form.setField("Under Arm", bean.isUnderArm() ?"On":"Off");
            form.setField("Inner Backside", bean.isInnerBackSide() ?"On":"Off");
            form.setField("Neck", bean.isNeck() ?"On":"Off");

            LOGGER.info("Pdf pages {}",reader.getNumberOfPages());
            Image image = Image.getInstance(signImagePath + dateValue + bean.getFirstName() + ".png");
            PdfImage stream = new PdfImage(image, "", null);
            stream.put(new PdfName("Sign"), new PdfName(dateValue + bean.getFirstName() + ".pdf"));
            PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
            image.setDirectReference(ref.getIndirectReference());
            image.setAbsolutePosition(85, 525);
            image.scaleAbsolute(250, 20);
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
            //putInDropbox(pdfName);
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

    private void createSignature(NewClientBean bean, String dateValue) throws Exception{
        File imageFile = new File(
                signImagePath+dateValue+bean.getFirstName()+".png");
        if (!imageFile.exists()) {
            imageFile.getParentFile().mkdir();
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

    public File copySourceFile(NewClientBean bean, String dateValue) throws IOException {
        File source = new File(srcPdfDir);
        if (!source.exists()) {
            source.getParentFile().mkdir();
        }
        File file = new File(copyPdfDir);
        if (!file.isDirectory())
            file.mkdir();

        File destination = new File(copyPdfDir + dateValue + bean.getFirstName() + ".pdf");
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


