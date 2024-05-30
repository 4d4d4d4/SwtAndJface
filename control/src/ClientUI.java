import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class ClientUI extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel connectPanel;// 链接面板
    private JPanel controlPanel;// 远程控制面板
    private JPanel msgShowPanel;// 信息显示面板
    private JTextArea msgShowArea;// 信息显示框
    private JPanel msgEditPanel;// 信息编辑面板
    private JTextArea msgEditArea;// 信息发送编辑框
    Container pane = null;
    private static final int msgPort = 11268;// 信息发送接收端口
    private ObjectInputStream ObjISMsg;// 信息输入流
    private ObjectOutputStream ObjOSMsg;// 信息输出流
    private Font font = new Font("Dialog", Font.BOLD, 18);// 设置字体样式
    Socket socket = null;
    private String serverIp = null;

    private JTabbedPane jTabbedpane = null;// 存放选项卡的组件
    private JButton jbtn = null;//发送按钮

    private DataInputStream dis;// 接受被控制端发来的图片
    private ObjectOutputStream oos;// 发送控制事件
    private JLabel backImage;// 此本版使用一个JLable显示图片
    private Socket client;

    /**
     * ClientUI构造函数
     */
    public ClientUI() {
        super("客户端");
        String input = JOptionPane.showInputDialog("请输入要连接的服务器(192.168.0.2:18080):", "192.168.1.9:11268");
        if (input == null) {
            return;
        }
        Pattern pattern = Pattern.compile("(\\d+.\\d+.\\d+.\\d+):(\\d+)");
        java.util.regex.Matcher m = pattern.matcher(input);
        if (!m.matches()) {
            return;
        }
        serverIp = m.group(1);
        int port = Integer.parseInt(m.group(2));
        connectServer(serverIp, port);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(300, 200, 300, 400);
        jTabbedpane = new JTabbedPane();
        initConnectPanel();// 初始化连接面板
        initMsgShowPanel();// 初始化消息显示面板
        initMsgEditPanel();// 初始化消息编辑面板
        initcontrolPanel();

        jTabbedpane.addTab("聊天窗口", msgShowPanel);
        jTabbedpane.addTab("文件浏览", connectPanel);
        jTabbedpane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int n = jTabbedpane.getSelectedIndex();
                if (n == 2) {
                    msgEditPanel.setVisible(false);
                } else {
                    msgEditPanel.setVisible(true);
                }

            }
        });

        JScrollPane scrollPanef = new JScrollPane(controlPanel);
        jTabbedpane.addTab("远程控制", scrollPanef);
        setLayout(new BorderLayout());
        //pane.add(connectPanel, BorderLayout.NORTH);
        add(jTabbedpane, BorderLayout.CENTER);
        add(msgEditPanel, BorderLayout.SOUTH);
        pack();
        setVisible(true);
        new ImgThread(serverIp, 18080).start();

    }

    /**
     * 初始化连接面板
     */
    private void initcontrolPanel() {
        controlPanel = new JPanel();
        controlPanel.setSize(1050, 800);
        backImage = new JLabel();
        JPanel pane = new JPanel();
        JScrollPane scrollPane = new JScrollPane(pane);
        pane.setLayout(new FlowLayout());
        pane.add(backImage);
        controlPanel.add(scrollPane);

        addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                sendEventObject(e);
            }

            public void keyReleased(KeyEvent e) {
                sendEventObject(e);
            }

            public void keyTyped(KeyEvent e) {
            }
        });
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                sendEventObject(e);
            }
        });
        backImage.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                sendEventObject(e);
            }

            public void mouseMoved(MouseEvent e) {
                if (Math.random() > 0.8)
                    sendEventObject(e);
            }
        });
        backImage.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                sendEventObject(e);
            }

            public void mouseReleased(MouseEvent e) {
                sendEventObject(e);
            }
        });
    }

    /**
     * 发送事件
     *
     * @param event
     */
    private void sendEventObject(java.awt.event.InputEvent event) {
        try {
            oos.writeObject(event);
        } catch (Exception ef) {
            ef.printStackTrace();
        }
    }

    public Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * 根据图片数据更新控制端界面
     *
     * @param imageData
     */
    public void update(byte[] imageData) {
        ImageIcon image = new ImageIcon(imageData);
        backImage.setIcon(image);
        controlPanel.repaint();
    }

    public void updateSize(Dimension client) {
        Dimension clientSize = getScreenSize();
        double width = 0, height = 0;
        if (clientSize.getWidth() >= client.getWidth()) {
            width = client.getWidth() + 60;
        } else {
            width = clientSize.getWidth();
        }
        if ((clientSize.getHeight() - client.getHeight()) > 0) {
            height = client.getHeight() + 60;
        } else {
            height = clientSize.getHeight();
        }
        //controlPanelsetSize((int) width, (int) height);
        controlPanel.setSize((int) width, (int) height);

    }

    /**
     * 读被控制段发送来的数据
     *
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public byte[] readBytes() {
        byte[] data = null;
        try {
            int len = dis.readInt();
            data = new byte[len];
            dis.readFully(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 初始化连接面板
     */
    private void initConnectPanel() {
        connectPanel = new JPanel();
        jbtn = new JButton("选择文件");
        connectPanel.add(jbtn);

        //单机文件按钮时事件
        jbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {

                JFileChooser sourceFileChooser = new JFileChooser(".");
                sourceFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int status = sourceFileChooser.showOpenDialog(pane);
                File sourceFile = new File(sourceFileChooser.getSelectedFile().getPath());
                //服务器text area提示
                msgShowArea.append("发送文件：" + sourceFile.getName() + "\r\n");
                sendMsg("shuo####控制端给你发送了一个文件");
                ClientFileThread client;
                try {
                    client = new ClientFileThread(serverIp);
                    // 启动客户端连接
                    client.sendFile(sourceFile); // 传输文件
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });


    }

    public void connectServer(String host, int port) {// 连接服务器
        try {
            socket = new Socket(host, port);
            new recThread(socket).start();// 接收信息线程

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接出错:" + e);
        }
    }

    class recThread extends Thread {// 接收信息线程

        private byte[] data;// 原始消息
        private String str;

        public recThread(Socket sMsg) throws IOException {// 线程构造函数
            ObjOSMsg = new ObjectOutputStream(sMsg.getOutputStream());// 创建输出流
            ObjISMsg = new ObjectInputStream(sMsg.getInputStream());// 创建输入流
        }

        public void run() {// 线程监听函数
            try {
                while (true) {

                    data = (byte[]) ObjISMsg.readObject();
                    str = new String(data);
                    String msg[] = str.split("####");
                    if (str.startsWith("shuo")) {
                        msgShowArea.append("远程端说：" + msg[1] + " \n");
                    }
                    if (str.startsWith("file")) {
                        msgShowArea.append("远程端说：" + msg[1] + " \n");
                    }
                }
            } catch (Exception e) {
                System.out.println("消息接收错误：" + e);
            }
            ;
        }
    }

    class ImgThread extends Thread {// 接收信息线程
        private String host;
        private int port;

        public ImgThread(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public ImgThread(Socket sMsg) throws IOException {// 线程构造函数
            ObjOSMsg = new ObjectOutputStream(sMsg.getOutputStream());// 创建输出流
            ObjISMsg = new ObjectInputStream(sMsg.getInputStream());// 创建输入流
        }

        public void run() {// 线程监听函数
            try {
                client = new Socket(host, port);
                oos = new ObjectOutputStream(client.getOutputStream());
                dis = new DataInputStream(client.getInputStream());
                double height = 100;
                double width = 100;
                height = dis.readDouble();
                width = dis.readDouble();
                updateSize(new Dimension((int) width, (int) height));
                while (true) {
                    byte[] imageData = readBytes();
                    update(imageData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化消息显示面板
     */
    private void initMsgShowPanel() { // 初始化消息显示面板
        msgShowPanel = new JPanel();
        msgShowPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("显示框: ");
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
     */
    private void initMsgEditPanel() { // 初始化消发送编辑面板
        msgEditPanel = new JPanel();
        msgEditPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("编辑框: ");
        label.setFont(font);

        msgEditArea = new JTextArea(5, 50);
        msgEditArea.setLineWrap(true);
        JScrollPane msgEditPane = new JScrollPane();
        msgEditPane.setViewportView(msgEditArea);

        JPanel buttonPanel = new JPanel();
        JButton sendButton = new JButton("发送");
        sendButton.setFont(font);
        // boolean result=true;

        sendButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {// 发送消息
                sendMsg("shuo####" + msgEditArea.getText());
            }
        });
        buttonPanel.add(sendButton);


        msgEditPanel.add(label, BorderLayout.NORTH);
        msgEditPanel.add(msgEditPane, BorderLayout.CENTER);
        msgEditPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void sendMsg(String msg) {
        try {
            byte[] data = msg.getBytes();
            ObjOSMsg.writeObject(data);
            ObjOSMsg.flush();
            msgShowArea.append("我" + "说:" + msgEditArea.getText() + "\n");
            msgShowArea.setCaretPosition(msgShowArea.getText().length());
            msgEditArea.setText(null);
        } catch (Exception b) {
            b.printStackTrace();
            System.out.println("消息发送错误:" + b);
        }

    }

    public static void main(String[] args) {

        new ClientUI(); // 生成新的 ClientUI()对象
    }


}