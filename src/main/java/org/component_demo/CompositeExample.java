package org.component_demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

/**
 * @Classname CompositeExample
 * @Description 面板案例
 * @Date 2024/5/26 下午1:45
 * @Created by 憧憬
 */
public class CompositeExample {
    public static void main(String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setSize(600, 460);
        shell.setText("composite实例");
        shell.setToolTipText("composite实例");

        final Composite composite = new Composite(shell, SWT.BORDER);
        composite.setBounds(20, 25, 210, 220);
        composite.setToolTipText("composite容器");

        final Group group = new Group(composite, SWT.NONE);
        group.setBounds(20, 20, 160, 160);
        group.setText("请输入: ");
        group.setToolTipText("group 容器");

        final Label label1 = new Label(group, SWT.NONE);
        label1.setBounds(10, 30, 45, 20);
        label1.setText("用户名: ");

        final Text text1 = new Text(group, SWT.BORDER);
        text1.setBounds(25, 30, 120, 20);

        final Label label2 = new Label(group, SWT.NONE);
        label2.setBounds(10, 60, 45, 20);
        label2.setText("密码: ");

        final Text text2 = new Text(group, SWT.BORDER);
        text2.setBounds(20, 60, 120, 20);

        Button button1 = new Button(group, SWT.BORDER);
        button1.setBounds(20, 105, 40, 25);
        button1.setText("确定");

        Button button2 = new Button(group, SWT.BORDER);
        button2.setBounds(85, 105, 40, 25);
        button2.setText("取消");

        Comment.maintain(display, shell);

    }
}
