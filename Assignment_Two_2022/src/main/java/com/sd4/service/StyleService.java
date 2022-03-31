/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.service;

/**
 *
 * @author Cian
 */
 
import com.sd4.model.Brewery;
import com.sd4.model.Style;
import com.sd4.repository.BreweryRepository;
import com.sd4.repository.StyleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StyleService {
     @Autowired
    private StyleRepository styleRepo;   
     
    public Optional<Style> findOne(long id) {
        return styleRepo.findById(id);
    }

    public List<Style> findAll() {
        return (List<Style>) styleRepo.findAll();
    }
    
  
    public long count() {
        return styleRepo.count();
    }

    public void deleteByID(long beerID) {
        styleRepo.deleteById(beerID);
    }

    public void saveBrewery(Style a) {
        styleRepo.save(a);
        
    } 
}