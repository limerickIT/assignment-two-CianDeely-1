/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.service;

import com.sd4.model.Beer;
import com.sd4.repository.BeerRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Cian
 */
@Service

public class BeerService {
     @Autowired
     
       private BeerRepository beerRepo;
     
    private List<Beer> beers;

     
         @PostConstruct
    public void BeerService() {
beers = (List<Beer>) beerRepo.findAll() ;
//brewerys = (List<Brewery>) breweryRepo.findAll();
    }
     
     
     
    public Page<Beer> findPaginated(Pageable pageable, String name) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Beer> list;
        beers = (List<Beer>) beerRepo.findByName(name);
        if (beers.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, beers.size());
            list = beers.subList(startItem, toIndex);
        }
           

        Page<Beer> beerPage
                          = new PageImpl<Beer>(list, PageRequest.of(currentPage, pageSize), beers.size());


        return beerPage;
    }

        // Add this method
   


    public Optional<Beer> findOne(Long id) {
        return beerRepo.findById(id);
    }

    public List<Beer> findAll() {
        return (List<Beer>) beerRepo.findAll();
    }
    
    public List<Beer> findByName(String name) {
        String name1 = name;    
        
        List<Beer> beerList;
        beerList = beerRepo.findByName(name1);
        return beerList;
    }

    public long count() {
        return beerRepo.count();
    }

    public void deleteByID(long beerID) {
        beerRepo.deleteById(beerID);
    }

    public void saveBeer(Beer a) {
        beerRepo.save(a);
        
    }  
    
    
}