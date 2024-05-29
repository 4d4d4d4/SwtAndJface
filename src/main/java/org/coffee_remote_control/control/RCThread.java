package org.coffee_remote_control.control;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Classname RCThread
 * @Description 远程控制线程
 * @Date 2024/5/30 上午2:04
 * @Created by 憧憬
 */
public class RCThread extends Thread{
    private Integer port;
    private ObjectInputStream objIS;
    private DataOutputStream dataOS;

    public RCThread(Integer port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket accept = serverSocket.accept();
                objIS = new ObjectInputStream(accept.getInputStream());
                dataOS = new DataOutputStream(accept.getOutputStream());

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
