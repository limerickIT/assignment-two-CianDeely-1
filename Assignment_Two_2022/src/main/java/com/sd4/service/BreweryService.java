/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.service;

import com.sd4.model.Brewery;
import com.sd4.repository.BreweryRepository;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Cian
 */
@Service

public class BreweryService {
     @Autowired
     
       private BreweryRepository breweryRepo;
     
    private List<Brewery> brewerys;
     
         @PostConstruct
    public void breweryService() {
  
brewerys = (List<Brewery>) breweryRepo.findAll() ;
    }
     
     
     
        // Add this method
   


    public Optional<Brewery> findOne(Long id) {
        return breweryRepo.findById(id);
    }

    public List<Brewery> findAll() {
        return (List<Brewery>) breweryRepo.findAll();
    }
    
  
    public long count() {
        return breweryRepo.count();
    }

    public void deleteByID(long beerID) {
        breweryRepo.deleteById(beerID);
    }

    public void saveBeer(Brewery a) {
        breweryRepo.save(a);
        
    }  
    
    
}
