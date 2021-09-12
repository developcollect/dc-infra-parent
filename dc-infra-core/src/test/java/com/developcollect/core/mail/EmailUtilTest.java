package com.developcollect.core.mail;

import cn.hutool.extra.mail.MailAccount;
import com.developcollect.core.mail.datasource.ReNameURLDataSource;
import com.developcollect.core.utils.URLUtil;
import org.junit.Test;


import javax.activation.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class EmailUtilTest {

    static {
        EmailUtil.accountSupplier = () -> {
            MailAccount account = new MailAccount();
            account.setHost("smtp.qq.com");
            account.setPort(465);
            account.setFrom("呀哈哈 <321992685@qq.com>");
            account.setUser("321992685@qq.com");
            account.setPass("nkpnonlkhvmacaaa");
            account.setAuth(true);
            account.setSslEnable(true);
            account.setCharset(StandardCharsets.UTF_8);
            return account;
        };
    }

    @Test
    public void test1() {
        EmailUtil.sendText("3617246657@qq.com", "测试发送", "正文正文正文正文正文", new DataSource[] {
                new ReNameURLDataSource("test.jpg", URLUtil.url("http://192.168.0.79/r/jeemarket/2020/07/29/download88.jpg"))
        });
    }

}