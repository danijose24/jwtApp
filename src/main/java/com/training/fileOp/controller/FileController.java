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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public String compareFiles(@RequestParam("sourceFile") MultipartFile sourceFile,
                               @RequestParam("destinationFile") MultipartFile destinationFile, Model model) throws IOException {
        String sourceFileContent = new String(sourceFile.getBytes());
        String destinationFileContent = new String(destinationFile.getBytes());

        List<String> sourceFileLines = readLines(sourceFileContent);
        List<String> destinationFileLines = readLines(destinationFileContent);

        Patch<String> patch = DiffUtils.diff(sourceFileLines, destinationFileLines);

        List<String> unifiedDiff = DiffUtils.generateUnifiedDiff("Source File", "Destination File", sourceFileLines, patch, 0);

        model.addAttribute("diffLines", unifiedDiff);
        return "diffResult";
    }
    private ResponseEntity<String> diffrenciatTheFileChanges(String sourceFile, String destinationFile, List<String> linesOfSource, List<String> linesOfDestination) {
        Patch<String> patch = DiffUtils.diff(linesOfSource, linesOfDestination);

        List<String> unifiedDiffLines = DiffUtils.generateUnifiedDiff("sourceFile", "destinationFile", linesOfSource, patch, 0);

        StringBuilder formattedDiff = new StringBuilder();
        patch.getDeltas().forEach(delta -> {
            delta.getOriginal().getLines().forEach(line -> {
                String formattedLine = delta.getType() == Delta.TYPE.INSERT ? "<span class=\"addition\">" + line + "</span>" :
                        delta.getType() == Delta.TYPE.DELETE ? "<span class=\"subtraction\">" + line + "</span>" :
                                line;
                formattedDiff.append(formattedLine).append("\n");
            });
        });

        Map<String, String> response = new HashMap<>();
        response.put("sourceContent", sourceFile);
        response.put("destinationContent", destinationFile);
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
    public ResponseEntity<String> processSelectedFiles(@RequestParam(name = "selectedFiles", required = false) List<String> selectedFiles) throws IOException {
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            List<String> selectedFileOPs = selectedFiles.stream()
                    .map(fileName -> uploadDir + fileName)
                    .collect(Collectors.toList());

            return fileToCompare(selectedFileOPs);
        }
        return ResponseEntity.ok().build();
    }
    /**
     * Method to compare Two files
     *
     * @param selectedFileOPs
     * @return String which contains the Difference between the two files
     * @throws IOException
     */
    public ResponseEntity<String> fileToCompare(List<String> selectedFileOPs) throws IOException {
        List<File> filesToCompare = selectedFileOPs.stream()
                .map(File::new)
                .filter(file -> file.exists() && file.isFile())
                .collect(Collectors.toList());

        if (filesToCompare.size() < 2) {
            return ResponseEntity.badRequest().body("Not enough files to compare");
        }

        File sourceFile = filesToCompare.get(0);
        File destinationFile = filesToCompare.get(1);

        List<String> linesOfSource = readLines(sourceFile);
        List<String> linesOfDestination = readLines(destinationFile);

        return diffrenciatTheFileChanges(sourceFile.toString(), destinationFile.toString(), linesOfSource, linesOfDestination);
    }

    private List<String> readLines(File file) throws IOException {
        try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            return lines.collect(Collectors.toList());
        }
    }

    /**
     * Method to get the ContentType
     *
     * @param filename
     * @return
     */
    private MediaType determineContentType(String filename) {
        Map<String, MediaType> mediaTypeMap = new HashMap<>();
        mediaTypeMap.put(".", MediaType.TEXT_PLAIN);
        mediaTypeMap.put(ApplicationConstants.PDF, MediaType.APPLICATION_PDF);
        mediaTypeMap.put(ApplicationConstants.JPG, MediaType.IMAGE_JPEG);
        mediaTypeMap.put(ApplicationConstants.JPEG, MediaType.IMAGE_JPEG);
        mediaTypeMap.put(ApplicationConstants.PNG, MediaType.IMAGE_PNG);

        return mediaTypeMap.getOrDefault(getFileExtension(filename), MediaType.APPLICATION_OCTET_STREAM);
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex >= 0 ? filename.substring(lastDotIndex) : "";
    }
}
