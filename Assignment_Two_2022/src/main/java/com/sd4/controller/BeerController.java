/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.controller;

import com.sd4.model.Beer;
import com.sd4.service.BeerService;
import com.sd4.service.BreweryService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Cian
 */
@Controller
@RequestMapping("/beer")
public class BeerController {
    @Autowired
    private BeerService beerService;
    private BreweryService breweryService;
    
    @RequestMapping(value = "/searchByName", method = {RequestMethod.GET, RequestMethod.POST})
public String searchByNameSubmit(@ModelAttribute String name, Model model, @RequestParam("page") Optional<Integer> page, 
      @RequestParam("size") Optional<Integer> size){
    int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Beer> beerPage = beerService.findPaginated(PageRequest.of(currentPage - 1, pageSize), name);

        model.addAttribute("beerPage", beerPage);
        int totalPages = beerPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
       
    
   //Pageable firstPageWithTwoElements = PageRequest.of(0, 2);
   //Page<Beer> beersByName = beerService.findByName(name, firstPageWithTwoElements);
   //model.addAttribute("name", name, "aBeerList", beerService.findByName(name, pageable));
 //  model.addAttribute(beersByName);     
   return "nameResults";
}

    @RequestMapping(value = "/searchByID", method = {RequestMethod.GET, RequestMethod.POST})
public String searchByID (@ModelAttribute Long idSearch, Model model){
    Optional<Beer> beer = beerService.findOne(idSearch);
    Beer beer1 = beer.orElse(new Beer());
    model.addAttribute("beer", beer1);
    
    return "idResults";
    
}
        
    @RequestMapping(value = "/drilldownBeer", method = {RequestMethod.GET, RequestMethod.POST})
public String drilldownBeer (@ModelAttribute Long idSearch, Model model){
    Optional<Beer> beer = beerService.findOne(idSearch);
    Beer beer1 = beer.orElse(new Beer());
    model.addAttribute("beer", beer1);
    return "drilldownBeerResults";
    
}

@GetMapping("/displayAll")
public String displayAllPage(Model model){
    model.addAttribute("aBeerList", beerService.findAll());
    return "/viewAllBeers";
}
}
    
