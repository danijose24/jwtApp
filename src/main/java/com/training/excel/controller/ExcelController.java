package com.training.excel.controller;

import com.training.excel.controller.dto.ExcelDataDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class ExcelController {

    @PostMapping("/generateExcel")
    public ResponseEntity<ByteArrayResource> generateExcel(@RequestBody List<ExcelDataDTO> dynamicData) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DynamicSheet");

        // Create header row
        String[] headers = {"Name", "Age", "Country"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Create data rows from dynamic data
        for (int i = 0; i < dynamicData.size(); i++) {
            Row dataRow = sheet.createRow(i + 1); // Start from the second row (0-based index)
            ExcelDataDTO rowData = dynamicData.get(i);
            Cell nameCell = dataRow.createCell(0);
            nameCell.setCellValue(rowData.getName());
            Cell ageCell = dataRow.createCell(1);
            ageCell.setCellValue(rowData.getAge());
            Cell countryCell = dataRow.createCell(2);
            countryCell.setCellValue(rowData.getCountry());
        }

        // Write workbook to a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] excelContent = outputStream.toByteArray();
        ByteArrayResource resource = new ByteArrayResource(excelContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dynamic_excel.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelContent.length)
                .body(resource);
    }
}
