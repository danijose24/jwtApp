package com.training.fileOp.controller;

import com.training.constants.ApplicationConstants;
import com.training.fileOp.entity.FileOP;
import com.training.fileOp.service.FileService;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class FileController {

    @Autowired
    FileService fileService;
    // Reads the upload directory from application.properties
    @Value(ApplicationConstants.UPLOAD_DIR)
    private String uploadDir;

    @GetMapping("/uploadForm")
    public String showUploadForm() {
        return "uploadForm";
    }

    /**
     * upload the file in Database.
     *
     * @param file The ID of the employee to retrieve.
     * @return The result page.
     */
    @PostMapping(ApplicationConstants.UPLOAD)
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {

        if (!file.isEmpty()) {
            FileOP fileOPObj = new FileOP();
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadDir + file.getOriginalFilename());
                Files.write(path, bytes);
                String successMessage = "File uploaded successfully: " + file.getOriginalFilename();
                model.addAttribute("message", successMessage);
                fileOPObj.setFileLocation(uploadDir);
                fileOPObj.setFileName(file.getOriginalFilename());
                fileService.saveFileOP(fileOPObj);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("message", "File upload failed: " + e.getMessage());
            }
        } else {
            model.addAttribute("message", "No file chosen for upload.");
        }
        return "uploadForm";
    }

    /**
     * download  the file .
     *
     * @param filename The ID of the employee to retrieve.
     * @return The downloaded file.
     */
    @GetMapping(ApplicationConstants.DOWNLOAD + "/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM) // Set the content type to application/octet-stream
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Method to View the file .
     *
     * @param filename The ID of the employee to retrieve.
     * @return The file.
     */
    @GetMapping(ApplicationConstants.VIEW + "/{filename:.+}")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            MediaType contentType = determineContentType(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType.toString())
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Compares two uploaded files and returns the comparison results.
     * param file1 The first uploaded file.
     * param file2 The second uploaded file.
     * param model The Spring model for passing data to the view.
     *
     * @return ResponseEntity containing the comparison results in a map.
     * @throws IOException if there's an issue reading the contents of the uploaded files.
     */
    @GetMapping("/compareForm")
    public String compareUploadForm() {
        return "compareForm";
    }

    @PostMapping(ApplicationConstants.COMPARE)
    public ResponseEntity<String> compareFiles(@RequestParam("file1") MultipartFile file1,
                                               @RequestParam("file2") MultipartFile file2,
                                               Model model) throws IOException {
        String content1 = new String(file1.getBytes());
        String content2 = new String(file2.getBytes());

        List<String> lines1 = readLines(content1);
        List<String> lines2 = readLines(content2);
       return diffrenciatTheFileChanges(content1,content2,lines1,lines2);

    }
private ResponseEntity<String>  diffrenciatTheFileChanges(String content1,String content2,List<String> lines1,List<String> lines2){
    Patch<String> patch = DiffUtils.diff(lines1, lines2);

    StringWriter writer = new StringWriter();
    List<String> unifiedDiffLines = DiffUtils.generateUnifiedDiff("File 1", "File 2", lines1, patch, 0);

    StringBuilder formattedDiff = new StringBuilder();
    for (Delta<String> delta : patch.getDeltas()) {
        for (String line : delta.getOriginal().getLines()) {
            if (delta.getType() == Delta.TYPE.INSERT) {
                formattedDiff.append("<span class=\"addition\">").append(line).append("</span>").append("\n");
            } else if (delta.getType() == Delta.TYPE.DELETE) {
                formattedDiff.append("<span class=\"subtraction\">").append(line).append("</span>").append("\n");
            } else {
                formattedDiff.append(line).append("\n");
            }
        }
    }

    Map<String, String> response = new HashMap<>();
    response.put("sourceContent", content1);
    response.put("destinationContent", content2);
    response.put("formattedDiff", formattedDiff.toString());

    String htmlResponse = generateHtmlResponse(response);

    return ResponseEntity.ok(htmlResponse);
}
    private String generateHtmlResponse(Map<String, String> response) {
        String template = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>File Comparison</title>\n" +
                "    <style>\n" +
                "        .addition {\n" +
                "            background-color: #eaffea; /* Light green for additions */\n" +
                "        }\n" +
                "        .subtraction {\n" +
                "            background-color: #ffd7d7; /* Light red for subtractions */\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>File Comparison Result</h1>\n" +
                "    <table class=\"comparison-table\">\n" +
                "        <tr>\n" +
                "            <th>Source File</th>\n" +
                "            <th>Destination File</th>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"source-file\" id=\"source-file\">\n" +
                "                <h2>Source File</h2>\n" +
                "                <pre><!-- Display content of source file -->\n" +
                response.get("sourceContent") +
                "                </pre>\n" +
                "            </td>\n" +
                "            <td class=\"destination-file\" id=\"destination-file\">\n" +
                "                <h2>Destination File</h2>\n" +
                "                <pre><!-- Display content of destination file -->\n" +
                response.get("destinationContent") +
                "                </pre>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "    <div class=\"diff-container\">\n" +
                "        <h2>File Differences</h2>\n" +
                "        <pre>\n" +
                response.get("formattedDiff") +
                "        </pre>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        return template;
    }


    private List<String> readLines(String content) throws IOException {
        List<String> lines;
        try (StringReader reader = new StringReader(content)) {
            lines = IOUtils.readLines(reader);
        }
        return lines;
    }

    /**
     * Method to get All the files
     *
     * @param model
     * @return List of Files
     */
    @GetMapping("/getAllFiles")
    public String listFiles(Model model) {
        List<FileOP> fileOPList = fileService.getAllFiles();
        model.addAttribute("fileList", fileOPList);
        return "fileListCompare"; // Return the name of the Thymeleaf template to list files
    }

    /**
     * Method to get the files from the Drive using file Name and Location
     *
     * @return result of compared files content.
     * @throws IOException
     */
    @PostMapping("/processSelectedFiles")
    public ResponseEntity< String> processSelectedFiles(@RequestParam(name = "selectedFiles", required = false) List<String> selectedFiles) throws IOException {
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            List<String> selectedFileOPs = new ArrayList<>();

            for (String fileName : selectedFiles) {

                String filePath = uploadDir + fileName;

                selectedFileOPs.add(filePath);
            }

            return fileToCompare(selectedFileOPs);
        }
        return null;
    }

    /**
     * Method to compare Two files
     *
     * @param selectedFileOPs
     * @return String which contains the Difference between the two files
     * @throws IOException
     */
    public ResponseEntity<String> fileToCompare(List<String> selectedFileOPs) throws IOException {

        List<File> filesToCompare = new ArrayList<>();

        for (String filePath : selectedFileOPs) {
            // String filePath = selectedFileOP.getFileLocation() + selectedFileOP.getFileName();
            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                filesToCompare.add(file);
            } else {
                System.out.println("File not found or is not a regular file: " + filePath);
            }
        }

        File file1 = filesToCompare.get(0);
        File file2 = filesToCompare.get(1);
        String content1 = new String(file1.toString().getBytes());
        String content2 = new String(file2.toString().getBytes());

        List<String> lines1 = readLines(content1);
        List<String> lines2 = readLines(content2);
        return diffrenciatTheFileChanges(content1,content2,lines1,lines2);

    }

    /**
     * Method to get the ContentType
     *
     * @param filename
     * @return
     */
    private MediaType determineContentType(String filename) {
        if (filename.endsWith(".txt")) {
            return MediaType.TEXT_PLAIN;
        } else if (filename.endsWith(ApplicationConstants.PDF)) {
            return MediaType.APPLICATION_PDF;
        } else if (filename.endsWith(ApplicationConstants.JPG) || filename.endsWith(ApplicationConstants.JPEG)) {
            return MediaType.IMAGE_JPEG;
        } else if (filename.endsWith(ApplicationConstants.PNG)) {
            return MediaType.IMAGE_PNG;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
