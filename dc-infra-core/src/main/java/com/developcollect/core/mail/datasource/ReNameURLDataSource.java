package com.developcollect.core.mail.datasource;

import javax.activation.URLDataSource;
import java.net.URL;

/**
 * @author zak
 * @version 1.0
 * @date 2020/5/11 19:01
 */
public class ReNameURLDataSource extends URLDataSource {

    private String filename;

    public ReNameURLDataSource(String filename, URL url) {
        super(url);
        this.filename = filename;
    }

    @Override
    public String getName() {
        return filename;
    }
}
