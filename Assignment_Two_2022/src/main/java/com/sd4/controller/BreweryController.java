/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.sd4.model.Beer;
import com.sd4.model.Brewery;
import com.sd4.service.BeerService;
import com.sd4.service.BreweryService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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
    
        @GetMapping(value = "brewery/{id}", produces = MediaTypes.HAL_JSON_VALUE)
   public ResponseEntity<String> getOne(@PathVariable long id) throws ApiException, InterruptedException, IOException{
      Optional<Brewery> b = breweryService.findOne(id);
      if(!b.isPresent()){
          return new ResponseEntity(HttpStatus.NOT_FOUND);
      } else{
          Brewery brew = b.orElse(new Brewery());
          GeoApiContext context = new GeoApiContext.Builder() .apiKey("AIzaSyAIcevwYRy8c2SzfUOJKMKZwzVbCusKukg").build();
        GeocodingResult[] results =  GeocodingApi.geocode(context,
      brew.getAddress1() +", " + brew.getCity() + ", " + brew.getState()+ ", " + brew.getCountry()).await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(results[0].addressComponents));

// Invoke .shutdown() after your application is done making requests
        context.shutdown();
          
          Link selfLink =linkTo(BreweryController.class).slash("").withSelfRel();
                    Link allBreweriesLink = linkTo(methodOn(BreweryController.class).getAll()).withSelfRel();
          b.get().add(selfLink);
          return ResponseEntity.ok(gson.toJson(results[0].addressComponents));
      }
}
    
}
