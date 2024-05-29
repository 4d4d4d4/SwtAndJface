package org.component_demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @Classname FileDialogExample
 * @Description 什么也没有写哦~
 * @Date 2024/5/29 下午4:21
 * @Created by 憧憬
 */
public class FileDialogExample {
    public static void main(String[] args) {
        final Shell shell = new Shell();
        FileDialog OpenFileDialog = new FileDialog(shell, SWT.OPEN);
        OpenFileDialog.setText("打开型文件选择对话框");
        OpenFileDialog.setFilterExtensions(new String[]{"*.*", "*.psd",
                "*.jpg", "*.txt", "*.doc", "*.exe"});
        OpenFileDialog.setFilterNames(new String[]{"所有类型(*.*)",
                "potoshopg 格式(*.psd)", "文本格式(*.txt)"});
        OpenFileDialog.setFilterPath("C:\\");
        String open = OpenFileDialog.open();
        System.out.println(open);
    }

}
