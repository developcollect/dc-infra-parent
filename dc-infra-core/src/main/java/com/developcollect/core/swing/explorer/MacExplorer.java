package com.developcollect.core.swing.explorer;

import cn.hutool.core.util.RuntimeUtil;

public class MacExplorer implements IExplorer {
    @Override
    public void select(String filePath) {
        RuntimeUtil.exec("open -R " + filePath);

    }

    @Override
    public void open(String filePath) {
        RuntimeUtil.exec("open " + filePath);
    }
}
