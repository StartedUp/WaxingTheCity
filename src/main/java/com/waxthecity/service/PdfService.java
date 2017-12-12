package com.waxthecity.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.waxthecity.model.RegBean;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.*;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public void generatePdf(RegBean regBean) {
        LOGGER.info("generating pdf. Source pdf {}", srcPdfDir);
        // TODO: 8/12/17 copy source pdf to dest 
        // TODO: 8/12/17 read the copied pdf 
        // TODO: 8/12/17 edit the copied pdf and save
        PdfStamper stamper = null;
        PdfReader reader = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String dateValue = sdf.format(new Date());
            File file = copySourceFile(regBean);
            reader = new PdfReader(srcPdfDir);
            stamper = new PdfStamper(reader, new FileOutputStream(copyPdfDir + dateValue + regBean.getName() + ".pdf"));
            AcroFields form = stamper.getAcroFields();
            String fieldName = "Client Name";
            String list1 = "Please list";
            String time1 = "How longhow often";
            String list2 = "Please list_2";
            String time2 = "How longhow often_2";
            String illness = "Any other illnesscondition a medical professional is treating you for";
            String alergy = "Do you have any allergic reactions or allergies to flowerfruit extracts talcum powder or aspirin";
            String limitation = "Note any limitations on";
            String creDate = "Date";
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
                    regBean.isPregnant()?"On":"Off");
            form.setField("No_2",
                    regBean.isPregnant()?"Off":"On");
            stamper.setFormFlattening(true);
            stamper.close();
            PDDocument pdDocument = PDDocument.load(new File(srcPdfDir));
            PDDocumentCatalog pdCatalog = pdDocument.getDocumentCatalog();
            PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
            LOGGER.info("Printing the form names {}", pdAcroForm.getFields());
            for (PDField pdField : pdAcroForm.getFields()) {
                System.out.println(pdField);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
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


    public File copySourceFile(RegBean regBean) throws IOException {
        File source = new File(srcPdfDir);
        if (!source.exists()) {
            source.getParentFile().mkdir();
            //source.createNewFile();
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String dateString = sdf.format(date);
        File file = new File(copyPdfDir);
        if (!file.isDirectory())
            file.mkdir();
        /*if (!file.exists()) {
            file.getParentFile().mkdir();
           // file.createNewFile();
        }*/
        File destination = new File(copyPdfDir + dateString + regBean.getName() + ".pdf");
        FileChannel src = new FileInputStream(source).getChannel();
        FileChannel dest = new FileOutputStream(destination).getChannel();
        dest.transferFrom(src, 0, src.size());
        return destination;
    }

    public AcroFields fillTextFields(RegBean bean, AcroFields fields) throws IOException, DocumentException {
        for (String fieldName : fieldNames) {
            fields.setField(fieldName, bean.getName());
        }
        return fields;
    }
}
