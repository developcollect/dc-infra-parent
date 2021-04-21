package com.developcollect.core.swing.explorer;


public interface IExplorer {

    /**
     * 打开文件管理器，并选中指定文件
     *
     * @param filePath
     */
    void select(String filePath);


    void open(String filePath);
}
