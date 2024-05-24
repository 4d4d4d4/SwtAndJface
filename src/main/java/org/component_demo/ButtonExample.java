package org.component_demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @Classname ButtonExample
 * @Description 什么也没有写哦~
 * @Date 2024/5/23 下午2:09
 * @Created by 憧憬
 */
public class ButtonExample {
    public static void main(String[] args) {
        final Display display = Display.getDefault();
        final Shell shell = new Shell();
        shell.setSize(410, 200);
        // 设置窗体的标题文字
        shell.setText("Button 实例");
        final Button button1 = new Button(shell, SWT.NONE);
        button1.setText("SWT.NONE");
        button1.setBounds(15, 20, 60, 35);
        button1.setToolTipText("这是NONE的样式");

        final Button button2 = new Button(shell, SWT.CHECK);
        button2.setText("SWT.CHECK");
        button2.setBounds(110, 20, 75, 35);
        button2.setToolTipText("这是CHECK的样式");

        final Button button3 = new Button(shell, SWT.RADIO);
        button3.setText("SWT.RADIO");
        button3.setBounds(210, 25, 75, 25);
        button3.setToolTipText("这是RADIO的样式");

        final Button button4 = new Button(shell, SWT.ARROW);
        button4.setText("SWT.ARROW");
        button4.setBounds(310, 25, 45, 25);
        button4.setToolTipText("这是ARROW的样式");

        final Button button5 = new Button(shell, SWT.BORDER);
        button5.setText("SWT.BORDER");
        button5.setBounds(15, 75, 75, 25);
        button5.setToolTipText("这是BORDER的样式");

        final Button button6 = new Button(shell, SWT.TOGGLE);
        button6.setText("SWT.TOGGLE");
        button6.setBounds(110, 74, 75, 25);
        button6.setToolTipText("这是TOGGLE的样式");

        final Button button7 = new Button(shell, SWT.FLAT);
        button7.setText("SWT.FLAT");
        button7.setBounds(210, 74, 75, 35);
        button7.setToolTipText("这是FLAT的样式");

        final Button button8 = new Button(shell, SWT.NONE);
        button8.setText("setEnabled");
        button8.setBounds(310, 74, 75, 35);
        button8.setToolTipText("setEnabled");
        button8.setEnabled(false);


        shell.open();
        shell.requestLayout();
        while(!shell.isDisposed()){
            if(!display.readAndDispatch()){
                display.sleep();
            }
        }


    }
}

