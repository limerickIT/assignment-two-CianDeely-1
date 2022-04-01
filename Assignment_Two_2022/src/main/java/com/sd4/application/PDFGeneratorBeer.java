/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.application;

import com.sd4.model.Beer;
import lombok.Setter;
import java.awt.Color;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sd4.model.Brewery;
import com.sd4.model.Category;
import com.sd4.model.Style;


/**
 *
 * @author Cian
 */
@Setter
public class PDFGeneratorBeer {
    private Beer beer;
    private Brewery brewery;
    private Category category;
    private Style style;
    private Image image;
 public void generate(HttpServletResponse response) throws DocumentException, IOException {
  Document document = new Document(PageSize.A4);
  PdfWriter.getInstance(document, response.getOutputStream());
  document.open();
  Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
  fontTiltle.setSize(20);
  Paragraph paragraph = new Paragraph(beer.getName(), fontTiltle);
  paragraph.setAlignment(Paragraph.ALIGN_CENTER);
  document.add(paragraph);
  PdfPTable table = new PdfPTable(2);
  table.setWidthPercentage(100f);
  table.setWidths(new int[] { 4, 4 });
  table.setSpacingBefore(10);
  PdfPCell cell = new PdfPCell();
  cell.setBackgroundColor(Color.darkGray);
  cell.setPadding(5);
  Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
  font.setColor(Color.WHITE);
      
  cell.setPhrase(new Phrase("Beer Image", font));
  table.addCell(cell);
  table.addCell(image);
  table.completeRow();

  cell.setPhrase(new Phrase("Beer Name", font));
  table.addCell(cell);
  table.addCell(beer.getName());
  table.completeRow();
  
  cell.setPhrase(new Phrase("ABV", font));
  table.addCell(cell);
  table.addCell(String.valueOf(beer.getAbv()));
  table.completeRow();

  cell.setPhrase(new Phrase("Description", font));
  table.addCell(cell);
  table.addCell(beer.getDescription());
  table.completeRow();

  cell.setPhrase(new Phrase("Sell Price", font));
  table.addCell(cell);
  table.addCell(String.valueOf(beer.getSell_price()));
  table.completeRow();
  
  cell.setPhrase(new Phrase("Brewery Name", font));
  table.addCell(cell);
  table.addCell(brewery.getName());
  table.completeRow();

  cell.setPhrase(new Phrase("Website", font));
  table.addCell(cell);
  table.addCell(brewery.getWebsite());
  table.completeRow();

  cell.setPhrase(new Phrase("Beer Category", font));
  table.addCell(cell);
  table.addCell(category.getCat_name());
  table.completeRow();

  cell.setPhrase(new Phrase("Beer Style", font));
  table.addCell(cell);
  table.addCell(style.getStyle_name());
  
  document.add(table);
  document.close();
 }
}
