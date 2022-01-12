package com.developcollect.extra.maven;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.system.SystemUtil;
import com.developcollect.core.tree.TreeUtil;
import com.developcollect.core.utils.CollUtil;
import com.developcollect.core.utils.FileUtil;
import com.developcollect.core.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
public class MavenUtil {
    private static final Pattern TITLE_PATTERN = Pattern.compile("-*?< (.+?):(.+?) >-*?");

    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile(" {3}(.*?):(.*?):(.*?):(.*?):(.*)");
    private static final Pattern DEPENDENCY_TREE_ITEM_PATTERN = Pattern.compile("([+\\-\\\\| ]+)(.*?):(.*?):(.*?):(.*?):(.*)");

    private static final String POM_FILENAME = "pom.xml";

    static {
        String mavenHome = findMavenHome();
        if (mavenHome != null) {
            System.setProperty("maven.home", mavenHome);
        }
    }


    /**
     * 获取项目的依赖列表
     * http://maven.apache.org/plugins/maven-dependency-plugin/list-mojo.html
     *
     * @param pomPath pom文件路径或项目路径
     * @return 依赖列表
     */
    public static List<Module> getDependencyList(String pomPath) {
        List<Module> modules = new ArrayList<>();
        AtomicReference<Module> currModule = new AtomicReference<>();

        mvn(pomPath, Collections.singletonList("dependency:list"), s -> {
            if (s.startsWith("[INFO] ")) {
                String line = s.substring(7);

                // dependency 解析
                Matcher dependencyMatcher = DEPENDENCY_PATTERN.matcher(line);
                if (dependencyMatcher.find()) {
                    List<Dependency> dependencies = currModule.get().getDependencies();
                    Dependency dependency = new Dependency();
                    dependency.setGroupId(dependencyMatcher.group(1));
                    dependency.setArtifactId(dependencyMatcher.group(2));
                    dependency.setType(dependencyMatcher.group(3));
                    dependency.setVersion(dependencyMatcher.group(4));
                    dependency.setScope(dependencyMatcher.group(5));

                    dependencies.add(dependency);
                    return;
                }

                // title 解析
                Matcher titleMatcher = TITLE_PATTERN.matcher(line);
                if (titleMatcher.find()) {
                    Artifact artifact = new Artifact();
                    artifact.setGroupId(titleMatcher.group(1));
                    artifact.setArtifactId(titleMatcher.group(2));

                    Module module = new Module();
                    module.setArtifact(artifact);
                    module.setDependencies(new ArrayList<>());

                    currModule.set(module);
                    modules.add(module);
                    return;
                }

                // version
                //  Building app-front web 1.0.1-SNAPSHOT
                //  Building bs-ncc-api 1.1.5-SNAPSHOT                                [2/10]
                if (line.startsWith("Building ")) {
                    String versionLine = line.substring(9);
                    versionLine = versionLine.replaceFirst(" *?\\[\\d+?/\\d+?]$", "");
                    int idx = versionLine.lastIndexOf(" ");
                    String version = versionLine.substring(idx + 1);
                    Artifact artifact = currModule.get().getArtifact();
                    if (artifact == null) {
                        throw new RuntimeException("artifact is null");
                    }
                    artifact.setVersion(version);
                    return;
                }
            }
        });

        return modules;
    }

    /**
     * “+-”      后面的jar包是顶层依赖包，在pom中进行了声明；
     *
     * “|  \-”    后面的jar包是引用包，未在pom中声明；只要声明顶层包，其对应的引用包会自动去仓库中下载；
     *
     * “\-”，    不管在pom文件中最后一个依赖引用是哪个，其前缀都是“\-”，即 ”\-” 仅表后最后一个依赖引用；
     */
    public static List<Module> getDependencyTree(String pomPath) {
        List<Module> modules = new ArrayList<>();
        AtomicReference<Module> currModule = new AtomicReference<>();
        AtomicInteger prevTreeDeep = new AtomicInteger();
        LinkedList<DependencyTree> dependencyTreeStack = new LinkedList<>();

        mvn(pomPath, Collections.singletonList("dependency:tree"), s -> {
            System.out.println(s);
            if (s.startsWith("[INFO] ")) {
                String line = s.substring(7);

                // dependency 解析
                Matcher dependencyMatcher = DEPENDENCY_TREE_ITEM_PATTERN.matcher(line);
                if (dependencyMatcher.find()) {
                    Dependency dependency = new Dependency();
                    String treeHead = dependencyMatcher.group(1);
                    int deep = treeHead.length() / 3;
                    dependency.setGroupId(dependencyMatcher.group(2));
                    dependency.setArtifactId(dependencyMatcher.group(3));
                    dependency.setType(dependencyMatcher.group(4));
                    dependency.setVersion(dependencyMatcher.group(5));
                    dependency.setScope(dependencyMatcher.group(6));

                    DependencyTree tree = new DependencyTree();
                    tree.setDependency(dependency);
                    tree.setChildren(new ArrayList<>());

                    if (treeHead.startsWith("+")) {
                        currModule.get().getDependencyTrees().add(tree);
                        dependencyTreeStack.pollFirst();
                        dependencyTreeStack.push(tree);
                    } else {
                        if (treeHead.endsWith("\\-")) {
                            dependencyTreeStack.pop();
                        } else {
                            DependencyTree prevNode = dependencyTreeStack.getFirst();
                            prevNode.getChildren().add(tree);
                            tree.setParent(prevNode);
                        }
                    }
                    prevTreeDeep.set(deep);
                    return;
                }

                // title 解析
                Matcher titleMatcher = TITLE_PATTERN.matcher(line);
                if (titleMatcher.find()) {
                    Artifact artifact = new Artifact();
                    artifact.setGroupId(titleMatcher.group(1));
                    artifact.setArtifactId(titleMatcher.group(2));

                    Module module = new Module();
                    module.setArtifact(artifact);
                    module.setDependencies(new ArrayList<>());

                    currModule.set(module);
                    modules.add(module);
                    return;
                }

                // version
                //  Building app-front web 1.0.1-SNAPSHOT
                //  Building bs-ncc-api 1.1.5-SNAPSHOT                                [2/10]
                if (line.startsWith("Building ")) {
                    String versionLine = line.substring(9);
                    versionLine = versionLine.replaceFirst(" *?\\[\\d+?/\\d+?]$", "");
                    int idx = versionLine.lastIndexOf(" ");
                    String version = versionLine.substring(idx + 1);
                    Artifact artifact = currModule.get().getArtifact();
                    if (artifact == null) {
                        throw new RuntimeException("artifact is null");
                    }
                    artifact.setVersion(version);
                    return;
                }
            }
        });

        return modules;
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


    public static InvocationResult mvnWithThrow(String pomPath, String... cmd) {
        return mvnWithThrow(pomPath, request -> request.setGoals(Arrays.asList(cmd)), invoker -> {
        });
    }


    public static InvocationResult mvnWithThrow(String pomPath, Consumer<InvocationRequest> requestHook, Consumer<Invoker> invokerHook) {
        StringBuilder errorInfoSb = new StringBuilder();
        AtomicReference<InvocationRequest> request = new AtomicReference<>();
        InvocationResult result = mvn(pomPath, req -> {
            request.set(req);
            if (requestHook != null) {
                requestHook.accept(req);
            }
        }, invoker -> {
            InvocationOutputHandler errorInfoInvocationOutputHandler = line -> {
                if (line.startsWith("[ERROR]")) {
                    errorInfoSb.append("\n").append(line);
                }
            };

            if (invokerHook != null) {
                invokerHook.accept(invoker);

                InvocationOutputHandler defaultOutputHandler = (InvocationOutputHandler) ReflectUtil.getFieldValue(invoker.getClass(), "DEFAULT_OUTPUT_HANDLER");
                InvocationOutputHandler outputHandler = (InvocationOutputHandler) ReflectUtil.getFieldValue(invoker, "outputHandler");
                if (outputHandler == defaultOutputHandler) {
                    invoker.setOutputHandler(errorInfoInvocationOutputHandler);
                } else {
                    invoker.setOutputHandler(line -> {
                        errorInfoInvocationOutputHandler.consumeLine(line);
                        outputHandler.consumeLine(line);
                    });
                }
            } else {
                invoker.setOutputHandler(errorInfoInvocationOutputHandler);
            }
        });

        if (result.getExecutionException() != null) {
            throw new UtilException(result.getExecutionException());
        }
        if (result.getExitCode() != 0) {
            throw new UtilException("执行mvn命令【mvn " + CollUtil.join(request.get().getGoals(), " ") + "】异常：" + errorInfoSb);
        }
        return result;
    }


    public static String findMavenHome() {
        String mavenHome = System.getProperty("maven.home");
        if (mavenHome == null) {
            mavenHome = System.getenv("MAVEN_HOME");
        }
        if (mavenHome == null) {
            mavenHome = System.getenv("maven_home");
        }
        if (mavenHome != null) {
            return mavenHome;
        }

        try {
            List<String> strings;
            if (SystemUtil.getOsInfo().isWindows()) {
                strings = RuntimeUtil.execForLines("cmd /c", "mvn", "-v");
            } else {
                strings = RuntimeUtil.execForLines("mvn", "-v");
            }

            for (String string : strings) {
                if (string.startsWith("Maven home:")) {
                    return string.substring(11).trim();
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }


    public static List<String> collectClassPaths(ProjectStructure projectStructure) {
        List<ProjectStructure> projectStructures = TreeUtil.flat(projectStructure, TreeUtil::preOrder, ps -> !"pom".equals(ps.getPackaging()));
        List<String> classPaths = projectStructures.stream()
                .map(ps -> ps.getProjectPath() + File.separator + "target" + File.separator + "classes")
                .collect(Collectors.toList());
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

        mvnWithThrow(projectRootPath + File.separator + "pom.xml", request -> {
            request.setGoals(Collections.singletonList("compile"));
            request.setDebug(true);
        }, invoker -> {
            invoker.setOutputHandler(line -> {
                // 先把输出全部存到sb中，后续在提取出需要的数据
                sb.append(line).append("\n");
            });
        });


        String s = sb.toString();
        String keyword = "compilePath";
        if (s.contains("classpathElements")) {
            keyword = "classpathElements";
        }

        Pattern compile = PatternPool.get("\\[DEBUG]   \\(f\\) " + keyword + " = \\[(.+?)]");
        Matcher matcher = compile.matcher(s);

        Set<String> strSet = new HashSet<>();
        while (matcher.find()) {
            String[] split = matcher.group(1).split(", ");
            strSet.addAll(Arrays.asList(split));
        }
        return new ArrayList<>(strSet);
    }
}
