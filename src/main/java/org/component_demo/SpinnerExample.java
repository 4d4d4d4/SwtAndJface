package org.component_demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 * @Classname SpinnerExample
 * @Description 微调控制组件 用于调控数字
 * @Date 2024/5/24 下午2:29
 * @Created by 憧憬
 */
public class SpinnerExample {
    public static void main(String[] args) {
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setText("Spinner实例");
        shell.setSize(300, 200);

        final Spinner spinner = new Spinner(shell, SWT.READ_ONLY | SWT.BORDER);
        spinner.setBounds(30, 20, 150, 60);
        spinner.setDigits(3); // 保留三位小数
        spinner.setMinimum(5); // 设置最小值  由于上面设置了3 所以最小调控值为 0.01  mininum/10**digits
        spinner.setMaximum(1000); // 设置最大值
        spinner.setSelection(0); // 默认值
        spinner.setIncrement(1); // 点击箭头的变化值 至少为1
        int selection = spinner.getSelection();
        String text = spinner.getText();
        System.out.println(text); // 获得的值为实际值/Math.power(10, digit);
        System.out.println(selection); // 获取的实际值
        shell.open();
        while (!shell.isDisposed()){
            if(!display.readAndDispatch()){
                display.sleep();
            }
        }
    }
}
