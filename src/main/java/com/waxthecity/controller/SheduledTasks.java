package com.waxthecity.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;

/**
 * Created by Balaji on 10/8/18.
 */
@Component
public class SheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(SheduledTasks.class.getName());

    @Value("${pdf.delete.path}")
    private String pdfPath;
    @Value("${image.delete.path}")
    private String imagePath;


    @Scheduled(cron = "31 46 19 * * ?")
    public void deleteFiles() {
        LOGGER.info("Delete method called : {}", LocalDateTime.now());
        LOGGER.info("Delete all generated files and images");
        File pdfFiles = new File(pdfPath);
        File imageFiles = new File(imagePath);
        recursiveDelete(pdfFiles);
        recursiveDelete(imageFiles);
    }

    public static void recursiveDelete(File file) {
        //to end the recursive loop
        if (!file.exists())
            return;

        //if directory, go inside and call recursively
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                //call recursively
                recursiveDelete(f);
            }
        }
        //call delete to delete files and empty directory
        file.delete();
        LOGGER.info("Deleted file/folder: {}",file.getAbsolutePath());
    }

}
