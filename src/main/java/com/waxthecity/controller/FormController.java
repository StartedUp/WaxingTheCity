package com.waxthecity.controller;

import com.dropbox.core.DbxException;
import com.waxthecity.model.RegBean;
import com.waxthecity.service.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Balaji on 7/12/17.
 */
@Controller
public class FormController {
    private static final Logger LOGGER= LoggerFactory.getLogger(FormController.class.getName());
    @Autowired
    private PdfService pdfService;
    @RequestMapping("/register")
    public String showForm() {
        LOGGER.info("Showing waxing the city form");
        return "waxingTheCityForm";
    }

    @RequestMapping(value = "/generate" , method = RequestMethod.POST)
    public String generatePdf(@ModelAttribute RegBean regBean,
                              BindingResult result, Model model){
        LOGGER.info("Generating pdf {}", regBean);
        /*SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String dateValue = sdf.format(new Date());*/
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

        if (result.hasErrors()){
            return "waxingTheCityForm";
        }
        pdfService.generatePdf(regBean);
        model.addAttribute("form", regBean)
                .addAttribute("date",dateValue);
        return "redirect://wtcforms.jvmhost.net/wtcPdf/generated/"+dateValue+regBean.getName()+".pdf";
    }
}
