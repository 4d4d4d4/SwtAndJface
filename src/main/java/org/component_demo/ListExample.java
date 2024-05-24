package org.component_demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * @Classname ListExample
 * @Description 列表
 * @Date 2024/5/24 下午3:22
 * @Created by 憧憬
 */

public class ListExample {
    public static void main(String[] args) {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setSize(600, 400);
        shell.setText("列表实例");

        // H_SCROLL、V_SCROLL分别代表横竖滚动条
        final List list1 = new List(shell, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        list1.setBounds(50, 45, 80, 80);
        for(int i = 0 ; i < 12; i++){
            list1.add("SWT开发指南第" + i + "章节");
        }

        final List list2 = new List(shell, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        list2.setBounds(130, 50, 80, 60);
        list2.setItems("1", "2", "3", "4", "5", "6");
        list2.addMouseListener(new MouseAdapter() {
            // 鼠标点击事件
            @Override
            public void mouseDown(MouseEvent e) {
                System.out.println("选中的数据是：" + list2.getSelection()[0]);
            }
            // 鼠标抬起事件
            @Override
            public void mouseUp(MouseEvent e) {
                System.out.println("它的序号是: " + list2.getSelectionIndex());
            }
        });

        Comment.maintain(display, shell);

    }
}
