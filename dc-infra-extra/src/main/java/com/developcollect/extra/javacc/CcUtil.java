package com.developcollect.extra.javacc;


import cn.hutool.core.exceptions.UtilException;
import com.developcollect.core.tree.TreeUtil;
import com.developcollect.extra.maven.MavenUtil;
import com.developcollect.extra.maven.ProjectStructure;
import org.apache.maven.shared.invoker.InvocationResult;


/**
 * call chain util
 */
public class CcUtil {

    /**
     * 传入一个Maven项目，解析该项目中的类中的方法调用关系
     * @param mavenProjectDir maven项目文件夹
     * @return
     */
    public static Object parseChain(String mavenProjectDir) {
        // 解析maven项目结构
        ProjectStructure projectStructure = MavenUtil.analysisProject(mavenProjectDir);

        // 执行clear、compile命令，定位classes目录
        InvocationResult invocationResult = MavenUtil.mvn(projectStructure.getPomPath(), new String[]{"clean", "compile"}, line -> {});
        if (invocationResult.getExitCode() != 0) {
            throw new UtilException("执行【mvn clear compile】命令失败");
        }


        // 识别出依赖的jar包
        // 定位需要解析的类和方法，执行解析
        // 扫描类

        return null;
    }











}
