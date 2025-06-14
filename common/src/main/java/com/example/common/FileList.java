package com.example.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FileList implements Serializable {

    private int code = 1;//0错误，文件夹不存在  1成功
    private Map<String,FileMetaInf> files = new HashMap<>();


    public static FileList notExist(){
        FileList fileList = new FileList();
        fileList.code = 0;
        return fileList;
    }


    public void addFile(String fileName,FileMetaInf fileMetaInf){
        this.files.put(fileName,fileMetaInf);
    }


//    getter setter
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, FileMetaInf> getFiles() {
        return files;
    }

    public void setFiles(Map<String, FileMetaInf> files) {
        this.files = files;
    }
}
