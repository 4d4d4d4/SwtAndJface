import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import control.RCServer;

class ServerUI extends JFrame {
    private JPanel msgShowPanel; // 展示消息面板
    private JTextArea msgShowArea; // 展示接收消息框
    private JPanel msgEditPanel;  // 消息发送面板
    private JTextArea msgEditArea; // 消息修改面板


    private ServerSocket serverMsg; // 服务端消息socket
    private static final int msgPort = 11268; // 消息开放端口号
    private static final int filePort = 8899; // 文件开放端口号
    private ObjectInputStream ObjISMsg;  // 接收消息流
    private ObjectOutputStream ObjOSMsg; // 发送消息流

    private String ip;

    private Font font = new Font("Dialog", Font.BOLD, 18);

    //ServerUI构造函数
    public ServerUI() {
        super("服务器端");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initMsgShowPanel(); //调用初始化消息显示面板函数
        initMsgEditPanel(); //调用初始化消息编辑面板函数

        // 将在init中初始化的两个panel添加到container中
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        pane.add(msgShowPanel, BorderLayout.CENTER);
        pane.add(msgEditPanel, BorderLayout.SOUTH);

//        JPanel p = new JPanel();
//        pane.add(p, BorderLayout.NORTH);

        pack();   //自动调整大小布局
        setVisible(true);

        try {
            serverMsg = new ServerSocket(msgPort); //通信端口 服务器消息开放端口11268
            // 等待客户端连接
            new recThread(serverMsg.accept()).start(); //启动接收监听线程

        } catch (Exception e) {
            System.out.println("server.accept:" + e);
        }

        //远程控制服务端
        RCServer rcs = new RCServer();
        try {
            rcs.startServer(18080);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class recThread extends Thread {//接收信息线程   


        private byte[] data;

        private String str;

        // 创建与客户端的输入输出连接
        public recThread(Socket c) throws IOException {//线程构造函数   
            ObjOSMsg = new ObjectOutputStream(c.getOutputStream()); // socket的输出流
            ObjISMsg = new ObjectInputStream(c.getInputStream()); // 创建输入流
            ip = c.getInetAddress().getHostAddress();
            System.out.println("ip=" + ip);

            // 服务器接收文件线程
            ServerFileThread server;
            try {
                server = new ServerFileThread(getContentPane());
                // 启动服务端文件处理线程
                server.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        public void run() {//线程监听函数   
            try {
                while (true) {

                    data = (byte[]) ObjISMsg.readObject();
                    str = new String(data);//字节数据转字符串
                    String msg[] = str.split("####");

                    if (str.startsWith("shuo")) {
                        msgShowArea.append("控制端说：" + msg[1] + "\n");
                    }

                    msgShowArea.setCaretPosition(msgShowArea.getText().length());
                }
            } catch (Exception e) {
                System.out.println("消息接收错误：" + e.getStackTrace());
            }
            ;
        }
    }

    /**
     * 初始化消息显示面板
     * 完成了消息展示面板的初始化、消息显示区域的初始化，并未消息显示区域添加滚动条，将消息显示区域添加给了消息显示面板
     */
    private void initMsgShowPanel() {
        msgShowPanel = new JPanel();
        msgShowPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("消息框: ");
        label.setFont(font);

        msgShowArea = new JTextArea(10, 50);
        msgShowArea.setEditable(false);
        msgShowArea.setLineWrap(true);

        JScrollPane msgShowPane = new JScrollPane();
        msgShowPane.setViewportView(msgShowArea);

        msgShowPanel.add(label, BorderLayout.NORTH);
        msgShowPanel.add(msgShowPane, BorderLayout.CENTER);
    }

    /**
     * 初始化消息编辑面板
     * 初始化了消息发送面板、消息发送区域并该区域添加了滚动轴、初始化了发送按钮面板添加了一个发送按钮、该按钮监听了一个消息发送方法
     */
    private void initMsgEditPanel() {
        // 初始化消息修改/发送面板
        msgEditPanel = new JPanel();
        msgEditPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("编辑框: ");
        label.setFont(font);

        // 初始化消息发送区域 并添加滚动轴
        msgEditArea = new JTextArea(5, 50);
        msgEditArea.setLineWrap(true);
        JScrollPane msgEditPane = new JScrollPane();
        msgEditPane.setViewportView(msgEditArea);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());


        JButton sendButton = new JButton("发送");
        sendButton.setFont(font);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {//发送消息   
                sendMsg("shuo####" + msgEditArea.getText());
            }
        });
        buttonPanel.add(sendButton);

        msgEditPanel.add(label, BorderLayout.NORTH);
        msgEditPanel.add(msgEditPane, BorderLayout.CENTER);
        msgEditPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 发送消息3
     * 将输入框的内容写入到ObjOSMsg流中、将自己说的消息写入到消息显示框 清空发送框
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        try {
            byte[] data = msg.getBytes();

            ObjOSMsg.writeObject(data); // 需要先等recThread线程初始化完成
            ObjOSMsg.flush();

            msgShowArea.append("我说: " + msgEditArea.getText() + "\n");
            msgShowArea.setCaretPosition(msgShowArea.getText().length()); // 设置插入文本的位置

            msgEditArea.setText(null); // 清空输入框
        } catch (Exception b) {
            b.printStackTrace();
            System.out.println("消息发送错误:" + b);
        }
    }

    public static void main(String[] args) {
        new ServerUI();

    }

}      