package org.zerhusen;

import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

public class FileTest {
    public static void main(String[] args) {
        try {
            String path = "E:\\Download";
            File rootFile = new File(path);
            handleFile(rootFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleFile(File rootFile) throws IOException {
        File[] files = rootFile.listFiles();
        if (files.length == 0) {
            rootFile.delete();
        } else {
            for (File file : rootFile.listFiles()) {
                if (file.isDirectory()) {
                    handleFile(file);
                } else {
                    long length = file.length();
                    if (length < 50 * 1024 * 1024L || length > 5242880000L) {
                        System.out.println(file.getAbsolutePath() + "；文件大小：" + length);
                        file.delete();
                    } else {
                        String name = file.getName();
                        System.out.println("合适文件：" + name);

                        File toFile = new File("E:\\Download\\All\\" + name);
                        FileCopyUtils.copy(file, toFile);
                        file.delete();
                    }
                }
            }
        }

    }
}
