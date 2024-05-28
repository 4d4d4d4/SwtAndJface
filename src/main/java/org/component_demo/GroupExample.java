package org.component_demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * @Classname GroupExample
 * @Description 分组
 * @Date 2024/5/26 下午1:27
 * @Created by 憧憬
 */
public class GroupExample {
    public static void main(String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setText("分组实例");
        shell.setSize(700, 400);
        shell.setToolTipText("Shell 容器");

        Group group1 = new Group(shell, SWT.NONE);
        group1.setText("请选择：");
        group1.setBounds(30, 30, 170, 90);

        Button button1 = new Button(group1, SWT.RADIO);
        button1.setText("Group实例");
        button1.setToolTipText("Group实例");
        button1.setBounds(30, 30, 120, 20);

        Button button2= new Button(group1, SWT.RADIO);
        button2.setText("button2实例");
        button2.setToolTipText("button2实例");
        button2.setBounds(30, 50, 120, 20);

        Group group2 = new Group(shell, SWT.BORDER );
        group2.setText("请选择2：");
        group2.setBounds(30, 180, 170, 90);

        Button button3 = new Button(group2, SWT.CHECK);
        button3.setText("Group实例");
        button3.setToolTipText("Group实例");
        button3.setBounds(30, 30, 120, 20);

        Button button4= new Button(group2, SWT.CHECK);
        button4.setText("button2实例");
        button4.setToolTipText("button2实例");
        button4.setBounds(30, 50, 120, 20);

        Comment.maintain(display, shell);
    }
}
