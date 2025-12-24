package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        //abc.png
        String originalFilename = file.getOriginalFilename();
        logger.info("Filename : {}", originalFilename);

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BadApiRequestException("Invalid file: no extension found");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!extension.equalsIgnoreCase(".png") &&
                !extension.equalsIgnoreCase(".jpg") &&
                !extension.equalsIgnoreCase(".jpeg")) {
            throw new BadApiRequestException("File with this " + extension + " not allowed !!");
        }

        String filename = UUID.randomUUID().toString();
        String fileNameWithExtension = filename + extension;
        
        // Get the project root directory (where the application is running from)
        String projectRoot = System.getProperty("user.dir");
        Path folderPath = Paths.get(projectRoot, path).toAbsolutePath().normalize();
        Path fullPath = folderPath.resolve(fileNameWithExtension);

        logger.info("full image path: {} ", fullPath);
        logger.info("file extension is {} ", extension);

        // Ensure directory exists
        Files.createDirectories(folderPath);

        // Upload the file
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, fullPath);
        }

        return fileNameWithExtension;
    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        // Get the project root directory (where the application is running from)
        String projectRoot = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectRoot, path).toAbsolutePath().normalize();
        Path filePath = uploadPath.resolve(name.trim());
        
        logger.info("Loading resource: {}", filePath);

        File file = filePath.toFile();
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        return new FileInputStream(file);
    }

    @Override
    public boolean deleteFile(String fileName, String folderPath) {
        try {
            // Get the project root directory (where the application is running from)
            String projectRoot = System.getProperty("user.dir");
            Path uploadPath = Paths.get(projectRoot, folderPath).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(fileName.trim());

            logger.info("Attempting to delete file at: {}", filePath);

            File file = filePath.toFile();

            if (!file.exists()) {
                logger.warn("File does not exist: {}", filePath);
                return false;
            }

            // Check if file is readable and writable
            if (!file.canRead() || !file.canWrite()) {
                logger.warn("File permissions issue - canRead: {}, canWrite: {}", file.canRead(), file.canWrite());
                try {
                    file.setWritable(true);
                    file.setReadable(true);
                } catch (Exception e) {
                    logger.warn("Failed to set file permissions: {}", e.getMessage());
                }
            }

            // Strategy 1: Try direct deletion first
            boolean deleted = file.delete();
            if (deleted) {
                logger.info("File deletion successful on first attempt: {}", deleted);
                return true;
            }

            // Strategy 2: Force garbage collection and try again
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            deleted = file.delete();
            if (deleted) {
                logger.info("File deletion successful after GC: {}", deleted);
                return true;
            }

            // Strategy 3: Try using Files.delete() instead of File.delete()
            try {
                Files.delete(filePath);
                logger.info("File deletion successful using Files.delete()");
                return true;
            } catch (IOException e) {
                logger.warn("Files.delete() failed: {}", e.getMessage());
            }

            // Strategy 4: Try Files.deleteIfExists()
            try {
                boolean existsAndDeleted = Files.deleteIfExists(filePath);
                if (existsAndDeleted) {
                    logger.info("File deletion successful using Files.deleteIfExists()");
                    return true;
                }
            } catch (IOException e) {
                logger.warn("Files.deleteIfExists() failed: {}", e.getMessage());
            }

            // Strategy 5: Mark for deletion on exit as fallback
            file.deleteOnExit();
            logger.warn("File marked for deletion on exit: {}", filePath);

            return false;

        } catch (Exception e) {
            logger.error("Unexpected error while deleting file: {}", fileName, e);
            return false;
        }
    }
}
