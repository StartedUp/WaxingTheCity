package com.waxthecity.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Balaji on 11/3/18.
 */
public class Test {
    public static void main(String[] args) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        System.out.println(new Timestamp(System.currentTimeMillis()).getTime());
        long l= new Timestamp(System.currentTimeMillis()).getTime();
        System.out.println(sdf.format(l));
    }
}
