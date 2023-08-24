package com.training.excel.controller;

import com.training.constants.ApplicationConstants;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping(ApplicationConstants.EXCEL)
public class ExcelController {

    /**
     * Generates an Excel file with dynamic data and returns it as a downloadable resource.
     *
     * @return ResponseEntity containing the generated Excel file.
     * @throws IOException if there is an issue creating or writing to the Excel file.
     */
    @GetMapping(ApplicationConstants.GENERATE_EXCEL)
    public ResponseEntity<ByteArrayResource> generateExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DynamicSheet");

        // Create some sample data
        String[] headers = {"Name", "Age", "Country"};
        Object[][] data = {
                {"Nagaraj", 28, "USA"},
                {"vigi", 25, "Canada"},
                {"Daniel", 28, "UK"}
        };

        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Create data rows
        for (int i = 0; i < data.length; i++) {
            Row dataRow = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = dataRow.createCell(j);
                if (data[i][j] instanceof String) {
                    cell.setCellValue((String) data[i][j]);
                } else if (data[i][j] instanceof Integer) {
                    cell.setCellValue((Integer) data[i][j]);
                }
            }
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
