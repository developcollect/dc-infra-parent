package com.developcollect.extra.maven;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.XmlUtil;
import com.developcollect.core.tree.TreeUtil;
import com.developcollect.core.utils.ArrayUtil;
import com.developcollect.core.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

        // pom类型项目，继续扫描子包
        if ("pom".equals(ps.getPackaging())) {
            File[] ls = FileUtil.ls(ps.getProjectPath());
            List<ProjectStructure> collect = Arrays.stream(ls)
                    .filter(File::isDirectory)
                    .filter(f -> ps.getModules().contains(f.getName()))
                    .map(MavenUtil::locatePom)
                    .filter(Objects::nonNull)
                    .map(pf -> analysisPom(ps, pf))
                    .collect(Collectors.toList());

            ps.setModuleProjectStructures(collect);
        }

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
                    ps.setGroupId(item.getTextContent().trim());
                } else if ("artifactId".equals(nodeName)) {
                    ps.setArtifactId(item.getTextContent().trim());
                } else if ("version".equals(nodeName)) {
                    ps.setVersion(item.getTextContent().trim());
                } else if ("packaging".equals(nodeName)) {
                    ps.setPackaging(item.getTextContent().toLowerCase().trim());
                } else if ("modules".equals(nodeName)) {
                    NodeList moduleNodeList = item.getChildNodes();
                    List<String> modules = new ArrayList<>();
                    for (int m = 0; m < moduleNodeList.getLength(); m++) {
                        Node moduleNode = moduleNodeList.item(m);
                        if ("module".equals(moduleNode.getNodeName())) {
                            modules.add(moduleNode.getTextContent().trim());
                        }
                    }
                    ps.setModules(modules);
                }
            }
        }

        // 如果pom文件中没有指定Packaging，那么设置默认值
        if (ps.getPackaging() == null) {
            ps.setPackaging("jar");
        } else if ("pom".equals(ps.getPackaging()) && ps.getModules() == null) {
            ps.setModules(Collections.emptyList());
            ps.setModuleProjectStructures(Collections.emptyList());
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
        return mvn(pomPath, Arrays.asList(cmd), null);
    }


    public static InvocationResult mvnWithThrow(String pomPath, String... cmd) {
        StringBuilder sb = new StringBuilder();
        InvocationResult result = mvn(pomPath, Arrays.asList(cmd), line -> {
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


    public static InvocationResult mvn(String pomPath, List<String> cmd, InvocationOutputHandler outputHandler) {
        return mvn(pomPath, request -> {
            // 设置goals
            request.setGoals(cmd);
        }, invoker -> {
            if (outputHandler != null) {
                invoker.setOutputHandler(outputHandler);
            }
        });

    }


    public static InvocationResult mvn(String pomPath, Consumer<InvocationRequest> requestHook, Consumer<Invoker> invokerHook) {
        InvocationRequest request = new DefaultInvocationRequest();
        // 设置pom文件
        request.setPomFile(new File(pomPath));

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


    public static String[] collectClassPaths(ProjectStructure projectStructure) {
        List<ProjectStructure> projectStructures = TreeUtil.flat(projectStructure, TreeUtil::preOrder, ps -> !"pom".equals(ps.getPackaging()));
        String[] classPaths = projectStructures.stream()
                .map(ps -> ps.getProjectPath() + "/target/classes")
                .toArray(String[]::new);
        return classPaths;
    }


    /**
     * 传入项目根目录，然后项目编译时的依赖classpath
     * 只支持maven项目
     *
     * @param projectRootPath
     * @return
     * @author Zhu Kaixiao
     * @date 2020/10/12 14:17
     */
    public static List<String> getDependClassPaths(String projectRootPath) {
        StringBuilder sb = new StringBuilder();
        StringBuilder errorSb = new StringBuilder();
        InvocationResult invocationResult = mvn(projectRootPath + File.separator + "pom.xml", request -> {
            request.setGoals(Collections.singletonList("compile"));
            request.setDebug(true);
        }, invoker -> {
            invoker.setOutputHandler(line -> {
                // 先把输出全部存到sb中，后续在提取出需要的数据
                if (line.startsWith("[ERROR]")) {
                    errorSb.append(line);
                } else if (line.startsWith("[DEBUG]")) {
                    sb.append(line);
                }
            });
        });

        if (invocationResult.getExecutionException() != null) {
            throw new UtilException(invocationResult.getExecutionException());
        }
        if (invocationResult.getExitCode() != 0) {
            throw new UtilException("执行mvn命令【mvn compile】异常：" + errorSb);
        }
        String s = sb.toString();

        Pattern compile = PatternPool.get("\\[DEBUG]   \\(f\\) compilePath = \\[(.+?)]");
        Matcher matcher = compile.matcher(s);

        Set<String> strSet = new HashSet<>();
        while (matcher.find()) {
            String[] split = matcher.group(1).split(", ");
            strSet.addAll(Arrays.asList(split));
        }
        return new ArrayList<>(strSet);
    }
}
