package com.developcollect.core.swing.explorer;

import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;

import java.io.File;

/**
 * 要打开当前目录，Win下可以 「explorer .」，Gnome 可以「nautilus .」
 *
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/5 11:08
 */
public class ExplorerUtil {

    private static IExplorer explorer;

    static {
        OsInfo osInfo = SystemUtil.getOsInfo();
        if (osInfo.isWindows()) {
            explorer = new WindowsExplorer();
        } else if (osInfo.isMac() || osInfo.isMacOsX()) {
            explorer = new MacExplorer();
        }
    }


    public static void select(File file) {
        explorer.select(file.getAbsolutePath());
    }

    public static void select(String filePath) {
        explorer.select(filePath);
    }

    public static void open(File file) {
        explorer.open(file.getAbsolutePath());
    }

}
