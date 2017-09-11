package ru.javago.main;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javago.ecxeption.RestartException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Restarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Restarter.class);

    private Restarter() {
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        String source = "";
        try {
            source = args[0];
        } catch (Exception e) {
            LOGGER.error("Path to Idea folder not found!", e);
            return;
        }
        copyFiles(source, getDestPath(source));
        String currentUserHomeDir = FileUtils.getUserDirectoryPath();
        File folder = new File(currentUserHomeDir);
        for (File folderEntry : folder.listFiles()) {
            if (folderEntry.getName().startsWith(".IntelliJIdea")) {
                cleanFolder(folderEntry + "\\config\\eval");
                deleteRowsIntoFile(folderEntry + "\\config\\options\\options.xml", "<property name=\"evl");
            }
        }
        cleanFolder(currentUserHomeDir + "\\AppData\\Roaming\\JetBrains");
        deletingDirectory(source);
        LOGGER.info("Success! Please enter any button to exit.");
        try {
            System.in.read();
        } catch (IOException e) {
            LOGGER.info("License for 30 days.", e);
        }
    }

    private static void cleanFolder(String fileDir) {
        File fileDirectory = new File(fileDir);
        try {
            FileUtils.cleanDirectory(fileDirectory);
        } catch (IOException e) {
            LOGGER.info("Exception when deleting files from: " + fileDir);
            throw new RestartException("Exception when deleting files from: " + fileDir, e);
        }
        LOGGER.info("Files were successfully deleting from: %s", fileDir);
    }

    private static void deleteRowsIntoFile(String filePath, String row) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().startsWith(row)) {
                    continue;
                }
                writer.write(currentLine + System.lineSeparator());
            }
        } catch (FileNotFoundException e) {
            LOGGER.info("File not found: " + filePath);
            throw new RestartException("File not found: " + filePath, e);
        } catch (IOException e) {
            LOGGER.info("Exception while working with file: " + filePath);
            throw new RestartException("Exception while working with file: " + filePath, e);
        }
        LOGGER.info("Successful deletion of string start with %s from file: %s", row, filePath);
    }

    private static void copyFiles(String source, String dest) {
        LOGGER.info("Waiting some times, files are copied..");
        File sourceFolder = new File(source);
        File destFolder = new File(dest);
        try {
            FileUtils.copyDirectory(sourceFolder, destFolder);
        } catch (IOException e) {
            LOGGER.info("Exception when copying directory: %s, to %s", source, dest);
            throw new RestartException("Exception when copying directory: " + source + ", to " + dest, e);
        }
        LOGGER.info("Files from %s to %d copied successfully", source, dest);
    }

    private static String getDestPath(String source) {
        String folderVersion = (source.substring(source.lastIndexOf('\\') + 1)).replaceAll("\\D+", "");
        if (StringUtils.isEmpty(folderVersion)) {
            return source + 1;
        }
        Integer newFolderVersion = Integer.parseInt(folderVersion) + 1;
        return source.replaceAll(folderVersion, String.valueOf(newFolderVersion));
    }

    private static void deletingDirectory(String directoryPath) {
        try {
            FileUtils.deleteDirectory(new File(directoryPath));
        } catch (IOException e) {
            LOGGER.info("Exception when deleting directory: %s", directoryPath);
            throw new RestartException("Exception when copying directory: " + directoryPath, e);
        }
    }
}
