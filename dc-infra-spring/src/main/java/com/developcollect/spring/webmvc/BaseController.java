package com.developcollect.spring.webmvc;


import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import com.developcollect.core.utils.LambdaUtil;
import com.developcollect.spring.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Consumer;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/9/26 11:59
 */
public class BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    protected boolean provideDownload(@NotNull final Resource resource) {
        return provideDownload(resource, WebUtil.getResponse());
    }

    /**
     * 提供文件下载
     * <p>
     * 如果想通过File对象提高文件下载  使用{@link org.springframework.core.io.FileSystemResource}
     * 如
     * FileSystemResource fileSystemResource = new FileSystemResource(new File("f://a.txt"));
     * provideDownload(fileSystemResource, response);
     * <p>
     * <p>
     * 如果需要给文件指定别名  使用{@link com.developcollect.spring.lang.RenameResource}
     * 如
     * RenameResource renameResource = new RenameResource(new File("f://a.txt"), "alias.txt");
     * provideDownload(renameResource, response);
     * <p>
     * <p>
     * 如果需要以输入流提供下载  使用{@link com.developcollect.spring.lang.RenameResource}
     * 如
     * InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("api-import-template.xlsx");
     * RenameResource renameResource new RenameResource(new InputStreamResource(inputStream), "api-import-template.xlsx");
     * provideDownload(renameResource, response);
     *
     * @param resource 资源文件
     * @param response response
     * @return boolean 是否提供下载成功
     * @author Zhu Kaixiao
     * @date 2019/9/27 11:21
     * @see BaseController#provideDownload(InputStream, String, HttpServletResponse)
     **/
    protected boolean provideDownload(@NotNull final Resource resource, @NotNull final HttpServletResponse response) {
        Assert.notNull(resource, "The resource must not be null");
        String filename = resourceFilename(resource);
        // 实现文件下载
        try (
                InputStream in = resource.getInputStream()
        ) {
            return provideDownload(in, filename, response);
        } catch (Exception e) {
            log.warn("Provide download failed! [{}]", resource.getDescription(), e);
            return false;
        }
    }



    /**
     * 以流类型提供文件下载
     *
     * @param in       输入流
     * @param filename 文件名
     * @param response response
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/9/28 10:21
     **/
    private boolean provideDownload(@NotNull final InputStream in, @NotNull final String filename,
                                    @NotNull final HttpServletResponse response) {
        Assert.notNull(filename, "The resourceFilename must not be null");
        return provideDownload(response, filename, out -> IoUtil.copy(in, out));
    }

    public boolean provideDownload(@NotNull final HttpServletResponse response, String filename, Consumer<OutputStream> outputStreamConsumer) {
        Assert.notNull(response, "The response must not be null");
        Assert.notNull(outputStreamConsumer, "The outputStreamConsumer must not be null");
        // 实现文件下载
        try {
            out(response, outputStreamConsumer, resp -> {
                // 配置文件下载
                resp.setHeader("Content-Type", "application/octet-stream");
                resp.setContentType("application/octet-stream; charset=utf-8");
                // 下载文件能正常显示中文
                resp.setHeader("Content-Disposition", "attachment;filename="
                        + LambdaUtil.raise(() -> encodeFilenameForDownload(WebUtil.getRequest(), filename)));
            });
            log.debug("Provide download successfully! [{}]", filename);
            return true;
        } catch (Exception e) {
            log.warn("Provide download failed! [{}]", filename, e);
            return false;
        }
    }


    private String resourceFilename(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            filename = "resource";
        }
        return filename;
    }


    public void out(final Resource resource, final Consumer<HttpServletResponse> consumer) {
        Assert.notNull(resource, "The resource must not be null");
        try (InputStream in = resource.getInputStream()) {
            out(in, WebUtil.getResponse(), consumer);
        } catch (IOException e) {
            throw new IORuntimeException(e.getMessage(), e);
        }
    }


    public void out(@NotNull final InputStream in,
                    @NotNull final HttpServletResponse response,
                    Consumer<HttpServletResponse> consumer) throws IOException {
        Assert.notNull(in, "The inputStream must not be null");
        out(response, out -> IoUtil.copy(in, out), consumer);
    }


    public void out(@NotNull final HttpServletResponse response,
                    Consumer<OutputStream> outputStreamConsumer,
                    Consumer<HttpServletResponse> responseConsumer) throws IOException {

        Assert.notNull(response, "The response must not be null");
        Assert.notNull(outputStreamConsumer, "The outputStreamConsumer must not be null");
        // 直接写出流
        try (
                OutputStream out = response.getOutputStream()
        ) {
            if (responseConsumer != null) {
                responseConsumer.accept(response);
            }
            outputStreamConsumer.accept(out);
            out.flush();
        } catch (IOException e) {
            log.warn("写出流失败");
            throw e;
        }
    }


    /**
     * 下载文件名重新编码
     *
     * @param request 请求对象
     * @param filename 文件名
     * @return 编码后的文件名
     */
    private static String encodeFilenameForDownload(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(filename.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }
}
