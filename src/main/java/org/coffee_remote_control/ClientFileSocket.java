package org.coffee_remote_control;

import java.io.*;
import java.net.Socket;

/**
 * @Classname ClientFileSocket
 * @Description 客户端用于传输文件的套接字
 * @Date 2024/5/29 下午9:06
 * @Created by 憧憬
 */
public class ClientFileSocket extends Socket {
    private FileInputStream fileInputStream;
    private DataOutputStream dataOutputStream;

    public ClientFileSocket(String host, int port) throws IOException {
        super(host, port);
    }

    public void sendFile(File file){
        if(!file.exists()){
            return;
        }
        System.out.println("文件名：" + file.getName() + "文件大小：" + file.length());
        try {
            this.dataOutputStream = new DataOutputStream(this.getOutputStream());
            this.fileInputStream = new FileInputStream(file);
            byte[] data = new byte[256*256];
            int index = -1;
            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.flush();
            dataOutputStream.writeLong(file.length());
            dataOutputStream.flush();
            while((index = fileInputStream.read(data)) != -1){
                dataOutputStream.write(data, 0, index);
            }
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            try {
                if(this.fileInputStream != null){
                    this.fileInputStream.close();
                }
                if(this.dataOutputStream != null) {
                    this.dataOutputStream.close();
                }
                this.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
