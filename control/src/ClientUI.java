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
    private JPanel connectPanel;// �������
    private JPanel controlPanel;// Զ�̿������
    private JPanel msgShowPanel;// ��Ϣ��ʾ���
    private JTextArea msgShowArea;// ��Ϣ��ʾ��
    private JPanel msgEditPanel;// ��Ϣ�༭���
    private JTextArea msgEditArea;// ��Ϣ���ͱ༭��
    Container pane = null;
    private static final int msgPort = 11268;// ��Ϣ���ͽ��ն˿�
    private ObjectInputStream ObjISMsg;// ��Ϣ������
    private ObjectOutputStream ObjOSMsg;// ��Ϣ�����
    private Font font = new Font("Dialog", Font.BOLD, 18);// ����������ʽ
    Socket socket = null;
    private String serverIp = null;

    private JTabbedPane jTabbedpane = null;// ���ѡ������
    private JButton jbtn = null;//���Ͱ�ť

    private DataInputStream dis;// ���ܱ����ƶ˷�����ͼƬ
    private ObjectOutputStream oos;// ���Ϳ����¼�
    private JLabel backImage;// �˱���ʹ��һ��JLable��ʾͼƬ
    private Socket client;

    /**
     * ClientUI���캯��
     */
    public ClientUI() {
        super("�ͻ���");
        String input = JOptionPane.showInputDialog("������Ҫ���ӵķ�����(192.168.0.2:18080):", "192.168.1.9:11268");
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
        initConnectPanel();// ��ʼ���������
        initMsgShowPanel();// ��ʼ����Ϣ��ʾ���
        initMsgEditPanel();// ��ʼ����Ϣ�༭���
        initcontrolPanel();

        jTabbedpane.addTab("���촰��", msgShowPanel);
        jTabbedpane.addTab("�ļ����", connectPanel);
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
        jTabbedpane.addTab("Զ�̿���", scrollPanef);
        setLayout(new BorderLayout());
        //pane.add(connectPanel, BorderLayout.NORTH);
        add(jTabbedpane, BorderLayout.CENTER);
        add(msgEditPanel, BorderLayout.SOUTH);
        pack();
        setVisible(true);
        new ImgThread(serverIp, 18080).start();

    }

    /**
     * ��ʼ���������
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
     * �����¼�
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
     * ����ͼƬ���ݸ��¿��ƶ˽���
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
     * �������ƶη�����������
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
     * ��ʼ���������
     */
    private void initConnectPanel() {
        connectPanel = new JPanel();
        jbtn = new JButton("ѡ���ļ�");
        connectPanel.add(jbtn);

        //�����ļ���ťʱ�¼�
        jbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {

                JFileChooser sourceFileChooser = new JFileChooser(".");
                sourceFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int status = sourceFileChooser.showOpenDialog(pane);
                File sourceFile = new File(sourceFileChooser.getSelectedFile().getPath());
                //������text area��ʾ
                msgShowArea.append("�����ļ���" + sourceFile.getName() + "\r\n");
                sendMsg("shuo####���ƶ˸��㷢����һ���ļ�");
                ClientFileThread client;
                try {
                    client = new ClientFileThread(serverIp);
                    // �����ͻ�������
                    client.sendFile(sourceFile); // �����ļ�
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });


    }

    public void connectServer(String host, int port) {// ���ӷ�����
        try {
            socket = new Socket(host, port);
            new recThread(socket).start();// ������Ϣ�߳�

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("���ӳ���:" + e);
        }
    }

    class recThread extends Thread {// ������Ϣ�߳�

        private byte[] data;// ԭʼ��Ϣ
        private String str;

        public recThread(Socket sMsg) throws IOException {// �̹߳��캯��
            ObjOSMsg = new ObjectOutputStream(sMsg.getOutputStream());// ���������
            ObjISMsg = new ObjectInputStream(sMsg.getInputStream());// ����������
        }

        public void run() {// �̼߳�������
            try {
                while (true) {

                    data = (byte[]) ObjISMsg.readObject();
                    str = new String(data);
                    String msg[] = str.split("####");
                    if (str.startsWith("shuo")) {
                        msgShowArea.append("Զ�̶�˵��" + msg[1] + " \n");
                    }
                    if (str.startsWith("file")) {
                        msgShowArea.append("Զ�̶�˵��" + msg[1] + " \n");
                    }
                }
            } catch (Exception e) {
                System.out.println("��Ϣ���մ���" + e);
            }
            ;
        }
    }

    class ImgThread extends Thread {// ������Ϣ�߳�
        private String host;
        private int port;

        public ImgThread(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public ImgThread(Socket sMsg) throws IOException {// �̹߳��캯��
            ObjOSMsg = new ObjectOutputStream(sMsg.getOutputStream());// ���������
            ObjISMsg = new ObjectInputStream(sMsg.getInputStream());// ����������
        }

        public void run() {// �̼߳�������
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
     * ��ʼ����Ϣ��ʾ���
     */
    private void initMsgShowPanel() { // ��ʼ����Ϣ��ʾ���
        msgShowPanel = new JPanel();
        msgShowPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("��ʾ��: ");
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
     * ��ʼ����Ϣ�༭���
     */
    private void initMsgEditPanel() { // ��ʼ�������ͱ༭���
        msgEditPanel = new JPanel();
        msgEditPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("�༭��: ");
        label.setFont(font);

        msgEditArea = new JTextArea(5, 50);
        msgEditArea.setLineWrap(true);
        JScrollPane msgEditPane = new JScrollPane();
        msgEditPane.setViewportView(msgEditArea);

        JPanel buttonPanel = new JPanel();
        JButton sendButton = new JButton("����");
        sendButton.setFont(font);
        // boolean result=true;

        sendButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {// ������Ϣ
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
            msgShowArea.append("��" + "˵:" + msgEditArea.getText() + "\n");
            msgShowArea.setCaretPosition(msgShowArea.getText().length());
            msgEditArea.setText(null);
        } catch (Exception b) {
            b.printStackTrace();
            System.out.println("��Ϣ���ʹ���:" + b);
        }

    }

    public static void main(String[] args) {

        new ClientUI(); // �����µ� ClientUI()����
    }


}