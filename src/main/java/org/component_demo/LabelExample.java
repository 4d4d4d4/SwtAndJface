package org.component_demo;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @Classname Main
 * @Description 什么也没有写哦~
 * @Date 2024/5/23 下午12:42
 * @Created by 憧憬
 */
public class LabelExample {
    public static void main(String[] args) {
        final Display display = Display.getDefault();
        final Shell shell = new Shell();
        shell.setSize(500, 375);
        shell.setText("Lable 测试界面");

        final Label label1 = new Label(shell, SWT.NONE);
        label1.setBounds(20, 15, 40, 20);
        label1.setText("label");
//        label1.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));

        final Label label2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label2.setBounds(0, 45, 500, 8);
//        label2.setText("label2");
//        label2.setBackground(display.getSystemColor(SWT.COLOR_RED));

        final Label label3 = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
//        label3.setText("label3");
        label3.setBounds(65, 0, 9, 350);
//        label3.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

        final Label label4 = new Label(shell, SWT.NONE);
        label4.setBounds(80, 70, 60, 60);
        label4.setText("label4");
        label4.setBackground(display.getSystemColor(SWT.COLOR_BLUE));

        shell.open();
        shell.layout();
        shell.requestLayout();
        while (!shell.isDisposed()){
            if(!display.readAndDispatch()){
                display.sleep();

            }
        }


    }
}
