package com.example.client;

import com.example.common.FileList;

import java.io.IOException;


public class Client {
    private static  String KEY;
    public static void main(String[] args) throws IOException {
        if (args.length == 0){
            System.out.println("请输入 KEY");
            return;
        }
        KEY = args[0];
        if (KEY == null || KEY.isEmpty()){
            System.out.println("请输入 KEY");
            return;
        }

        FileList fileList = new FileUtil().getFileList(KEY);
        if (fileList != null){
            FileUtil.getFiles(fileList,  KEY);
        }
    }
}


