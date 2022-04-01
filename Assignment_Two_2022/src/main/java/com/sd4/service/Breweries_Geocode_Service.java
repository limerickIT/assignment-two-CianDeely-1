/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.service;

import com.sd4.model.Breweries_Geocode;
import com.sd4.repository.BreweryGeocodeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Cian
 */
@Service
public class Breweries_Geocode_Service {
      @Autowired
     
       private BreweryGeocodeRepository brewGeocodeRepo;

    public Optional<Breweries_Geocode> findOne(Long id) {
        return brewGeocodeRepo.findById(id);
        
    }
    
    public Breweries_Geocode findByBreweryID(Long brewID){
        Breweries_Geocode code = new Breweries_Geocode();
        List<Breweries_Geocode> brewGeoList = (List<Breweries_Geocode>) brewGeocodeRepo.findAll();
        for (Breweries_Geocode bgc : brewGeoList){
            if(bgc.getBrewery_id() == brewID){
                code = bgc;
            }
        }
        return code;
    }

    public List<Breweries_Geocode> findAll() {
        return (List<Breweries_Geocode>) brewGeocodeRepo.findAll();
    }
   
    public long count() {
        return brewGeocodeRepo.count();
    }

    public void deleteByID(long beerID) {
        brewGeocodeRepo.deleteById(beerID);
    }

    public void saveGeocode(Breweries_Geocode a) {
        brewGeocodeRepo.save(a);
        
    }  
}
