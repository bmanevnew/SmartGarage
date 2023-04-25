package com.company.web.smart_garage.services;

import com.company.web.smart_garage.models.Visit;
import com.lowagie.text.DocumentException;

import java.io.ByteArrayOutputStream;

public interface VisitPdfExporterService {
    ByteArrayOutputStream export(Visit visit, String currencyCode) throws DocumentException;
}
