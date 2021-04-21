package com.developcollect.core.swing.explorer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;

import java.awt.*;
import java.io.IOException;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/5 11:09
 */
public class WindowsExplorer implements IExplorer {

    //Explorer /n
    //此命令使用默认设置打开一个资源管理器窗口。显示的内容通常是安装 Windows 的驱动器的根目录。
    //Explorer /e
    //此命令使用默认视图启动 Windows 资源管理器。
    //Explorer /e,C:\Windows
    //此命令使用默认视图启动 Windows 资源管理器，并把焦点定位在 C:\Windows。
    //Explorer /root, C:\Windows\Cursors
    //此命令启动 Windows 资源管理器后焦点定位在 C:\Windows\Cursors folder。此示例使用
    //C:\Windows\Cursors 作为 Windows 资源管理器的“根”目录。
    //备注：请注意命令中“/root”参数后面的逗号。
    //Explorer /select, C:\Windows\Cursors\banana.ani
    //此命令启动 Windows 资源管理器后选定“C:\Windows\Cursors\banana.ani”文件。
    //备注：请注意命令中“/select”参数后面的逗号。
    //Windows 资源管理器参数可以在一个命令中进行组合。以下示例显示了 Windows 资源管理器命令行参数的组合。
    //Explorer /root, \\server\share, select, Program.exe
    //此命令启动 Windows 资源管理器时以远程共享作为“根”文件夹，而且 Program.exe 文件将被选中。回到顶端
    //更改 Windows 资源管理器默认启动文件夹
    //若要更改 Windows 资源管理器的默认启动文件夹，请：
    //单击开始，指向所有程序，指向附件，然后右键单击Windows Explorer。
    //在出现的菜单上，单击属性。
    //在“目标”框中，将“/root”命令行参数附加到“%SystemRoot%\Explorer.exe”命令之后，并使用您希望的启动位置。


    @Override
    public void select(String filePath) {
        // Explorer /select, C:\Windows\Cursors\banana.ani
        String cmd = String.format("Explorer /select, %s", filePath);
        RuntimeUtil.exec(cmd);
    }

    @Override
    public void open(String filePath) {
        try {
            Desktop.getDesktop().open(FileUtil.file(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
