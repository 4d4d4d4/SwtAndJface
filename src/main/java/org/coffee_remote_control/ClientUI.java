package org.coffee_remote_control;

import org.apache.lucene.document.Field;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Classname ClientUI
 * @Description 客户端UI界面
 * @Date 2024/5/29 上午1:56
 * @Created by 憧憬
 */
public class ClientUI extends Shell {
    private static final Display display = Display.getDefault();
    private Composite showTextComposite = null; // 显示消息面板
    private Composite editTextComposite = null; // 发送消息面板
    private Composite fileComposite = null; // 文件传输面板
    private Composite chatComposite = null; // 聊天面板
    private CTabItem message = null; // 消息聊天
    private CTabItem file = null; // 文件传输选项卡
    private CTabItem remote = null; // 远程连接选项卡
    private Composite ctabComposite = null; // 选项卡面板
    private Composite remoteComposite = null; // 远程连接面板
    private String serverIp = null; // 服务器ip地址
    private Integer serverPort = null; // 服务器端口
    private Integer filePort =  5433; // 文件传输端口 固定的
    private CTabFolder CTab; // 选项卡
    private Socket socket; // 套接字
    private Text showText; // 展示消息框
    private  Text editTexts = null; // 发送消息框
    private ObjectInputStream objISMsg;  // 输入流
    private ObjectOutputStream objOSMsg; // 输出流

    public ClientUI() {
        super(display);
        this.setSize(650, 480);
        this.setText("客户端");
        this.setToolTipText("客户端");
        FontDialog fontdialog = new FontDialog(this, SWT.NONE);
        fontdialog.setFontList(new FontData[]{new FontData("宋体", 14, SWT.BOLD | SWT.ITALIC)});
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);
        this.setBackground(display.getSystemColor(SWT.COLOR_RED));

        // 初始化登录框
        InputDialog dialog = new InputDialog(null, "连接助手", "请输入服务器地址:", "127.0.0.1:8848", null);
        if (dialog.open() != Window.OK) {
            return;
        }
        // 正则匹配输入
        String address = dialog.getValue();
        Pattern pattern = Pattern.compile("(\\d+.\\d+.\\d+.\\d+):(\\d+)");
        Matcher matcher = pattern.matcher(address);
        if (!matcher.find()) {
            MessageDialog.openInformation(this, "信息提示", "提交失败 信息项不能为空");
//            new ClientUI();
            return;
        }

        serverIp = matcher.group(1);
        serverPort = Integer.valueOf(matcher.group(2));
        // 连接服务器
        connectServer(serverIp, serverPort);

        // 初始化选项卡面板
        ctabComposite = new Composite(this, SWT.NONE);
        ctabComposite.setLayout(new GridLayout());
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = SWT.FILL;
        gridData.grabExcessVerticalSpace = true;
        ctabComposite.setLayoutData(gridData);
        ctabComposite.setBackground(display.getSystemColor(SWT.COLOR_BLUE));

        CTab = new CTabFolder(ctabComposite, SWT.BORDER);
        CTab.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        // 创建每个选项卡及其内容
        createMessageTab();
        createFileTab();
        createRemoteTab();

        // 初始化显示消息面板
//        initShowTextComposite();
        this.open();
        this.layout();
        while (!this.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void connectServer(String serverIp, Integer serverPort) {
        try {
            System.out.println(serverIp + ":" + serverPort);
            socket = new Socket(serverIp, serverPort);
            new RecThread(socket).start();
        } catch (IOException e) {
            System.out.println("服务器套接字连接失败");
            MessageDialog.openInformation(this, "连接失败", "请确认url输入正确|请确认服务器开放");
//            new ClientUI();
            throw new RuntimeException(e);
        }
    }

    // 创建消息聊天选项卡
    private void createMessageTab() {
        chatComposite = new Composite(CTab, SWT.NONE);
        message = new CTabItem(CTab, SWT.FLAT, 0);
        message.setText("聊天窗口");
        message.setControl(chatComposite);

        chatComposite.setLayout(new GridLayout());

        initShowText(chatComposite);
        initEditText(chatComposite);

        CTab.setSelection(message);
    }

    private void initShowText(Composite chatComposite) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL; // 水平填充
        gridData.grabExcessHorizontalSpace = true; // 水平动态拉伸
        gridData.verticalAlignment = SWT.FILL; // 纵向填
        gridData.heightHint = 230;
        gridData.grabExcessVerticalSpace = true; // 纵向拉伸
        chatComposite.setLayoutData(gridData);

        showTextComposite = new Composite(chatComposite, SWT.NONE);
        showTextComposite.setLayoutData(gridData);
        showTextComposite.setLayout(new GridLayout());
        showTextComposite.setToolTipText("消息展示面板");

        Font heitFont = new Font(display, "黑体", 11, SWT.TITLE);
        Label textTitle = new Label(showTextComposite, SWT.TITLE);
        textTitle.setText("消息框：");
        textTitle.setFont(heitFont);

        showText = new Text(showTextComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        showText.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        showText.setEnabled(false);
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = SWT.FILL;
        gridData1.verticalAlignment = SWT.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        showText.setLayoutData(gridData1);
    }

    private void initEditText(Composite chatComposite) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL; // 水平填充
        gridData.grabExcessHorizontalSpace = true; // 水平动态拉伸
        gridData.verticalAlignment = SWT.FILL; // 纵向
        gridData.grabExcessVerticalSpace = true; // 纵向拉伸
        gridData.heightHint = 180;
        editTextComposite = new Composite(chatComposite, SWT.NONE);
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
        if (objOSMsg == null) {
            return;
        }

        String sendText = "khd##" + text;
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
            System.out.println("客户端消息发送失败" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 创建文件传输选项卡
    private void createFileTab() {
        file = new CTabItem(CTab, SWT.NONE);
        file.setText("文件传输");
        fileComposite = new Composite(CTab, SWT.NONE);
        fileComposite.setLayout(new GridLayout());
        file.setControl(fileComposite);
        Composite buttonComposite = new Composite(fileComposite, SWT.BORDER);
        buttonComposite.setLayout(new GridLayout());
        GridData gridData = new GridData();
        gridData.verticalAlignment = SWT.DEFAULT;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalIndent = 20;
        gridData.heightHint = 180;
        buttonComposite.setLayoutData(gridData);

        Button upload = new Button(buttonComposite, SWT.CENTER | SWT.FLAT);
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = SWT.CENTER;
        gridData1.verticalAlignment = SWT.DEFAULT;
        gridData1.verticalIndent = 20;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        gridData1.heightHint = 30;
        gridData1.widthHint = 80;
        upload.setLayoutData(gridData1);
        upload.setText("选择文件");
        // 绑定文件传输对话框
        upload.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog OpenFileDialog = new FileDialog(fileComposite.getShell(), SWT.OPEN);
                OpenFileDialog.setText("打开型文件选择对话框");
                OpenFileDialog.setFilterExtensions(new String[]{"*.*", "*.psd",
                        "*.jpg", "*.txt", "*.doc", "*.exe"});
                OpenFileDialog.setFilterNames(new String[]{"所有类型(*.*)",
                        "potoshopg 格式(*.psd)", "文本格式(*.txt)"});
                OpenFileDialog.setFilterPath("C:\\");
                String open = OpenFileDialog.open(); // 获取文件路径
                if(null == open) {
                    return;
                }
                try {
                    File source = new File(open);
                    ClientFileSocket clientFileSocket = new ClientFileSocket(serverIp, filePort);
                    clientFileSocket.sendFile(source);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                MessageDialog.openInformation(getShell(), "文件传输助手", "文件传输成功");
            }
        });
        // 延续聊天的对话框
//        initEditText(fileComposite);
    }

    // 创建远程控制选项卡
    private void createRemoteTab() {
        remote = new CTabItem(CTab, SWT.NONE);
        remote.setText("远程控制");

        remoteComposite = new Composite(CTab, SWT.NONE);
        remoteComposite.setLayout(new GridLayout());
        remote.setControl(remoteComposite);
        GridData gridData = new GridData();
        gridData.verticalAlignment = SWT.FILL;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        remoteComposite.setLayoutData(gridData);
        ImageLoader loader = new ImageLoader();
        ImageData[] load = loader.load("F:\\壁纸\\idea壁纸.jpg");
        Image newImage = new Image(null, load[0]);
        // 图片适配
        remoteComposite.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                int compositeWidth = remoteComposite.getBounds().width;
                int compositeHeight = remoteComposite.getBounds().height;

                // 计算图片的缩放比例
                float scaleX = (float) compositeWidth / newImage.getBounds().width;
                float scaleY = (float) compositeHeight / newImage.getBounds().height;
                float scale = Math.min(scaleX, scaleY); // 选择较小的比例以保持图片宽高比

                // 计算缩放后的图片尺寸
                int scaledWidth = (int) (newImage.getBounds().width * scale);
                int scaledHeight = (int) (newImage.getBounds().height * scale);

                // 绘制缩放后的图片
                e.gc.drawImage(newImage, 0, 0, newImage.getBounds().width, newImage.getBounds().height,
                        (compositeWidth - scaledWidth) / 2, (compositeHeight - scaledHeight) / 2,
                        scaledWidth, scaledHeight);
            }
        });
        remoteComposite.setBackgroundImage(newImage);

    }


    @Override
    protected void checkSubclass() {
    }


    private class RecThread extends Thread {
        @Override
        public void run() {
            System.out.println("等待服务端发送消息");
            while (true) {
                try {
                    byte[] data = (byte[]) objISMsg.readObject();
                    String message = new String(data);
                    String[] split = message.split("##"); // kfd##输入的消息
                    Display display = Display.getDefault(); // 获取当前Display实例
                    display.asyncExec(() -> {
                        if (split.length >1 && message.startsWith("fwd")) {
                            showText.append("服务端说：" + split[1] + "\n");
                        }
                        showText.setOrientation(showText.getText().length());
                    });
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        public RecThread(Socket socket) {
            try {
                objISMsg = new ObjectInputStream(socket.getInputStream());
                objOSMsg = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void main(String[] args) {
        new ClientUI();
    }
}
