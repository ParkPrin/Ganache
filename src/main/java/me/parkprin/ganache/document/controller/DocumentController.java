package me.parkprin.ganache.document.controller;

import me.parkprin.ganache.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public Map<String, String> create(){
        return documentService.createExcelFileAfterS3Upload(documentService.createDummyData());
    }
}
