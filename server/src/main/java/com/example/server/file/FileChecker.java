package com.example.server.file;

import com.example.common.FileList;
import com.example.common.FileMetaInf;
import com.example.common.Util;

import java.io.*;

public class FileChecker {

    private static final String FILE_PATH = "/home/ty/tmp/";
    //获取所有文件列表 及其 hash值
    public static FileList fileList() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()){
            return FileList.notExist();
        }

        FileList fileList = new FileList();

        for (File f : file.listFiles()) {
            FileMetaInf fileMetaInf = new FileMetaInf();
            fileMetaInf.setHash(Util.getHash(f));
            fileMetaInf.setSize(f.length());
            fileList.addFile(f.getName(), fileMetaInf);
        }
        return fileList;
    }

    public static void trans(String fileName,long startPosition, OutputStream outputStream) throws IOException {
        System.out.println("开始传输文件："+fileName);
        File file = new File(FILE_PATH,fileName);
        FileInputStream fileInputStream = new FileInputStream(file);

        //从指定位置开始传输文件
        fileInputStream.skip(startPosition);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer,  0, bytesRead);
        }
    }
}
