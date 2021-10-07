package com.developcollect.extra.javacc;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class CallChainParserTest {

    CallChainParser parser;

    @Before
    public void init() {

        ListableClassPathRepository repository = new ListableClassPathRepository(new String[]{
                "/Volumes/D2/code/java-projects/first/target/classes",
                "/Volumes/D2/.m2/repository/cn/hutool/hutool-all/5.7.13/hutool-all-5.7.13.jar"
        });
        parser = new CallChainParser(repository);

        parser.addFilter(callInfo -> {
            CallInfo.Call caller = callInfo.getCaller();
            MethodInfo methodInfo = caller.getMethodInfo();
            String callerClassName = methodInfo.getClassName();
            return !callerClassName.startsWith("java");
        });

        parser.setSubClassScanner((repo, superClass) -> {
            if (superClass.getClassName().startsWith("org.example.")) {
                return repo.getSubClassList(superClass);
            }
            return Collections.emptyList();
        });
    }


    @Test
    public void test22() throws IOException, NoSuchMethodException {


        CallInfo en2 = parser.parse("org.example.TestEntry", "en2", CcSupport.convertType(new Class[]{String.class, int.class}));

        CcSupport.printCallInfo(en2);

    }

    @Test
    public void testCycle() throws NoSuchMethodException, IOException {

        CallInfo en2 = parser.parse("org.example.TestEntry", "en3");

        CcSupport.printCallInfo(en2);
    }


    @Test
    public void testff() {
        Stream.generate(System::currentTimeMillis)
                .limit(100)
                .filter(l -> (l % 2) == 1)
                .forEach(System.out::println);
    }

    @Test
    public void testtt() {
        TreeNode root = new TreeNode(1);
        root.children = new LinkedList<>();
        for (int i = 2; i < 5; i++) {
            TreeNode c = new TreeNode(i);
            c.children = new LinkedList<>();
            root.children.add(c);
            for (int j = 0; j < 3; j++) {
                c.children.add(new TreeNode(i * 10 + j));
            }
        }

        ArrayList<Integer> integers = printFromTopToBottom(root);

        System.out.println(integers);
    }


    public ArrayList<Integer> printFromTopToBottom(TreeNode root) {
        ArrayList<Integer> lists = new ArrayList<>();

        if (root == null) {
            return lists;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode tree = queue.poll();
            if (tree.children != null) {
                for (TreeNode child : tree.children) {
                    queue.offer(child);
                }
            }

            lists.add(tree.val);
        }
        return lists;
    }


    public static class TreeNode {
        int val;
        List<TreeNode> children;

        public TreeNode(int val) {
            this.val = val;
        }

    }


    @Test
    public void test_f1() {
        CallInfo callInfo = parser.parse("org.example.TestEntry", "f33");
        CcSupport.printCallInfo(callInfo);
    }
}