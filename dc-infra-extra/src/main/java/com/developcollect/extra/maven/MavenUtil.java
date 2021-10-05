package com.developcollect.extra.maven;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.XmlUtil;
import com.developcollect.core.utils.ArrayUtil;
import com.developcollect.core.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Slf4j
public class MavenUtil {

    private static final String POM_FILENAME = "pom.xml";

    static {
        findMavenHome();
    }

    /**
     * 分析项目模块结构
     *
     * @param projectDir 项目顶级目录
     * @return 项目模块结构，无法解析时返回null
     */
    public static ProjectStructure analysisProject(String projectDir) {
        File pomFile = locatePom(new File(projectDir));
        if (pomFile == null) {
            throw new IllegalArgumentException("无法定位pom文件：" + projectDir);
        }

        return analysisPom(null, pomFile);
    }

    private static ProjectStructure analysisPom(ProjectStructure parent, File pomFile) {
        ProjectStructure ps = doAnalysisPom(parent, pomFile);

        File[] ls = FileUtil.ls(ps.getProjectPath());
        List<ProjectStructure> collect = Arrays.stream(ls)
                .map(MavenUtil::locatePom)
                .filter(Objects::nonNull)
                .map(pf -> analysisPom(ps, pf))
                .collect(Collectors.toList());

        ps.setModules(collect);

        return ps;
    }


    /**
     * 定位POM文件
     *
     * @param projectDir 项目文件夹
     * @return pom文件，如果不存在返回null
     */
    private static File locatePom(File projectDir) {
        if (!FileUtil.isDirectory(projectDir)) {
            return null;
        }
        File pomFile = FileUtil.file(projectDir, POM_FILENAME);
        if (!pomFile.exists()) {
            return null;
        }

        return pomFile;
    }

    private static ProjectStructure doAnalysisPom(ProjectStructure parent, File pomFile) {
        ProjectStructure ps = new ProjectStructure();
        Document document = XmlUtil.readXML(pomFile);
        NodeList projectNl = document.getElementsByTagName("project");
        if (projectNl.getLength() != 1) {
            throw new IllegalArgumentException("无效的pom: " + pomFile.getAbsolutePath());
        }
        Node projectNode = projectNl.item(0);
        NodeList projectChildNodes = projectNode.getChildNodes();
        for (int i = 0; i < projectChildNodes.getLength(); i++) {
            Node item = projectChildNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = item.getNodeName();
                if ("groupId".equals(nodeName)) {
                    ps.setGroupId(item.getTextContent());
                } else if ("artifactId".equals(nodeName)) {
                    ps.setArtifactId(item.getTextContent());
                } else if ("version".equals(nodeName)) {
                    ps.setVersion(item.getTextContent());
                } else if ("packaging".equals(nodeName)) {
                    ps.setPackaging(item.getTextContent().toLowerCase());
                }
            }
        }

        // 如果pom文件中没有指定Packaging，那么设置默认值
        if (ps.getPackaging() == null) {
            ps.setPackaging("jar");
        }
        if (ps.getGroupId() == null && parent != null) {
            ps.setGroupId(parent.getGroupId());
        }
        if (ps.getVersion() == null && parent != null) {
            ps.setVersion(parent.getVersion());
        }

        ps.setParent(parent);
        ps.setProjectPath(FileUtil.getCanonicalPath(pomFile.getParentFile()));
        ps.setPomPath(FileUtil.getCanonicalPath(pomFile));
        return ps;
    }


    public static InvocationResult mvn(String pomPath, String... cmd) {
        return mvn(pomPath, cmd, null);
    }


    public static InvocationResult mvnWithThrow(String pomPath, String... cmd) {
        StringBuilder sb = new StringBuilder();
        InvocationResult result = mvn(pomPath, cmd, line -> {
            if (line.startsWith("[ERROR]")) {
                sb.append("\n").append(line);
            }
        });
        if (result.getExecutionException() != null) {
            throw new UtilException(result.getExecutionException());
        }
        if (result.getExitCode() != 0) {
            throw new UtilException("执行mvn命令【mvn " + ArrayUtil.join(cmd, " ") + "】异常：" + sb);
        }
        return result;
    }


    public static InvocationResult mvn(String pomPath, String[] cmd, InvocationOutputHandler outputHandler) {
        return mvn(pomPath, cmd, request -> {
        }, invoker -> {
            if (outputHandler != null) {
                invoker.setOutputHandler(outputHandler);
            }
        });

    }


    public static InvocationResult mvn(String pomPath, String[] cmd, Consumer<InvocationRequest> requestHook, Consumer<Invoker> invokerHook) {
        InvocationRequest request = new DefaultInvocationRequest();
        // 设置pom文件
        request.setPomFile(new File(pomPath));
        // 设置goals
        request.setGoals(Arrays.asList(cmd));
        // 设置为非交互模式
        request.setBatchMode(true);
        // 设置跳过单元测试
        request.setMavenOpts("-Dmaven.test.skip=true");
        // 安静模式，不输出IFNO，但会输出ERROR
//        request.setQuiet(true);

        requestHook.accept(request);

        Invoker invoker = new DefaultInvoker();
        invokerHook.accept(invoker);
        try {
            return invoker.execute(request);
        } catch (MavenInvocationException e) {
            throw new UtilException(e);
        }
    }


    public static String findMavenHome() {
        String mavenHome = System.getProperty("maven.home");
        if (mavenHome != null) {
            return mavenHome;
        }

        try {
            List<String> strings = RuntimeUtil.execForLines("mvn", "-v");
            for (String string : strings) {
                if (string.startsWith("Maven home:")) {
                    mavenHome = string.substring(11).trim();
                    System.setProperty("maven.home", mavenHome);
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }
}
