package com.videogenerator.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class FileUtil {

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");
    private static final List<String> ALLOWED_VIDEO_EXTENSIONS = Arrays.asList("mp4", "avi", "mov", "mkv", "flv", "wmv");
    private static final List<String> ALLOWED_AUDIO_EXTENSIONS = Arrays.asList("mp3", "wav", "aac", "flac", "m4a");

    /**
     * Check if file extension is allowed for images
     */
    public static boolean isValidImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_IMAGE_EXTENSIONS.contains(extension);
    }

    /**
     * Check if file extension is allowed for videos
     */
    public static boolean isValidVideoFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_VIDEO_EXTENSIONS.contains(extension);
    }

    /**
     * Check if file extension is allowed for audio
     */
    public static boolean isValidAudioFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_AUDIO_EXTENSIONS.contains(extension);
    }

    /**
     * Get file extension from filename
     */
    public static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }

    /**
     * Get file name without extension
     */
    public static String getFileNameWithoutExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(0, lastDot);
        }
        return filename;
    }

    /**
     * Create directory if it doesn't exist
     */
    public static boolean createDirectoryIfNotExists(String dirPath) {
        try {
            Files.createDirectories(Paths.get(dirPath));
            log.info("Directory created: {}", dirPath);
            return true;
        } catch (Exception e) {
            log.error("Error creating directory: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Delete file
     */
    public static boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get file size in MB
     */
    public static double getFileSizeInMB(long sizeInBytes) {
        return sizeInBytes / (1024.0 * 1024.0);
    }

    /**
     * Get file size in GB
     */
    public static double getFileSizeInGB(long sizeInBytes) {
        return sizeInBytes / (1024.0 * 1024.0 * 1024.0);
    }

    /**
     * Format file size to human readable format
     */
    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int unitIndex = (int) (Math.log10(sizeInBytes) / Math.log10(1024));
        double size = sizeInBytes / Math.pow(1024, unitIndex);
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * Check if directory exists
     */
    public static boolean directoryExists(String dirPath) {
        return Files.isDirectory(Paths.get(dirPath));
    }

    /**
     * Check if file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}
