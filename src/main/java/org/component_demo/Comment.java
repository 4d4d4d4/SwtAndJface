package org.component_demo;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @Classname Comment
 * @Description 维持界面
 * @Date 2024/5/24 下午2:47
 * @Created by 憧憬
 */
public class Comment {
    public static void maintain(Display display, Shell shell){
        shell.open();
        shell.layout();
//        shell.requestLayout();
        while (!shell.isDisposed()){
            if(!display.readAndDispatch()){
                display.sleep();
            }
        }
    }
}
