package org.component_demo;

/**
 * @Classname CTabExample
 * @Description 什么也没有写哦~
 * @Date 2024/5/29 下午1:58
 * @Created by 憧憬
 */

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;

public class CTabFolderExample {
    private List list;
    private Composite comp;
    private CTabFolder CTab;

    public CTabFolderExample() {
        final Display display = Display.getDefault();
        final Shell shell = new Shell();
        shell.setSize(400, 300);
        shell.setText("CTabFolder 实例");
        shell.setLayout(new GridLayout(7, false));// 设置容器为 7 列
        {
            list = new List(shell, SWT.BORDER);
            list.setItems(new String[]{"Item1", "Item2", "Item3", "Item4",
                    "Item5"});
            GridData gridA = new GridData(GridData.FILL_VERTICAL);
            gridA.horizontalSpan = 4;// 水平强占 4 列             gridA.widthHint = 60;// 使宽度为 60 个像素             gridA.horizontalIndent = 5;// 设置 List 距离 shell 容器左边框为 5 个像素             list.setLayoutData(gridA);
        }
        {
            comp = new Composite(shell, SWT.BORDER);
            GridData gridB = new GridData(GridData.FILL_BOTH);
            // 使 comp 面板双向充满             gridB.horizontalSpan = 3;             comp.setLayoutData(gridB);             comp.setLayout(new GridLayout());
        }
        CTab = new CTabFolder(comp, SWT.BORDER);
        // 定义 CTabItem 对象 Item1
        CTab.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        CTab.setSimple(false);
        final CTabItem Item1 = new CTabItem(CTab, SWT.CLOSE, 0);
        Item1.setText("Item1");
        Text text1 = new Text(CTab, SWT.MULTI);
        Item1.setControl(text1);
        // 设置 text1 背景颜色
        //8.6 自定义选项卡(C TabFodl er类、C TabItem 类)

        text1.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
        final CTabItem Item2 = new CTabItem(CTab, SWT.CLOSE, 1);
        Item2.setText("Item2");
        Text text2 = new Text(CTab, SWT.MULTI);
        // 设置 text2 背景颜色
        text2.setBackground(display.getSystemColor(SWT.COLOR_BLUE
                | SWT.COLOR_BLACK));
        Item2.setControl(text2);
        final CTabItem Item3 = new CTabItem(CTab, SWT.CLOSE, 2);
        Item3.setText("Item3");
        Text text3 = new Text(CTab, SWT.MULTI);
        text3.setBackground(display.getSystemColor(SWT.COLOR_RED));
        Item3.setControl(text3);
        final CTabItem Item4 = new CTabItem(CTab, SWT.CLOSE, 3);
        Item4.setText("Item4");
        Text text4 = new Text(CTab, SWT.MULTI);
        text4.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
        Item4.setControl(text4);
        final CTabItem Item5 = new CTabItem(CTab, SWT.CLOSE, 4);
        Item5.setText("Item5");
        Text text5 = new Text(CTab, SWT.MULTI);
        text5.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
        Item5.setControl(text5);
        // 设置最小化，最大化标签可见
        CTab.setMinimizeVisible(true);
        CTab.setMaximizeVisible(true);
        CTab.setMaximized(true);
        // 设置选项卡的最小化，最大化和恢复功能。
        CTab.addCTabFolder2Listener(new CTabFolder2Adapter() {
            // 使选项卡最小化
            public void minimize(CTabFolderEvent event) {
                CTab.setMinimized(true);
                comp.layout(true);
            }

            // 使选项卡最大化
            public void maximize(CTabFolderEvent event) {
                CTab.setMaximized(true);
                CTab.setLayoutData(new GridData(GridData.FILL_BOTH));
                comp.layout(true);
            }

            // 使选项卡恢复
            public void restore(CTabFolderEvent event) {
                CTab.setMinimized(false);
                CTab.setMaximized(false);
                CTab.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
                comp.layout(true);
            }
        });
        list.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = list.getSelectionIndex();
                if (selectionIndex == 0) {                     // 设 Item1 为当前选择项
                    CTab.setSelection(Item1);
                } else if (selectionIndex == 1) {
                    // 设 Item2 为当前选择项
                    CTab.setSelection(Item2);
                } else if (selectionIndex == 2) {
                    CTab.setSelection(Item3);
                } else if (selectionIndex == 3) {
                    CTab.setSelection(Item4);
                } else if (selectionIndex == 4) {
                    CTab.setSelection(Item5);
                }
            }
        });
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

    public static void main(String[] args) {
        new CTabFolderExample();
    }
}

