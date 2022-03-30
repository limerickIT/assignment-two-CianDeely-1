/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import com.sd4.model.Beer;
import com.sd4.model.Breweries_Geocode;
import com.sd4.model.Brewery;
import com.sd4.service.BeerService;
import com.sd4.service.Breweries_Geocode_Service;
import com.sd4.service.BreweryService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Cian
 */

@Controller
@RequestMapping("/brewery")
public class BreweryController {
     @Autowired
    private BeerService beerService;
    @Autowired
    private BreweryService breweryService;
    @Autowired
    private Breweries_Geocode_Service bcService;
    
    
     @GetMapping(value= "", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Brewery>> getAll()
    {
        List<Brewery> breweryList = breweryService.findAll();

        for(final Brewery brewery : breweryList){
            Link drillDownLink = linkTo(BreweryController.class).slash("/brewery").slash("drilldown").slash(brewery.getId()).withSelfRel();
            brewery.add(drillDownLink);
        }
        if(breweryList.isEmpty())
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        else{
            Link selfLink = linkTo(BeerController.class).slash("").withSelfRel();
          return ResponseEntity.ok(breweryList);
        }
}
    
        @GetMapping(value = "brewery/map/{id}")
   public ResponseEntity<String> getOne(@PathVariable long id) {
      Optional<Brewery> b = breweryService.findOne(id);
      if(!b.isPresent()){
          return new ResponseEntity(HttpStatus.NOT_FOUND);
      } else{
          Brewery brew = b.orElse(new Brewery());
          Breweries_Geocode bgCode = bcService.findByBreweryID(brew.getId());
          
       return ResponseEntity.ok
        ("<html> <body> <h1> " + brew.getName() 
       + "</h1> <h2> " + brew.getAddress1() + " " + brew.getAddress2() + "</h2> " 
       + " <iframe width=\"600\" height=\"450\" style=\"border:0\" loading=\"lazy\" "+"src=\"https://maps.google.com/maps?q=" 
       + brew.getName() + brew.getAddress1() + brew.getAddress2() + brew.getCity() + brew.getCountry() + "=&output=embed\">\"" );
      }
}
   
   
   
   
   @GetMapping(value = "brewery/qr/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
   public ResponseEntity<byte[]> getQR(@PathVariable long id) throws WriterException, IOException, FileNotFoundException, NotFoundException{
        Optional<Brewery> b = breweryService.findOne(id);
          if(!b.isPresent())
          {
               return null;
          }
          else
          {
          Brewery brew = b.orElse(new Brewery());
                 // The data that the QR code will contain
        String data = "MECARD:N:"+brew.getName() + ";" + "TEL:"+brew.getPhone() + ";" + "EMAIL:"+brew.getEmail()+ ";ADR:"+brew.getAddress1() + " " + brew.getAddress2()  + ";;";        

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray(); 
        // Encoding charset

    return ResponseEntity.ok(pngData);
      }          
    }
}
