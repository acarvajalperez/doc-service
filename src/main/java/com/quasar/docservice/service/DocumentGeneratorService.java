package com.quasar.docservice.service;

import com.quasar.docservice.exception.DocumentGenerationException;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class DocumentGeneratorService {

    public byte[] generatePdf(MultipartFile templateFile, Map<String, Object> data) {
        try (InputStream in = templateFile.getInputStream()) {
            // Load the DOCX template and set Velocity as the template engine
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

            // Create context and populate with data
            IContext context = report.createContext();
            if (data != null) {
                data.forEach(context::put);
            }

            // PDF Conversion Options
            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);

            // Generate report and convert to PDF
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                report.convert(context, options, out);
                return out.toByteArray();
            }

        } catch (IOException | XDocReportException e) {
            throw new DocumentGenerationException("Failed to generate PDF document from template.", e);
        }
    }
}
