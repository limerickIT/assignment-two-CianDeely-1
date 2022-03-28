/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.controller;

import com.sd4.model.Beer;
import com.sd4.service.BeerService;
import com.google.gson.Gson;
import com.sd4.model.Brewery;
import com.sd4.service.BreweryService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


/**
 *
 * @author Cian
 */
@Controller
@RequestMapping("/beer")
public class BeerController {
    @Autowired
    private BeerService beerService;
    @Autowired
    private BreweryService breweryService;
    
    private static final Gson gson = new Gson();


    @GetMapping(value= "", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Beer>> getAll()
    {
        List<Beer> beerList = beerService.findAll();
        
        for(final Beer beer : beerList){
            Link drillDownLink = linkTo(BeerController.class).slash("/beer").slash("drilldown").slash(beer.getId()).withSelfRel();
            beer.add(drillDownLink);
        }
        if(beerList.isEmpty())
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        else{
            Link selfLink = linkTo(BeerController.class).slash("").withSelfRel();
          return ResponseEntity.ok(beerList);
        }
}
    
    @GetMapping(value = "beer/{id}", produces = MediaTypes.HAL_JSON_VALUE)
   public ResponseEntity<Beer> getOne(@PathVariable long id){
      Optional<Beer> b = beerService.findOne(id);
      if(!b.isPresent()){
          return new ResponseEntity(HttpStatus.NOT_FOUND);
      } else{
          Link selfLink =linkTo(BeerController.class).slash("").withSelfRel();
                    Link allBeersLink = linkTo(methodOn(BeerController.class).getAll()).withSelfRel();
          b.get().add(selfLink);
          return ResponseEntity.ok(b.get());
      }
}
   
      @GetMapping(value = "beer/drilldown/{id}", produces = MediaTypes.HAL_JSON_VALUE)
   public ResponseEntity<String> drillDown(@PathVariable long id){
      Optional<Beer> b = beerService.findOne(id);
      if(!b.isPresent()){
          return new ResponseEntity(HttpStatus.NOT_FOUND);
      } else{
          Beer beer = b.orElse(new Beer());
          Long breweryID = b.get().getBrewery_id();
          System.out.println("PRINTING BREWERY ID " + breweryID);
          Optional<Brewery> brew = breweryService.findOne(breweryID);
          Brewery brewery = brew.orElse(new Brewery());
          
          String beerDrilldown;
          JSONObject json = new JSONObject();
          JSONArray array = new JSONArray();
          JSONObject item = new JSONObject();
          item.put("Description: ", beer.getDescription());
          item.put("Name: ", beer.getName());         
          item.put("Brewery Name:", brewery.getName());
          array.add(item);          
          json.put("Beer", array);
          beerDrilldown = json.toString();
         
          return ResponseEntity.ok(beerDrilldown);
      }
}

//   public ResponseEntity add(@RequestBody Beer b){
//       beerService.saveBeer(b);
//       return new ResponseEntity(HttpStatus.CREATED);
//   }
//        

}
    
