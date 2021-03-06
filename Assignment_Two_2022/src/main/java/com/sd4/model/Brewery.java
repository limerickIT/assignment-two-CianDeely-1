/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd4.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

/**
 *
 * @author Alan.Ryan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Brewery extends RepresentationModel<Beer> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotEmpty(message = "Brewery name is required")
    private String name;
    @NotEmpty(message = "Address line 1 is required")
    private String address1;
    @NotEmpty(message = "Address line 2 is required")
    private String address2;
    @NotEmpty(message = "City is required")
    private String city;
    @NotEmpty(message = "State is required")
    private String state;
    private String code;
    @NotEmpty(message = "Country is required")
    private String country;
    @NotEmpty(message = "Phone number is required")
   @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$",
             message="Mobile number is invalid")
    private String phone;
    private String website;
    private String image;
    
    @Lob
    private String description;
    
    private Integer add_user;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date last_mod;
    
    private Double credit_limit;
    @NotEmpty(message = "Email is required")
    @Email
    private String email;
}
