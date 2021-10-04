package com.developcollect.extra.maven;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.XmlUtil;
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


    public static InvocationResult mvn(String pomPath, String[] cmd) {
        return mvn(pomPath, cmd, null, null);
    }

    public static InvocationResult mvn(String pomPath, String[] cmd, InvocationOutputHandler outputAndErrorHandler) {
        return mvn(pomPath, cmd, outputAndErrorHandler, outputAndErrorHandler);
    }

    public static InvocationResult mvn(String pomPath, String[] cmd, InvocationOutputHandler outputHandler, InvocationOutputHandler errorHandler)  {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pomPath));
        request.setGoals(Arrays.asList(cmd));
        request.addShellEnvironment("skip.test", "true");
        Invoker invoker = new DefaultInvoker();
        try {
            if (outputHandler != null) {
                invoker.setOutputHandler(outputHandler);
            }
            if (errorHandler != null) {
                invoker.setErrorHandler(errorHandler);
            }
            InvocationResult invocationResult = invoker.execute(request);
            return invocationResult;
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
