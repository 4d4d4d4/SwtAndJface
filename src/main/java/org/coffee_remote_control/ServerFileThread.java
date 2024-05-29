package org.coffee_remote_control;

import org.coffee_remote_control.utils.FileSizeUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Classname ServerFileThread
 * @Description 服务器用于监听文件传输的线程
 * @Date 2024/5/29 下午9:06
 * @Created by 憧憬
 */
public class ServerFileThread extends Thread {
    private Integer fileHost = 5433;
    private Shell shell;

    public ServerFileThread(Shell shell) {
        this.shell = shell;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(fileHost);
            while (true) {
                Socket accept = serverSocket.accept();
                new Thread(new Task(accept)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class Task implements Runnable {

        private DataInputStream dataInputStream;
        private FileOutputStream fileOutputStream;
        private Socket socket;

        public Task(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            Display aDefault = Display.getDefault();
            aDefault.asyncExec(()->{

            try {
                String fileFolder = "D:\\receive_file\\";
                dataInputStream = new DataInputStream(socket.getInputStream());
                String fileName = dataInputStream.readUTF();
                long fileLength = dataInputStream.readLong();
                System.out.println("文件名：" + fileName + "文件大小" + fileLength);
                    boolean q = MessageDialog.openQuestion(shell, "提示", "是否接收来自客户端文件《" + fileName + "》(" + FileSizeUtil.formatFileSize(fileLength) + ")?");
                    if (!q) {
                        return;
                    }
                File folder = new File(fileFolder);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                File file = new File(fileFolder + fileName);
                fileOutputStream = new FileOutputStream(file);
                byte[] data = new byte[256 * 256];
                int index = -1;
                while ((index = dataInputStream.read(data)) != -1) {
                    fileOutputStream.write(data, 0, index);
                }
                fileOutputStream.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        }
    }
}
