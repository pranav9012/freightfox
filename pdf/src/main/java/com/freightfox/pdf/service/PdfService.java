package com.freightfox.pdf.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.freightfox.pdf.Repository.PdfRepo;
import com.freightfox.pdf.model.InvoiceData;
import com.freightfox.pdf.model.PdfData;
import com.itextpdf.html2pdf.HtmlConverter;

@Service
public class PdfService {

    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private PdfRepo repo;
    public boolean generatePdf(String htmlPage, String dirPath, String fileName) throws Exception{
        try{
            File directory = new File(dirPath);
            if(!directory.exists()){
            directory.mkdirs();
            }

            String fullPath = dirPath + File.separator + fileName;

            HtmlConverter.convertToPdf(htmlPage, new FileOutputStream(fullPath));
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public String getUniqueName(InvoiceData invoiceData){
        String uniqueName = String.valueOf(invoiceData.hashCode()) + ".pdf";
        return uniqueName;
    }

    public String getPath(){
        Path path = Paths.get(System.getProperty("user.dir"), "invoices");
        return path.toString();
    }

    public String generateHtmlString(InvoiceData invoiceData){
        Context context = new Context();
        context.setVariable("content", invoiceData);
        String htmlContent = templateEngine.process("invoice", context);
        return htmlContent;
    }

    public File downloadPdf(String filename) throws Exception{
        File file = new File(getPath() + File.separator + filename);
        if(!file.exists()){
            throw new FileNotFoundException("No file found");
        }
        return file;
    }

    public PdfData isExists(String filename) throws Exception{
        try{
            PdfData data = repo.getByPath(filename);
            return data;
        } catch (Exception e){
            throw new FileNotFoundException("No file found");
        }
    }

    public void savePDF(String uniqueName) {
        repo.save(new PdfData(uniqueName));
    }
}
