package com.quasar.docservice.controller;

import com.quasar.docservice.exception.InvalidJsonException;
import com.quasar.docservice.exception.TemplateNotFoundException;
import com.quasar.docservice.service.DocumentGeneratorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/generate")
public class DocController {

    private final DocumentGeneratorService documentGeneratorService;
    private final ObjectMapper objectMapper;

    public DocController(DocumentGeneratorService documentGeneratorService, ObjectMapper objectMapper) {
        this.documentGeneratorService = documentGeneratorService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = { MediaType.APPLICATION_PDF_VALUE,
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<byte[]> generateDocument(
            @RequestPart("template") MultipartFile template,
            @RequestPart("data") String dataJson) {

        if (template == null || template.isEmpty()) {
            throw new TemplateNotFoundException("Template file (.docx) is missing or empty.");
        }

        Map<String, Object> dataObject;
        try {
            dataObject = objectMapper.readValue(dataJson, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("The provided data is not a valid JSON.", e);
        }

        byte[] pdfBytes = documentGeneratorService.generatePdf(template, dataObject);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
