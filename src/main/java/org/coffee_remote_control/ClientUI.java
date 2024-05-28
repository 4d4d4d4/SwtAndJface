package org.coffee_remote_control;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.internal.C;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * @Classname ClientUI
 * @Description 客户端UI界面
 * @Date 2024/5/29 上午1:56
 * @Created by 憧憬
 */
public class ClientUI extends Shell {
    private static final Display display = Display.getDefault();
    private final Composite showTextComposite = new Composite(this, SWT.NONE); // 显示消息面板
    private final Composite editTextComposite = new Composite(this, SWT.NONE); // 发送消息面板

    public ClientUI() {
        super(display);
        this.setSize(650, 480);
        this.setText("服务端");
        this.setToolTipText("服务器");
        FontDialog fontdialog = new FontDialog(this, SWT.NONE);
        fontdialog.setFontList(new FontData[]{new FontData("宋体", 14, SWT.BOLD | SWT.ITALIC)});
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);


        // 初始化显示消息面板
        initShowTextComposite();
        // 初始化消息发送面板
        initEditTextComposite();
        this.open();
        this.layout();
        while (!this.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    // 初始化发送面板
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

        Text editText = new Text(editTextComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP); // 多行 有线条 换行
        editText.setTextLimit(150);
        editText.setBackground(display.getSystemColor(SWT.COLOR_WHITE)); // 设置颜色白色
        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = SWT.FILL;
        gridData1.verticalAlignment = SWT.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = true;
        editText.setLayoutData(gridData1);

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

                System.out.println("发送了消息");
            }
        });
    }

    // 初始化显示面板
    private void initShowTextComposite() {
    }

    @Override
    protected void checkSubclass() {
    }

    public static void main(){
        new ClientUI();
    }
}
