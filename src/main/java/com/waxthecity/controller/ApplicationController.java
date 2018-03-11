package com.waxthecity.controller;

import com.waxthecity.service.NewRegistrationService;
import com.waxthecity.service.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    private NewRegistrationService service;

    @RequestMapping("/new")
    public String showNewClientForm() {
        LOGGER.info("Showing New Client registration form");
        return "registrationForm";
    }

    @GetMapping("/cancel")
    public String showPolicyCancellationForm() {
        LOGGER.info("Showing Policy cancellation form");
        return "policyCancellationForm";
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam("imageData") String imagedata){
        LOGGER.info("cancel form submission : {} ", imagedata);
        service.cancelPolicy(imagedata);
        return "waxingTheCityForm";
    }

}
