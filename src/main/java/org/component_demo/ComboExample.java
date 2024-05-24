package org.component_demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @Classname ComboExample
 * @Description 下拉框
 * @Date 2024/5/24 下午2:45
 * @Created by 憧憬
 */
public class ComboExample {
    public static void main(String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setSize(600, 400);
        shell.setText("下拉框实例");

        final Combo combo = new Combo(shell, SWT.SIMPLE | SWT.READ_ONLY); // 只读 无下拉按钮 排列展示
        combo.setBounds(50, 50, 90, 90);
        combo.setItems("Eclipse", "SWT", "Jface");

        final Combo combo1 = getCombo(shell);  // 下拉可修改

        final Button button2 = new Button(shell, SWT.NONE);
        button2.setBounds(200, 115, 45, 25);
        button2.setText("清空");
        button2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                combo1.removeAll();
            }
        });

        Comment.maintain(display, shell);
    }

    private static Combo getCombo(Shell shell) {
        final Combo combo1 = new Combo(shell, SWT.DROP_DOWN);
        combo1.setBounds(150, 50, 90, 20);
        for (int i = 0; i < 8; i++) {
            combo1.add("page" + i);
        }
        final Button button1 = new Button(shell, SWT.NONE);
        button1.setBounds(145, 115, 45, 25);
        button1.setText("OK");
        // 添加到下拉列表
        button1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                combo1.add(combo1.getText());
            }
        });
        return combo1;
    }
}
