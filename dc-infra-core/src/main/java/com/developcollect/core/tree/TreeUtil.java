package com.developcollect.core.tree;

import cn.hutool.core.lang.Assert;
import com.developcollect.core.utils.CollectionUtil;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeUtil {

    /**
     * 把实体list使用指定的转换器转换成另一个类型的树
     *
     * @param nodes
     * @param converter
     * @param comparator
     * @return TN 树的根节点
     * @throws IllegalArgumentException 当找到多个根节点时抛出该异常
     * @author Zhu Kaixiao
     * @date 2020/12/8 13:43
     */
    public static <TN extends IMasterNode<TN>, ID extends Serializable, E extends IIdTree<E, ID>> TN convertToTreeAndSort(List<E> nodes, Function<E, TN> converter, Comparator<TN> comparator) {
        if (CollectionUtil.isEmpty(nodes)) {
            return null;
        }
        Assert.notNull(converter);

        TN root = null;
        Map<ID, List<TN>> childrenMap = new HashMap<>(nodes.size());

        for (E entity : nodes) {
            ID id = entity.getId();
            ID parentId = entity.getParentId();

            TN treeNode = converter.apply(entity);
            treeNode.setChildren(childrenMap.computeIfAbsent(id, l -> new ArrayList<>()));
            if (parentId == null) {
                if (root != null) {
                    throw new IllegalArgumentException("发现多个根节点");
                }
                root = treeNode;
            } else {
                List<TN> vos = childrenMap.computeIfAbsent(parentId, l -> new ArrayList<>());
                vos.add(treeNode);
            }
        }
        if (comparator != null) {
            for (Map.Entry<ID, List<TN>> entry : childrenMap.entrySet()) {
                entry.getValue().sort(comparator);
            }
        }
        return root;
    }


    /**
     * 转换成树
     *
     * @param nodes
     * @param converter
     * @return java.util.List<VO>
     * @author Zhu Kaixiao
     * @date 2020/12/8 11:48
     */
    public static <TN extends IMasterNode<TN>, ID extends Serializable, E extends IIdTree<E, ID>> List<TN> convertToTreesAndSort(List<E> nodes, Function<E, TN> converter, Comparator<TN> comparator) {
        if (CollectionUtil.isEmpty(nodes)) {
            return Collections.emptyList();
        }
        Assert.notNull(converter);

        List<TN> result = new ArrayList<>();
        Map<ID, List<TN>> childrenMap = new HashMap<>(nodes.size());

        for (E entity : nodes) {
            ID id = entity.getId();
            ID parentId = entity.getParentId();

            TN tn = converter.apply(entity);
            tn.setChildren(childrenMap.computeIfAbsent(id, l -> new ArrayList<>()));
            if (parentId == null) {
                result.add(tn);
            } else {
                List<TN> vos = childrenMap.computeIfAbsent(parentId, l -> new ArrayList<>());
                vos.add(tn);
            }
        }
        if (comparator != null) {
            result.sort(comparator);
        }
        return result;
    }


    /**
     * 把一颗树转换成另一颗树
     * 例如： 把实体树转换成VO树
     *
     * @param root      树根节点
     * @param converter 转换器
     * @return TN2
     * @author Zhu Kaixiao
     * @date 2020/12/8 12:43
     */
    public static <TN1 extends IMasterNode<TN1>, TN2 extends IMasterNode<TN2>> TN2 convert(TN1 root, Function<TN1, TN2> converter) {
        Assert.notNull(root);
        Assert.notNull(converter);

        TN2 treeNode = converter.apply(root);
        if (CollectionUtil.isNotEmpty(root.getChildren())) {
            List<TN2> children = root.getChildren()
                    .stream()
                    .map(child -> convert(child, converter))
                    .collect(Collectors.toList());
            treeNode.setChildren(children);
        } else {
            treeNode.setChildren(Collections.emptyList());
        }
        return treeNode;
    }


    /**
     * 把文件路径集合解析成一颗树
     * 注意：文件路径分割符固定为/, 暂不支持\,\\
     * 文件路径必须为绝对路径，以/开头，不支持相对路径
     *
     * @param paths 文件路径集合
     * @return 路径树
     */
    public static PathTree resolveFilePaths(Collection<String> paths) {
        List<String> tmp = paths.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        Map<String, PathTree> fileNodeMap = new LinkedHashMap<>();

        PathTree root = new PathTree();
        root.setPayload("/");
        root.setChildren(new LinkedList<>());
        fileNodeMap.put(root.getPayload(), root);


        for (String s : tmp) {
            String[] split = s.split("/");
            split[0] = "/";
            for (int i = 1; i < split.length; i++) {
                PathTree parent = fileNodeMap.get(split[i - 1]);
                PathTree self = fileNodeMap.get(split[i]);
                if (self == null) {
                    self = new PathTree();
                    self.setPayload(split[i]);
                    self.setChildren(new LinkedList<>());
                    self.setParent(parent);
                    parent.getChildren().add(self);
                    fileNodeMap.put(self.getPayload(), self);
                }
            }
        }

        return root;
    }
}
