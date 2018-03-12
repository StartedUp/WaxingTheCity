package com.waxthecity.controller;

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
    	newClientService.register(bean);
    	return "waxingTheCityForm";
    }

    @GetMapping("/cancel")
    public String showPolicyCancellationForm() {
        LOGGER.info("Showing Policy cancellation form");
        return "policyCancellationForm";
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam("imageData") String imagedata){
        LOGGER.info("cancel form submission : {} ", imagedata);
        cancellationService.cancelPolicy(imagedata);
        return "waxingTheCityForm";
    }

}
