package com.developcollect.extra.webdriver;

import cn.hutool.core.io.IORuntimeException;
import com.developcollect.core.utils.FileUtil;
import org.openqa.selenium.WebDriverException;

import java.io.File;

public class FileOutputType implements org.openqa.selenium.OutputType<File> {

    private String saveDir = FileUtil.getTmpDirPath();

    public FileOutputType() {
    }

    public FileOutputType(String saveDir) {
        this.saveDir = saveDir;
    }

    @Override
    public File convertFromBase64Png(String s) {
        return this.save(BYTES.convertFromBase64Png(s));
    }

    @Override
    public File convertFromPngBytes(byte[] bytes) {
        return this.save(bytes);
    }

    private File save(byte[] data) {
        try {
            File tempFile = FileUtil.createTempFile("screenshot", ".png", FileUtil.file(saveDir), true);
            FileUtil.writeBytes(data, tempFile);
            return tempFile;
        } catch (IORuntimeException e) {
            throw new WebDriverException(e.getCause());
        }
    }

    @Override
    public String toString() {
        return "FileOutputType";
    }
}
