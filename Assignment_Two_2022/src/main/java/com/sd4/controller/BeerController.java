/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.controller;

import com.sd4.model.Beer;
import com.sd4.service.BeerService;
import com.google.gson.Gson;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.sd4.application.PDFGeneratorBeer;
import com.sd4.model.Brewery;
import com.sd4.model.Category;
import com.sd4.model.Style;
import com.sd4.service.BreweryService;
import com.sd4.service.CategoryService;
import com.sd4.service.StyleService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Positive;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;


/**
 *
 * @author Cian
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@Validated
public class BeerController  {
    @Autowired
    private BeerService beerService;
    @Autowired
    private BreweryService breweryService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private CategoryService categoryService;
      @Autowired
    private StyleService styleService;
    private static final Gson gson = new Gson();


    @GetMapping(value= "beer", produces=MediaType.APPLICATION_JSON_VALUE)
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
   public ResponseEntity<Beer> getOne(@PathVariable @Positive long id){
      Optional<Beer> b = beerService.findOne(id);
      if(!b.isPresent()){
          return new ResponseEntity(HttpStatus.NOT_FOUND);
      } else{
          Link selfLink =linkTo(BeerController.class).slash("beer").withSelfRel();
                    Link allBeersLink = linkTo(methodOn(BeerController.class).getAll()).withSelfRel();
          b.get().add(selfLink);
          return ResponseEntity.ok(b.get());
      }
}
   
   
   @GetMapping("beer/pdf/{id}")
 public void generator(HttpServletResponse response, @PathVariable @Positive long id) throws DocumentException, IOException {
  response.setContentType("application/pdf");
        Optional<Beer> b = beerService.findOne(id);
            if(!b.isPresent()){
                return;
            } else{
        Beer beer = b.orElse(new Beer());

        Optional<Brewery> brew = breweryService.findOne(id);
        Brewery brewery = brew.orElse(new Brewery());
        Optional<Category> category = categoryService.findOne(beer.getCat_id());
        Category cat = category.orElse(new Category());
        Optional<Style> style = styleService.findOne(beer.getStyle_id());
        Style beerStyle = style.orElse(new Style());
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
        String currentDateTime = dateFormat.format(new Date());
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=pdf_"+currentDateTime+".pdf";
        response.setHeader(headerkey, headervalue);
        PDFGeneratorBeer beerPDF = new PDFGeneratorBeer();
        try{
        Image img = Image.getInstance(getClass().getClassLoader().getResource("static/assets/images/large/"+beer.getId()+".jpg"));
        beerPDF.setImage(img);
        } catch (Exception exc){
            
        }
        beerPDF.setBeer(beer);
        beerPDF.setCategory(cat);
        beerPDF.setBrewery(brewery);
        beerPDF.setStyle(beerStyle);
        beerPDF.generate(response);
        
 }
 }
     @GetMapping(value = {"beer/image/thumbnail/{id}","beer/image/large/{id}"}, produces = MediaType.IMAGE_JPEG_VALUE)
   public ResponseEntity<byte[]> getImage(@PathVariable @Positive long id) throws WriterException, IOException, FileNotFoundException, NotFoundException{
       
  UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
         String path = builder.buildAndExpand().getPath();
           Resource resource = null ;
           
          if(path.contains("thumbnail")){
          resource = resourceLoader.getResource("classpath:static/assets/images/thumbs/"+id+".jpg");
          }
          else if (path.contains("large")){
            resource = resourceLoader.getResource("classpath:static/assets/images/large/"+id+".jpg");
          }
        if (!resource.exists())
        {
            if(path.contains("thumbnail")){
            resource = resourceLoader.getResource("classpath:static/assets/images/thumbs/noimage.jpg");
          }
          else if (path.contains("large")){
          resource = resourceLoader.getResource("classpath:static/assets/images/large/noimage.jpg");
          }
        }  
        File file = resource.getFile();
        byte[] fileContent = Files.readAllBytes(file.toPath());
        InputStream in = new ByteArrayInputStream(fileContent);
       
    return ResponseEntity.ok(IOUtils.toByteArray(in));

 
}
   
      @GetMapping(value = "beer/drilldown/{id}", produces = MediaTypes.HAL_JSON_VALUE)
   public ResponseEntity<String> drillDown(@PathVariable @Positive long id){
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
         @GetMapping(value = "beer/allImages")
   public ResponseEntity<StreamingResponseBody> getAllImages(HttpServletResponse response ) throws IOException{
		int BUFFER_SIZE = 1024;
		StreamingResponseBody streamResponseBody = out -> {

			final ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
			ZipEntry zipEntry = null;
                        ZipEntry zipEntryThumb = null;
			InputStream inputStream = null;
                        InputStream inputStreamThumb = null;
			try {
                                  Resource resource = null ;
                                  Resource thumbnail = null ;
				for (Beer beer : beerService.findAll()) {
                                   thumbnail = resourceLoader.getResource("classpath:static/assets/images/thumbs/"+beer.getId()+".jpg");
                                   resource = resourceLoader.getResource("classpath:static/assets/images/large/"+beer.getId()+".jpg");
                                   if (resource.exists()){
                                       
                                   File file = resource.getFile();
                                   File thumb = thumbnail.getFile();
                                  byte[] fileContent = Files.readAllBytes(file.toPath());
                                   byte[] thumbContent = Files.readAllBytes(thumb.toPath());
                                    InputStream in = new ByteArrayInputStream(fileContent);
                                  InputStream inThumb = new ByteArrayInputStream(thumbContent);
                   
					zipEntry = new ZipEntry("large-"+file.getName());
					zipEntryThumb = new ZipEntry("thumb-"+thumb.getName());
					inputStream = new FileInputStream(file);
                                      	inputStreamThumb = new FileInputStream(thumb);
                                        
					zipOutputStream.putNextEntry(zipEntry);
					byte[] bytes = new byte[BUFFER_SIZE];
					int length;
					while ((length = inputStream.read(bytes)) >= 0) {
						zipOutputStream.write(bytes, 0, length);
                                        }
                                        zipOutputStream.putNextEntry(zipEntryThumb);
                                        while ((length = inputStreamThumb.read(bytes)) >= 0) {
						zipOutputStream.write(bytes, 0, length);
					}
                                      }
                                   }
				
				response.setContentLength((int) (zipEntry != null ? zipEntry.getSize() : 0));
			} catch (IOException e) {
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				if (zipOutputStream != null) {
					zipOutputStream.close();
				}
			}

		};

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=AllBeerImages.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");

		return ResponseEntity.ok(streamResponseBody);

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

//   public ResponseEntity add(@RequestBody Beer b){
//       beerService.saveBeer(b);
//       return new ResponseEntity(HttpStatus.CREATED);
//   }
//        

   
  

}
    
