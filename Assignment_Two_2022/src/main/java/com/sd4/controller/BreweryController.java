/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.controller;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sd4.model.Breweries_Geocode;
import com.sd4.model.Brewery;
import com.sd4.service.BeerService;
import com.sd4.service.Breweries_Geocode_Service;
import com.sd4.service.BreweryService;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Cian
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@Validated
public class BreweryController {
    @Autowired
    private BeerService beerService;
    @Autowired
    private BreweryService breweryService;
    @Autowired
    private Breweries_Geocode_Service bcService;
    
    
     @GetMapping(value= "brewery", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Brewery>> getAll()
    {
        List<Brewery> breweryList = breweryService.findAll();

        for(final Brewery brewery : breweryList){
            Link drillDownLink = linkTo(BreweryController.class).slash("/brewery/").slash(brewery.getId()).withSelfRel();
            brewery.add(drillDownLink);
        }
        if(breweryList.isEmpty())
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        else{
            Link selfLink = linkTo(BeerController.class).slash("").withSelfRel();
          return ResponseEntity.ok(breweryList);
        }
}
    
    
     
    @GetMapping(value = "brewery/{id}", produces = MediaTypes.HAL_JSON_VALUE)
   public ResponseEntity<Brewery> getOne(@PathVariable @Positive long id){
      Optional<Brewery> b = breweryService.findOne(id);
      if(!b.isPresent()){
          return new ResponseEntity(HttpStatus.NOT_FOUND);
      } else{
          Link selfLink =linkTo(BreweryController.class).slash("brewery").withSelfRel();
                    Link allBreweriesLink = linkTo(methodOn(BeerController.class).getAll()).withSelfRel();
          b.get().add(selfLink);
          return ResponseEntity.ok(b.get());
      }
}
    
        @GetMapping(value = "brewery/map/{id}")
   public ResponseEntity<String> getMap(@PathVariable @Positive long id) {
      Optional<Brewery> b = breweryService.findOne(id);
      if(!b.isPresent()){
          return new ResponseEntity(HttpStatus.NOT_FOUND);
      } else{
          Brewery brew = b.orElse(new Brewery());
          Breweries_Geocode bgCode = bcService.findByBreweryID(brew.getId());
          
       return ResponseEntity.ok
        ("<html> <body> <h1> " + brew.getName() 
       + "</h1> <h2> " + brew.getAddress1() + " " + brew.getAddress2() + "</h2><br><h2>"+brew.getCity()+"</h2><br><h2>"+brew.getState()+"</h2><br><h2>"+brew.getCountry()+"</h2>"
       + " <iframe width=\"600\" height=\"450\" style=\"border:0\" loading=\"lazy\" "+"src=\"https://maps.google.com/maps?q=" 
       + brew.getName() + brew.getAddress1() + brew.getAddress2() + brew.getCity() + brew.getCountry() + "=&output=embed\">\"" );
      }
}
   
   @GetMapping(value = "brewery/qr/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
   public ResponseEntity<byte[]> getQR(@PathVariable @Positive long id) throws WriterException, IOException, FileNotFoundException, NotFoundException{
        Optional<Brewery> b = breweryService.findOne(id);
          if(!b.isPresent())
          {
               return null;
          }
          else
          {
          Brewery brew = b.orElse(new Brewery());
        String data = "BEGIN:VCARD\n VERSION:3.0\nFN:"+brew.getName() + "\n" + "TEL:"+brew.getPhone() + "\n" + "EMAIL:"+brew.getEmail()+ "\nADR:"+brew.getAddress1() + " " + brew.getAddress2()  + "\nURL:" + brew.getWebsite() + "\nEND:VCARD";        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray(); 

    return ResponseEntity.ok(pngData);
      }          
    }
    
    @PostMapping (value = "brewery/add/", consumes = "application/json")
    public ResponseEntity<String> addBrewery(@RequestBody @Valid Brewery brewery) {
        breweryService.saveBrewery(brewery);
        System.out.println("PRINTING ADDED BREWERY" + brewery.toString());
        return ResponseEntity.ok("Brewery data is valid");
    }
   
   
      @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@ExceptionHandler(ConstraintViolationException.class)
public Map<String, String> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();
     
    ex.getConstraintViolations().forEach(cv -> {
        errors.put("message", cv.getMessage());
        errors.put("path", (cv.getPropertyPath()).toString());
    }); 
 
    return errors;
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
@ExceptionHandler(MethodArgumentNotValidException.class)
public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
 
    ex.getBindingResult().getFieldErrors().forEach(error -> 
        errors.put(error.getField(), error.getDefaultMessage()));
     
    return errors;
}



}
