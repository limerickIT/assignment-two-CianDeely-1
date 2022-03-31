/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.repository;

import com.sd4.model.Category;
import com.sd4.model.Style;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Cian
 */
@Repository
public interface StyleRepository extends PagingAndSortingRepository<Style, Long>   {

}
