package ru.javago.main;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Класс для тестирования Restarter
 */
public class RestarterTest {

    @Test
    public void cleanFolderTest() {
        String directoryToTest = createDirectoryToTest();
        try {
            Whitebox.invokeMethod(Restarter.class, "cleanFolder", directoryToTest);
            File folder = new File(directoryToTest);
            Assert.assertTrue(folder.list().length == 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteTestDirectory(directoryToTest);
        }
    }

    @Test
    public void deleteRowsIntoFileTest() {
        String directoryToTest = createDirectoryToTest();
        String filePath = directoryToTest + "\\test.txt";
        String testPrefix = "something";
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Whitebox.invokeMethod(Restarter.class, "deleteRowsIntoFile", filePath, testPrefix);
            boolean presence = false;
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().startsWith(testPrefix)) {
                    presence = true;
                    break;
                }
            }
            Assert.assertFalse(presence);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteTestDirectory(directoryToTest);
        }
    }

    @Test
    public void copyFilesTest() {
        String directoryToTest = createDirectoryToTest();
        String directoryToTestNew = directoryToTest + "New";
        try {
            Whitebox.invokeMethod(Restarter.class, "copyFiles", directoryToTest, directoryToTestNew);
            File origin = new File(directoryToTest + "\\test.txt");
            File copy = new File(directoryToTestNew + "\\test.txt");
            System.out.println();
            Assert.assertTrue(FileUtils.contentEquals(origin, copy));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteTestDirectory(directoryToTest);
            deleteTestDirectory(directoryToTestNew);
        }
    }

    @Test
    public void getDestPathTest(){
        try {
            Object destPath = Whitebox.invokeMethod(Restarter.class, "getDestPath", "C:\\something\\something\\Idea2");
            Assert.assertEquals((String) destPath, "C:\\something\\something\\Idea3");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createDirectoryToTest() {
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        Path filePath = Paths.get(path + "\\testFolder\\test.txt");
        List lines = Arrays.asList("test", "something_test", "test");
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path + "\\testFolder";
    }

    private void deleteTestDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
