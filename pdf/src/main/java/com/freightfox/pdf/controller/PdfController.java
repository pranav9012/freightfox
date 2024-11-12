package com.freightfox.pdf.controller;

import java.io.File;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.freightfox.pdf.model.InvoiceData;
import com.freightfox.pdf.service.ItemValidator;
import com.freightfox.pdf.service.PdfService;



@Controller
public class PdfController {

    @Autowired
    private ItemValidator validatorService;
    @Autowired
    private PdfService pdfService;
    @CrossOrigin(origins = "*")
    @PostMapping(value = "/generate-doc", consumes = "application/json")
    public String jsonToPdf(@RequestBody(required = false) InvoiceData invoiceData, Model model) {
        try{
            if(invoiceData == null || invoiceData.getItems() == null || invoiceData.getItems().isEmpty()){
                model.addAttribute("errorMsg", "Missing Items. Please provide all the necessary items");
                return "error";
            }

            if (validatorService.checkDuplicates(invoiceData.getItems())) {
                model.addAttribute("errorMsg", "Duplicate Item exists");
                return "error";
            }
            
            String uniqueName = pdfService.getUniqueName(invoiceData);
            if(pdfService.isExists(uniqueName) == null){
                String htmlString = pdfService.generateHtmlString(invoiceData);
                String dirPath = pdfService.getPath();
                if(pdfService.generatePdf(htmlString, dirPath, uniqueName)){
                    pdfService.savePDF(uniqueName);
                    model.addAttribute("pdfName", uniqueName);
                    return "pdfView";
                } else {
                    model.addAttribute("errorMsg", "Error occurred while generating the PDF");
                    return "error";
                }
            } else {
                try{
                    model.addAttribute("pdfName",uniqueName);
                    return "pdfView";
                } catch (Exception e){
                    model.addAttribute("errorMsg", "Error occurred while generating the PDF");
                    return "error";
                }
            }

        } catch (Exception e){
            model.addAttribute("errorMsg", "An error occurred while generating the invoice");
            return "error";
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/pdf/{filename}")
    public ResponseEntity<Resource> getPdf(@PathVariable String filename) throws Exception {
        try{
            File file = pdfService.downloadPdf(filename);
            return ResponseEntity.ok()
                        .contentLength(file.length())
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(Files.newInputStream(file.toPath())));
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("download/{filename}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String filename) throws Exception {
        try{
            File file = pdfService.downloadPdf(filename);
            return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                        .contentLength(file.length())
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(Files.newInputStream(file.toPath())));
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
