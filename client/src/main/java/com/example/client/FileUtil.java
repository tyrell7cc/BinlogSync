package com.example.client;

import com.example.common.FileList;
import com.example.common.FileMetaInf;
import com.example.common.ReqCode;
import com.example.common.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.Files;

public class FileUtil {
    private static final String FOLDER = "/home/ty/tmpA";
    public FileList getFileList(String KEY){

        Net net = new Net();
        try {
            net.connect();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("连接服务器失败");
            return null;
        }

        // 获取输出流，发送数据到服务器
        DataOutputStream dataOutputStream = new DataOutputStream(net.outputStream);
        try {
            dataOutputStream.writeInt(KEY.length());
            dataOutputStream.writeUTF(KEY);
            dataOutputStream.writeByte(ReqCode.FILE_LIST);
            dataOutputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("写入请求失败");
            return null;
        }
        System.out.println("开始获取文件列表...");

        // 读取服务器的响应
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(net.inputStream));
            String jsonResult = br.readLine();
            FileList fileList = new ObjectMapper().readValue(jsonResult,FileList.class);
            try {
                System.out.println(new ObjectMapper().writer().writeValueAsString(fileList));
                System.out.println("\n");
            }catch (Exception e){
                e.printStackTrace();
            }
            return fileList;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("服务器返回错误");
            return null;
        }finally {
            net.close();
        }

    }


    //获取文件增量 默认从0开始   写到临时文件夹 成功了再移动
    //报错 则清理临时文件夹
    public static void getFiles(FileList fileList,  String KEY){
        try {
            //清理临时文件夹 删除它和里面的文件
            File folderFile = new File(FOLDER);
            File tempFolder = new File(FOLDER,"temp");
            if (tempFolder.exists()){
                for (File file : tempFolder.listFiles()) {
                    file.delete();
                }
            }else {
                tempFolder.mkdirs();
            }
            System.out.println("清理临时文件夹ok");
            System.out.println("开始转移已有文件到temp");
            for (File file : folderFile.listFiles()) {
                if (file.isFile()){
                    File tempFile = new File(tempFolder,file.getName());
                    Files.copy(file.toPath(), tempFile.toPath());
                }
            }

            System.out.println("开始获取文件\n\n");
            getFilesInternal(fileList,  KEY);
            System.out.println("文件获取成功");

            //文件获取成功，删除原来的文件，转移临时文件夹中的文件
            for (File file : folderFile.listFiles()){
                if (file.isFile()){
                    file.delete();
                }
            }
            for (File tmpFile : tempFolder.listFiles()) {
                File file = new File(FOLDER,tmpFile.getName());
                Files.move(tmpFile.toPath(),file.toPath());
            }
        }catch (Exception e){
            e.printStackTrace();

            //清理临时文件夹 删除它和里面的文件
            File tempFolder = new File(FOLDER,"temp");
            if (tempFolder.exists()){
                for (File file : tempFolder.listFiles()) {
                    file.delete();
                }
                tempFolder.delete();
            }
        }
    }
    private static void getFilesInternal(FileList fileList,  String KEY) throws IOException {
        if (fileList.getCode() != 1){
            System.out.println("服务器返回code 为 0");
            return;
        }
        File folderFile = new File(FOLDER);
        if (!folderFile.exists()){
            System.out.println(FOLDER + " 文件夹不存在,请先创建");
            return;
        }
        //创建临时文件夹
        File tempFolder = new File(folderFile,"temp");
        if (!tempFolder.exists()){
            tempFolder.mkdirs();
        }
        for (String fileName : fileList.getFiles().keySet()) {
            FileMetaInf fileMetaInf = fileList.getFiles().get(fileName);

            //文件不存在 直接创建 从服务器拉新
            File file = new File(folderFile,fileName);
            if (!file.exists()){
                System.out.println("文件不存在,开始创建:"+fileName);
                file = new File(tempFolder,fileName);
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);

                try {
                    Net net = new Net();
                    net.connect();
                    InputStream inputStream = net.inputStream;

                    DataOutputStream dataOutputStream = new DataOutputStream(net.outputStream);
                    try {
                        dataOutputStream.writeInt(KEY.length());
                        dataOutputStream.writeUTF(KEY);
                        dataOutputStream.writeByte(ReqCode.GET_FILE);
                        dataOutputStream.writeUTF(fileName);
                        dataOutputStream.writeLong(0);
                        dataOutputStream.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("写入请求失败");
                        throw e;
                    }

                    System.out.println("开始获取文件:"+fileName);
                    //从 inputStream 读取数据 写入 file

                    int read = inputStream.read();
                    while (read != -1){
                        fos.write(read);
                        read = inputStream.read();
                    }
                    System.out.println("over");
                    fos.close();
                    net.close();
                }catch (Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }else {
                System.out.println("文件已存在,走增量模式:"+fileName);
                file = new File(tempFolder,fileName);
                //判断文件是否一致 大小 && hash
                if (file.length() == fileMetaInf.getSize() && fileMetaInf.getHash().equals(Util.getHash(file))){
                    System.out.println("文件无变化："+fileName);
                    continue;
                }
                FileOutputStream fos = new FileOutputStream(file,true);

                try {
                    Net net = new Net();
                    net.connect();
                    InputStream inputStream = net.inputStream;

                    DataOutputStream dataOutputStream = new DataOutputStream(net.outputStream);
                    try {
                        dataOutputStream.writeInt(KEY.length());
                        dataOutputStream.writeUTF(KEY);
                        dataOutputStream.writeByte(ReqCode.GET_FILE);
                        dataOutputStream.writeUTF(fileName);
                        dataOutputStream.writeLong(file.length());
                        dataOutputStream.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("写入请求失败");
                        throw e;
                    }

                    System.out.println("开始获取文件add:"+fileName);
                    //从 inputStream 读取数据 写入 file

                    int read = inputStream.read();
                    while (read != -1){
                        fos.write(read);
                        read = inputStream.read();
                    }
                    System.out.println("overadd \n");
                    fos.close();
                    net.close();
                }catch (Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        // ? 移动文件 删除临时文件夹
    }
}
