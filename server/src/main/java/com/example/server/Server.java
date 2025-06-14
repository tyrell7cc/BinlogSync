package com.example.server;

import com.example.server.net.ReqDispatcher;

import java.io.*;
import java.net.*;

public class Server {
    private static String KEY;

    public static void main(String[] args) {
        System.out.println("starting...");
        if (args.length == 0){
            System.out.println("请输入 KEY");
            return;
        }
        KEY = args[0];
        if (KEY == null || KEY.isEmpty()){
            System.out.println("请输入 KEY");
            return;
        }
        new Server().start();
    }

    // 监听 TCP 端口 9909
    public void start() {
        int port = 9909;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器正在端口 " + port + " 上监听...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("新客户端连接: " + clientSocket.getInetAddress());

                // 处理客户端请求
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 处理客户端请求
    private void handleClient(Socket clientSocket) {
        try{
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            OutputStream out = clientSocket.getOutputStream();

            int keyLength = in.readInt();
            String key = in.readUTF();
            byte reqCode = in.readByte();

            System.out.println("读取到 KEY 长度: " + keyLength);
            System.out.println("读取到 KEY 内容: " + key);
            System.out.println("读取到 ReqCode: " + reqCode);

            if (!KEY.equals(key)){
                in.close();
                out.close();
                clientSocket.close();
                return;
            }
            //dispatcher
            ReqDispatcher.dispatch(reqCode, out,in);
            System.out.println("dispatch over");
            in.close();
            out.flush();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("连接关闭");
        }
    }
}

