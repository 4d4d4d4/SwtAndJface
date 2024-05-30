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
    private JPanel msgShowPanel; // չʾ��Ϣ���
    private JTextArea msgShowArea; // չʾ������Ϣ��
    private JPanel msgEditPanel;  // ��Ϣ�������
    private JTextArea msgEditArea; // ��Ϣ�޸����


    private ServerSocket serverMsg; // �������Ϣsocket
    private static final int msgPort = 11268; // ��Ϣ���Ŷ˿ں�
    private static final int filePort = 8899; // �ļ����Ŷ˿ں�
    private ObjectInputStream ObjISMsg;  // ������Ϣ��
    private ObjectOutputStream ObjOSMsg; // ������Ϣ��

    private String ip;

    private Font font = new Font("Dialog", Font.BOLD, 18);

    //ServerUI���캯��
    public ServerUI() {
        super("��������");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initMsgShowPanel(); //���ó�ʼ����Ϣ��ʾ��庯��
        initMsgEditPanel(); //���ó�ʼ����Ϣ�༭��庯��

        // ����init�г�ʼ��������panel��ӵ�container��
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        pane.add(msgShowPanel, BorderLayout.CENTER);
        pane.add(msgEditPanel, BorderLayout.SOUTH);

//        JPanel p = new JPanel();
//        pane.add(p, BorderLayout.NORTH);

        pack();   //�Զ�������С����
        setVisible(true);

        try {
            serverMsg = new ServerSocket(msgPort); //ͨ�Ŷ˿� ��������Ϣ���Ŷ˿�11268
            // �ȴ��ͻ�������
            new recThread(serverMsg.accept()).start(); //�������ռ����߳�

        } catch (Exception e) {
            System.out.println("server.accept:" + e);
        }

        //Զ�̿��Ʒ����
        RCServer rcs = new RCServer();
        try {
            rcs.startServer(18080);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class recThread extends Thread {//������Ϣ�߳�   


        private byte[] data;

        private String str;

        // ������ͻ��˵������������
        public recThread(Socket c) throws IOException {//�̹߳��캯��   
            ObjOSMsg = new ObjectOutputStream(c.getOutputStream()); // socket�������
            ObjISMsg = new ObjectInputStream(c.getInputStream()); // ����������
            ip = c.getInetAddress().getHostAddress();
            System.out.println("ip=" + ip);

            // �����������ļ��߳�
            ServerFileThread server;
            try {
                server = new ServerFileThread(getContentPane());
                // ����������ļ������߳�
                server.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        public void run() {//�̼߳�������   
            try {
                while (true) {

                    data = (byte[]) ObjISMsg.readObject();
                    str = new String(data);//�ֽ�����ת�ַ���
                    String msg[] = str.split("####");

                    if (str.startsWith("shuo")) {
                        msgShowArea.append("���ƶ�˵��" + msg[1] + "\n");
                    }

                    msgShowArea.setCaretPosition(msgShowArea.getText().length());
                }
            } catch (Exception e) {
                System.out.println("��Ϣ���մ���" + e.getStackTrace());
            }
            ;
        }
    }

    /**
     * ��ʼ����Ϣ��ʾ���
     * �������Ϣչʾ���ĳ�ʼ������Ϣ��ʾ����ĳ�ʼ������δ��Ϣ��ʾ������ӹ�����������Ϣ��ʾ������Ӹ�����Ϣ��ʾ���
     */
    private void initMsgShowPanel() {
        msgShowPanel = new JPanel();
        msgShowPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("��Ϣ��: ");
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
     * ��ʼ������Ϣ������塢��Ϣ�������򲢸���������˹����ᡢ��ʼ���˷��Ͱ�ť��������һ�����Ͱ�ť���ð�ť������һ����Ϣ���ͷ���
     */
    private void initMsgEditPanel() {
        // ��ʼ����Ϣ�޸�/�������
        msgEditPanel = new JPanel();
        msgEditPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("�༭��: ");
        label.setFont(font);

        // ��ʼ����Ϣ�������� ����ӹ�����
        msgEditArea = new JTextArea(5, 50);
        msgEditArea.setLineWrap(true);
        JScrollPane msgEditPane = new JScrollPane();
        msgEditPane.setViewportView(msgEditArea);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());


        JButton sendButton = new JButton("����");
        sendButton.setFont(font);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {//������Ϣ   
                sendMsg("shuo####" + msgEditArea.getText());
            }
        });
        buttonPanel.add(sendButton);

        msgEditPanel.add(label, BorderLayout.NORTH);
        msgEditPanel.add(msgEditPane, BorderLayout.CENTER);
        msgEditPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * ������Ϣ3
     * ������������д�뵽ObjOSMsg���С����Լ�˵����Ϣд�뵽��Ϣ��ʾ�� ��շ��Ϳ�
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        try {
            byte[] data = msg.getBytes();

            ObjOSMsg.writeObject(data); // ��Ҫ�ȵ�recThread�̳߳�ʼ�����
            ObjOSMsg.flush();

            msgShowArea.append("��˵: " + msgEditArea.getText() + "\n");
            msgShowArea.setCaretPosition(msgShowArea.getText().length()); // ���ò����ı���λ��

            msgEditArea.setText(null); // ��������
        } catch (Exception b) {
            b.printStackTrace();
            System.out.println("��Ϣ���ʹ���:" + b);
        }
    }

    public static void main(String[] args) {
        new ServerUI();

    }

}      