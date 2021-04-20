package com.developcollect.webdriver;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashSet;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * @author zak
 * @since 1.0.0
 */
@Slf4j
public class WebDriverUtil {

    private static WebDriver driver;
    private static String ORIGIN_WINDOW_HANDLE;

    static {
        try {
            //实例化ChromeDriver对象
            //创建无Chrome无头参数
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");

            // 关掉日志
            System.setProperty("webdriver.chrome.silentOutput", "true");
            java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

            //创建Drive实c例
            driver = new ChromeDriver(options);

            ORIGIN_WINDOW_HANDLE = driver.getWindowHandle();
            // 加上钩子， 程序退出时也退出浏览器
            Runtime.getRuntime().addShutdownHook(new Thread(WebDriverUtil::quit));

        } catch (Throwable t) {
            log.error("打开ChromeDriver失败", t);
            if (driver != null) {
                driver.quit();
            }
        }

    }

    /**
     * 网页截图
     *
     * @param url      链接
     * @param by       元素定位
     * @param target   返回值格式(file, bytes, base64)
     * @param consumer 自定义处理  比如设置浏览器宽度，高度等等
     * @return X 返回值
     * @author zak
     * @date 2020/5/27 15:04
     */
    private static <X> X screenshot(final String url, final By by, OutputType<X> target, final Consumer<WebDriver> consumer) {
        synchronized (WebDriverUtil.class) {
            WebDriver driver = get(url);
            // 切换到新的标签
            final LinkedHashSet<String> windowHandles = (LinkedHashSet) driver.getWindowHandles();
            driver.switchTo().window(windowHandles.toArray(new String[windowHandles.size()])[1]);

            // 调用自定义处理
            consumer.accept(driver);

            // 截图
            final TakesScreenshot ts = by == null ? (TakesScreenshot) driver : driver.findElement(by);
            final X x = takeScreenshot(ts, target);

            // 关闭当前标签
            driver.close();
            // 切换回原始的标签
            driver.switchTo().window(ORIGIN_WINDOW_HANDLE);
            return x;
        }
    }

    /**
     * 网页截图
     *
     * @param url      链接
     * @param target   返回值格式(file, bytes, base64)
     * @param consumer 自定义处理  比如设置浏览器宽度，高度等等
     * @return X
     * @author zak
     * @date 2020/5/27 15:06
     */
    private static <X> X screenshot(final String url, final OutputType<X> target, final Consumer<WebDriver> consumer) {
        return screenshot(url, null, target, consumer);
    }

    /**
     * 网页截图
     *
     * @param url    链接
     * @param by     元素定位
     * @param target 返回值格式(file, bytes, base64)
     * @return X
     * @author zak
     * @date 2020/5/27 15:06
     */
    public static <X> X screenshot(final String url, final By by, final OutputType<X> target) {
        return screenshot(url, by, target, WebDriverUtil::screen1920x1080);
    }

    /**
     * 网页截图， 截取整个网页完整的图
     *
     * @param url    链接
     * @param target 返回值格式(file, bytes, base64)
     * @return X
     * @author zak
     * @date 2020/5/27 15:06
     */
    public static <X> X screenshotFull(final String url, final OutputType<X> target) {
        return screenshot(url, target, WebDriverUtil::scrollMax);
    }

    /**
     * 网页截图
     *
     * @param url    链接
     * @param target 返回值格式(file, bytes, base64)
     * @return X
     * @author zak
     * @date 2020/5/27 15:08
     */
    public static <X> X screenshot(final String url, final OutputType<X> target) {
        return screenshot(url, target, WebDriverUtil::screen1920x1080);
    }


    private static <X> X takeScreenshot(final TakesScreenshot takesScreenshot, final OutputType<X> target) {
        final OutputType ot = target == OutputType.FILE ? OutputType.BYTES : target;
        final Object obj = takesScreenshot.getScreenshotAs(ot);

        return target == OutputType.FILE
                ? (X) FileUtil.writeFromStream(new ByteArrayInputStream((byte[]) obj), FileUtil.createTempFile("webdriver", ".jpg", FileUtil.getTmpDir(), true))
                : (X) obj;
    }

    private static WebDriver get(String url) {
        // 默认从新标签打开页面， 这样关掉新页面的话还有一个原始标签在， 浏览器就不会退出
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.open('" + url + "')");
        return driver;
    }


    /**
     * 退出浏览器
     * 这个没调用会导致浏览器没退出，从而导致内存无法释放
     *
     * @author zak
     * @date 2020/5/27 15:17
     */
    public static void quit() {
        log.debug("退出WebDriver");
        driver.quit();
    }


    private static void scrollMax(WebDriver driver) {
        //声明一个js执行器
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long scrollHeight = (long) js.executeScript("return document.body.parentNode.scrollHeight");
        driver.manage().window().setSize(new Dimension(1920, (int) scrollHeight));
    }

    private static void screen1920x1080(WebDriver driver) {
        driver.manage().window().setSize(new Dimension(1920, 1080));
    }
}
