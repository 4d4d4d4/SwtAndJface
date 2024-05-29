package org.coffee_remote_control;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
/**
 * @Classname ServerUI
 * @Description 服务器GUI界面
 * @Date 2024/5/28 下午4:24
 * @Created by 憧憬
 */

public class ServerUI extends Shell {
    private static final Display display = Display.getDefault();
    private final Composite showTextComposite = new Composite(this, SWT.NONE); // 显示消息面板
    private final Composite editTextComposite = new Composite(this, SWT.NONE); // 发送消息面板
    private ServerSocket serverSocket; // 服务端开放套接字
    private Integer msgPort = 8848; // 开放chat端口 8848钛合金
    private ObjectInputStream objISMsg; // 接收消息流
    private ObjectOutputStream objOSMsg; // 发送消息流
    private Text showText;  // 展示文本框
    private Text editTexts;  // 输入文本框

    public ServerUI() {
        super(display);
        this.setSize(650, 480);
        this.setText("服务端");
        this.setToolTipText("服务");
        FontDialog fontdialog = new FontDialog(this, SWT.NONE);
        fontdialog.setFontList(new FontData[]{new FontData("宋体", 14, SWT.BOLD | SWT.ITALIC)});
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);

        // 初始化显示消息面板
        initShowTextComposite();
        // 初始化消息发送面板
        initEditTextComposite();

        // 开启一个新的线程执行服务端Socket连接，以保持界面的非阻塞
        Thread serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(msgPort);
                // 接收客户端连接并启动接收消息线程
                while (true) {
                    System.out.println("等待客户端连接");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("客户端连接成功");
                    new RecThread(clientSocket).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("服务器socket连接失败");
            }finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        ServerFileThread serverFileThread = new ServerFileThread(this);
        serverFileThread.setDaemon(true);
        serverFileThread.start();
        this.open();
        this.layout();
        while (!this.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    // 初始化消息发送面板
    private void initEditTextComposite() {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL; // 水平填充
        gridData.grabExcessHorizontalSpace = true; // 水平动态拉伸
        gridData.verticalAlignment = SWT.DEFAULT; // 纵向
        gridData.grabExcessVerticalSpace = true; // 纵向拉伸
        gridData.heightHint = 180;
        editTextComposite.setLayoutData(gridData);
        editTextComposite.setLayout(new GridLayout());
        editTextComposite.setToolTipText("消息展示面板");

        Font heitFont = new Font(display, "黑体", 11, SWT.TITLE);
        Label textTitle = new Label(editTextComposite, SWT.TITLE);
        textTitle.setText("编辑框：");
        textTitle.setFont(heitFont);

        editTexts = new Text(editTextComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP); // 多行 有线条 换行
        editTexts.setTextLimit(150);
        editTexts.setBackground(display.getSystemColor(SWT.COLOR_WHITE)); // 设置颜色白色
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = SWT.FILL;
        gridData1.verticalAlignment = SWT.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        editTexts.setLayoutData(gridData1);

        Button button = new Button(editTextComposite, SWT.NONE);
        button.setText("发送");
        GridData gridData2 = new GridData();
        gridData2.widthHint = 80;
        gridData2.heightHint = 30;
        gridData2.horizontalAlignment = SWT.CENTER;
        gridData2.verticalIndent = 7;
        button.setLayoutData(gridData2);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                sendMsg(editTexts.getText());
            }
        });
    }

    private void sendMsg(String text) {
        if(objOSMsg == null){
            MessageDialog.openInformation(this, "信息提示", "尚未有客户端连接，请稍等");
            return;
        }
        String sendText = "fwd##" + text;
        byte[] bytes = sendText.getBytes(StandardCharsets.UTF_8);
        try {
            objOSMsg.writeObject(bytes);
            objOSMsg.flush();
            Display.getDefault().asyncExec(() -> {
                showText.append("我说：" + text + "\n");
                showText.setOrientation(showText.getText().length());
                editTexts.setText("");
                editTexts.getParent().layout();  // 强制刷新布局
            });
        } catch (IOException e) {
            System.out.println("服务端消息发送失败" + e.getMessage());
            throw new RuntimeException(e);
        }
    }



    // 初始化显示消息面板
    private void initShowTextComposite() {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL; // 水平填充
        gridData.grabExcessHorizontalSpace = true; // 水平动态拉伸
        gridData.verticalAlignment = SWT.DEFAULT; // 纵向
        gridData.grabExcessVerticalSpace = true; // 纵向拉伸
        gridData.heightHint = 230; // 高度
        showTextComposite.setLayoutData(gridData);
        showTextComposite.setLayout(new GridLayout());
        showTextComposite.setToolTipText("消息展示面板");

        Font heitFont = new Font(display, "黑体", 11, SWT.TITLE);
        Label textTitle = new Label(showTextComposite, SWT.TITLE);
        textTitle.setText("消息框：");
        textTitle.setFont(heitFont);


        showText = new Text(showTextComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        showText.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        showText.setEnabled(false);
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = SWT.FILL;
        gridData1.verticalAlignment = SWT.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        showText.setLayoutData(gridData1);
    }


    @Override
    protected void checkSubclass() {

    }

    public static void main(String[] args) {
        ServerUI serverUI = new ServerUI();

    }

    //
    private class RecThread extends Thread {
        private String currentIp ;
        @Override
        public void run() {
            Display display = Display.getDefault(); // 获取当前Display实例
            System.out.println("等待客户端输入");
            while(true){
                try {
                    byte[] bytes = (byte[]) objISMsg.readObject();
                    String msg = new String(bytes);
                    String[] split = msg.split("##");
                    System.out.println(msg);
                        display.asyncExec(() ->{
                            if(split.length > 1 && split[0].equals("khd")) {
                                showText.append("客户端" + currentIp + "说：" + split[1] + "\n");
                            }
                            showText.setOrientation(showText.getText().length());
                        });

                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public RecThread(Socket accept) {
            try {
                System.out.println(accept.getInputStream());
                objOSMsg = new ObjectOutputStream(accept.getOutputStream());
                objISMsg = new ObjectInputStream(accept.getInputStream());
                System.out.println("初始化进程");
                String clientIp = accept.getInetAddress().getHostAddress();
                currentIp = clientIp;
                System.out.println("客户端连接IP为：" + clientIp);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}


