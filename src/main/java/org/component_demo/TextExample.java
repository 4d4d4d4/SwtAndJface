package org.component_demo;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
//import org.eclipse.core.runtime.IStatus;
/**
 * @Classname TextExample
 * @Description 什么也没有写哦~
 * @Date 2024/5/23 下午2:37
 * @Created by 憧憬
 */
public class TextExample {
    public static void main(String[] args) {
        final Display display = Display.getDefault();
        final Shell shell = new Shell();
        shell.setSize(650, 435);
        shell.setText("Text综合实例");
        final Label label1 = new Label(shell, SWT.NONE);
        label1.setBounds(25, 25, 90, 25);
        label1.setText("User Name:");
        final Text text1 = new Text(shell, SWT.BORDER); // 展示边框
        text1.setBounds(120, 25, 110, 20);
        text1.setToolTipText("此选项不能为空");


        final Label label2 = new Label(shell, SWT.NONE);
        label2.setText("User Password:");
        label2.setBounds(260, 25, 110, 25);
        final Text text2 = new Text(shell, SWT.PASSWORD | SWT.BORDER);
        text2.setBounds(380, 25, 110, 20);
        text2.setTextLimit(8); // 限制输入长度
        text2.setToolTipText("此选项不能为空, 密码最多为8位");

        final Text text3 = new Text(shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        text3.setBounds(50, 55, 250, 90);

        final Button button1 = new Button(shell, SWT.NONE);
        button1.setBounds(140, 160, 80, 20);
        button1.setText("OK");
        button1.setToolTipText("点击ok按钮， 姓名将显示到下方文本框中");

        button1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String userName = text1.getText();
                String password = text2.getText();
                if (userName == null || userName.isEmpty() || password == null || password.isEmpty()){
                    MessageDialog.openInformation(shell, "信息提示", "提交失败 信息项不能为空");
                }else {
                    text3.append("username: " + userName + "      password: " + password);
                    text3.insert("\n" + "username :   " + userName + "   password: " + password + " \n");
                }

            }
        });


        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }


    }
}
