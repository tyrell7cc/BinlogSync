package com.example.client;

import java.io.*;
import java.net.Socket;

public class Net {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9909;

    public InputStream inputStream;
    public OutputStream outputStream;

    public void connect() throws IOException {
        Socket socket = new Socket(HOST, PORT);
        System.out.println("连接到服务器...");
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        System.out.println("连接到服务器成功");
    }
    public void close(){
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
