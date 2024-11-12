package com.freightfox.pdf.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freightfox.pdf.model.PdfData;

public interface PdfRepo extends JpaRepository<PdfData, Long>{

    public PdfData getByPath(String filename);

}
