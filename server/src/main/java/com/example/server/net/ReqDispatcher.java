package com.example.server.net;

import com.example.common.FileList;
import com.example.common.ReqCode;
import com.example.server.file.FileChecker;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class ReqDispatcher {


    public static void dispatch(byte reqCode, OutputStream outputStream,DataInputStream dataInputStream) {

        if (reqCode == ReqCode.FILE_LIST){
            //先将binlog文件复制到文件夹  执行 bac.sh `cp /home/ubuntu/flodust.db.lib/binlog.* /home/ubuntu/fddb/`
            try {
                FileList fileList = FileChecker.fileList();
                String jsonResult = new ObjectMapper().writeValueAsString(fileList);
                outputStream.write(jsonResult.getBytes());
                outputStream.flush();
                System.out.println("返回FileList");
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("返回FileList成功");
        }else if (reqCode == ReqCode.GET_FILE){
            try {
                String fileName = dataInputStream.readUTF();
                long startPosition = dataInputStream.readLong();
                System.out.println("你想要："+fileName);
                FileChecker.trans(fileName,startPosition,outputStream);
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                outputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("返回文件成功");
        }
    }
}
