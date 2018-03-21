package com.waxthecity.controller;

import com.waxthecity.model.CancellationBean;
import com.waxthecity.service.PolicyCancellationService;
import com.waxthecity.model.NewClientBean;
import com.waxthecity.service.NewClientService;
import com.waxthecity.service.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Balaji on 24/2/18.
 */
@Controller
public class ApplicationController {
    private static final Logger LOGGER= LoggerFactory.getLogger(ApplicationController.class.getName());
    @Autowired
    private PolicyCancellationService cancellationService;
    
    @Autowired
    private NewClientService newClientService;

    @RequestMapping("/")
    public String showNewClientForm() {
        LOGGER.info("Showing New Client registration form");
        return "registrationForm";
    }
    
    @PostMapping("/save")
    public String registerNewClient(@ModelAttribute NewClientBean bean,BindingResult result, Model model) {
        LOGGER.info("new client pdf generation {}", bean);
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
    	newClientService.register(bean, dateValue);
    	return "redirect://wtcforms.jvmhost.net/wtcPdf/generated/newClient/"+dateValue+"new"+bean.getFirstName()+
                bean.getLastName()+".pdf";
    }

    @GetMapping("/cancel")
    public String showPolicyCancellationForm() {
        LOGGER.info("Showing Policy cancellation form");
        return "policyCancellationForm";
    }

    @PostMapping("/cancel")
    public String cancel(@ModelAttribute CancellationBean bean, BindingResult result, Model model){
        LOGGER.info("cancel form submission : {} ", bean);
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
        cancellationService.cancelPolicy(bean,dateValue);
        return "redirect://wtcforms.jvmhost.net/wtcPdf/generated/cancellation/"+dateValue+"cancel"+bean.getFirstName()+
                bean.getLastName()+".pdf";
    }

}
