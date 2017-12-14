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
    @RequestMapping("/")
    public String showForm() {
        LOGGER.info("Showing waxing the city form");
        return "waxingTheCityForm";
    }

    @RequestMapping(value = "/generate" , method = RequestMethod.POST)
    public String generatePdf(@ModelAttribute RegBean regBean,
                              BindingResult result, Model model){
        LOGGER.info("Generating pdf {}", regBean);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String dateValue = sdf.format(new Date());
        if (result.hasErrors()){
            return "waxingTheCityForm";
        }
        pdfService.generatePdf(regBean);
        model.addAttribute("form", regBean)
                .addAttribute("date",dateValue);
        return "redirect";
    }
    @RequestMapping("/dropbox")
    public String drop() {
        LOGGER.info("dropbox waxing the city form");
        try {
            pdfService.putInDropbox();
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "waxingTheCityForm";
    }
}
have to go to the class for hibernate.. at 11.45
i don5 hav3 time ok