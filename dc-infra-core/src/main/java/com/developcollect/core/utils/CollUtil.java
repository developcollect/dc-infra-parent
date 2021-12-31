package com.developcollect.core.utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;


/**
 * 集合工具类
 *
 * @author zak
 * @since 1.0.0
 */
public class CollUtil extends cn.hutool.core.collection.CollUtil {


    /**
     * 去重
     *
     * @param list
     * @param hash
     * @return java.util.List<T>
     */
    public static <T> List<T> distinct(List<T> list, ToIntFunction<T> hash) {
        if (isEmpty(list)) {
            return list;
        }
        Iterator<T> iterator = list.iterator();
        Set<Integer> hashCodeSet = new HashSet<>();
        while (iterator.hasNext()) {
            int hashCode = hash.applyAsInt(iterator.next());
            if (hashCodeSet.contains(hashCode)) {
                iterator.remove();
            } else {
                hashCodeSet.add(hashCode);
            }
        }
        return list;
    }

    public static <T> List<T> distinct(List<T> list) {
        return distinct(list, Objects::hashCode);
    }

    /**
     * 将集合中的元素的位置打乱
     *
     * @param list
     * @return java.util.List<T>
     * @author zak
     * @date 2020/8/24 9:57
     */
    public static <T> List<T> shuffle(List<T> list) {
        Collections.shuffle(list);
        return list;
    }


    /**
     * 交换集合中的两个元素
     *
     * @param list
     * @param i
     * @param j
     * @return java.util.List<T>
     * @author zak
     * @date 2020/8/24 10:04
     */
    public static <T> List<T> swap(List<T> list, int i, int j) {
        Collections.swap(list, i, j);
        return list;
    }

    /**
     * 根据集合中对象的某个方法的返回值是否和给定的值相等, 来判断集合中是否存在符合要求的对象
     *
     * @param collection 集合
     * @param function   方法
     * @param value      值
     * @return boolean
     * @author zak
     * @date 2019/11/20 10:04
     */
    public static <T> boolean contains(final Collection<T> collection, final Function<T, Object> function, final Object value) {
        return contains(collection, ele -> Objects.equals(function.apply(ele), value));
    }


    /**
     * 获取集合中满足指定条件的第一个元素
     *
     * @param collection 集合
     * @param function   转换方法
     * @param value      预计值
     * @deprecated use {@link CollUtil#findFirst(Iterable, Function, Object)}
     */
    @Deprecated
    public static <T> T get(final Collection<T> collection, final Function<T, Object> function, final Object value) {
        return findFirst(collection, function, value);
    }

    /**
     * 获取集合中满足指定条件的第一个元素
     *
     * @param collection 集合
     * @param predicate  条件
     * @return boolean
     * @author zak
     * @date 2020/8/24 9:39
     * @deprecated use {@link CollUtil#findFirst(Iterable, Predicate)}
     */
    @Deprecated
    public static <T> T get(final Collection<T> collection, final Predicate<T> predicate) {
        return findFirst(collection, predicate);
    }

    /**
     * 获取集合中满足指定条件的第一个元素
     *
     * @param collection 集合
     * @param predicate  条件
     * @return 满足指定条件的第一个元素
     */
    public static <T> T findFirst(Iterable<T> collection, Predicate<T> predicate) {
        if (null != collection) {
            for (T t : collection) {
                if (predicate.test(t)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static <T> T findFirst(Iterable<T> collection, final Function<T, Object> function, final Object value) {
        return findFirst(collection, ele -> Objects.equals(function.apply(ele), value));
    }

    /**
     * 获取集合中满足指定条件的所有元素
     * 该方法不修改原始集合
     *
     * @param collection 集合
     * @param predicate  条件
     * @return 满足指定条件的第一个元素
     */
    public static <T> List<T> findAll(Iterable<T> collection, Predicate<T> predicate) {
        if (null != collection) {
            List<T> ret = new ArrayList<>();
            for (T t : collection) {
                if (predicate.test(t)) {
                    ret.add(t);
                }
            }
            return ret;
        }
        return null;
    }

    /**
     * 获取集合中所有经过function转换后值和value相等的元素
     *
     * @param collection 集合
     * @param function   转换方法
     * @param value      预计值
     * @param <T>        元素类型
     * @return 所有经过function转换后值和value相等的元素
     */
    public static <T> List<T> findAll(Iterable<T> collection, final Function<T, Object> function, final Object value) {
        return findAll(collection, ele -> Objects.equals(function.apply(ele), value));
    }


    /**
     * 获取集合中满足指定条件的第一个元素的下标，如果没有满足条件的元素，则返回-1
     *
     * @param coll      集合
     * @param predicate 条件
     * @return 满足指定条件的第一个元素的下标，如果没有满足条件的元素，则返回-1
     */
    public static <T> int firstIndex(Iterable<T> coll, final Predicate<T> predicate) {
        if (coll != null) {
            int i = 0;
            for (T t : coll) {
                if (predicate.test(t)) {
                    return i;
                }
                ++i;
            }
        }

        return -1;
    }

    /**
     * 获取集合中所有经过function转换后值和value相等的元素
     *
     * @param collection 集合
     * @param function   转换方法
     * @param value      预计值
     * @param <T>        元素类型
     * @return 所有经过function转换后值和value相等的元素
     * @deprecated use {@link CollUtil#findAll(Iterable, Function, Object)}
     */
    @Deprecated
    public static <T> List<T> sub(final Collection<T> collection, final Function<T, Object> function, final Object value) {
        return sub(collection, ele -> Objects.equals(function.apply(ele), value));
    }

    /**
     * 获取集合中满足指定条件的所有元素
     *
     * @param collection 集合
     * @param predicate  条件
     * @return boolean
     * @deprecated use {@link CollUtil#findAll(Iterable, Predicate)}
     */
    @Deprecated
    public static <T> List<T> sub(final Collection<T> collection, final Predicate<T> predicate) {
        return findAll(collection, predicate);
    }

    public static <T> T computeIfAbsent(Collection<T> collection, Function<T, Object> function, Object value, Supplier<T> supplier) {
        T ret = get(collection, function, value);
        if (ret == null) {
            ret = supplier.get();
            collection.add(ret);
        }
        return ret;
    }


    // region 笛卡尔积

    /**
     * 笛卡尔积
     *
     * @param lists
     * @return void
     * @author zak
     * @date 2019/11/29 18:03
     */
    public static <T> List<List<T>> cartesianProduct(final List<List<T>> lists) {
        T[][] arrays = (T[][]) new Object[lists.size()][];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = (T[]) lists.get(i).toArray();
        }

        return cartesianProduct(arrays);
    }

    public static <T> List<List<T>> cartesianProduct(final T[][] arrays) {
        int[][] indexAndLength = new int[2][arrays.length];

        for (int i = 0; i < arrays.length; i++) {
            indexAndLength[0][i] = 0;
            indexAndLength[1][i] = arrays[i].length;
        }

        List<List<T>> cartesianProductList = new ArrayList<>();
        getOptions(arrays, indexAndLength, cartesianProductList);
        return cartesianProductList;
    }


    private static <T> void getOptions(final T[][] arrays, int[][] indexAndLength, List<List<T>> cartesianProductList) {
        List<T> ret = new ArrayList<>(arrays.length);
        cartesianProductList.add(ret);
        for (int i = 0; i < arrays.length; i++) {
            ret.add(arrays[i][indexAndLength[0][i]]);
        }

        if (addIndex(indexAndLength, arrays.length)) {
            getOptions(arrays, indexAndLength, cartesianProductList);
        }
    }

    private static boolean addIndex(int[][] indexAndLength, int index) {
        if (index <= 0) {
            return false;
        }

        if ((indexAndLength[0][index - 1] += 1) < indexAndLength[1][index - 1]) {
            return true;
        }
        indexAndLength[0][index - 1] = 0;
        return addIndex(indexAndLength, index - 1);
    }

    // endregion 笛卡尔积

    /**
     * 判断集合是否全为null
     *
     * @param collection Collection
     * @return
     */
    public static boolean isAllNull(final Collection<?> collection) {
        for (Object o : collection) {
            if (o != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 在两个不同的泛型的集合中根据指定的比较器比较两个集合中的元素是否相同
     *
     * @param collection1 集合1
     * @param collection2 集合2
     * @param comparator  比较器
     * @param sameSize    是否规定两个集合的大小相同  如果不规定,则以较小的集合为主进行比较
     * @return boolean
     * @author zak
     * @date 2019/12/26 17:43
     */
    public static <E1, E2> boolean crossClassMatch(Collection<E1> collection1, Collection<E2> collection2, CrossClassComparator<E1, E2> comparator, boolean sameSize) {

        if (sameSize && collection1.size() != collection2.size()) {
            return false;
        }

        final Iterator<E1> iterator1 = collection1.iterator();
        final Iterator<E2> iterator2 = collection2.iterator();
        Iterator mainIterator = collection1.size() < collection2.size()
                ? iterator1
                : iterator2;
        while (mainIterator.hasNext()) {
            if (comparator.compare(iterator1.next(), iterator2.next()) != 0) {
                return false;
            }
        }

        return true;

    }

    /**
     * 在两个不同的泛型的集合中根据指定的比较器比较两个集合中的元素是否相同
     *
     * @param collection1
     * @param collection2
     * @param comparator
     * @return boolean
     * @author zak
     * @date 2019/12/26 17:55
     */
    public static <E1, E2> boolean crossClassMatch(Collection<E1> collection1, Collection<E2> collection2, CrossClassComparator<E1, E2> comparator) {
        return crossClassMatch(collection1, collection2, comparator, true);
    }

    /**
     * 在两个不同的泛型的集合中根据指定的比较器比较两个集合中的元素是否相同
     * 但是不要求相同的对象所在的下标也一样
     *
     * @param collection1
     * @param collection2
     * @param comparator
     * @param sameSize
     * @return boolean
     * @author zak
     * @date 2019/12/26 18:07
     */
    public static <E1, E2> boolean crossClassBroadMatch(Collection<E1> collection1, Collection<E2> collection2,
                                                        CrossClassComparator<E1, E2> comparator, boolean sameSize) {
        if (sameSize && collection1.size() != collection2.size()) {
            return false;
        }


        boolean flg = collection1.size() < collection2.size()
                ? true
                : false;

        List list = new ArrayList();
        if (flg) {
            for (E1 e1 : collection1) {
                for (E2 e2 : collection2) {
                    boolean eq = false;
                    if (comparator.compare(e1, e2) == 0) {
                        if (contains(list, o -> o == e2)) {
                            continue;
                        }
                        list.add(e2);
                        eq = true;
                    }
                    if (eq == false) {
                        return false;
                    } else {
                        break;
                    }
                }
            }
        } else {
            for (E2 e2 : collection2) {
                for (E1 e1 : collection1) {
                    boolean eq = false;
                    if (comparator.compare(e1, e2) == 0) {
                        if (contains(list, o -> o == e1)) {
                            continue;
                        }
                        list.add(e1);
                        eq = true;
                    }
                    if (eq == false) {
                        return false;
                    } else {
                        break;
                    }
                }
            }
        }


        return true;
    }


    public static <E1, E2> boolean crossClassBroadMatch(Collection<E1> collection1, Collection<E2> collection2,
                                                        CrossClassComparator<E1, E2> comparator) {
        return crossClassBroadMatch(collection1, collection2, comparator, true);
    }


    /**
     * @param collection
     * @param predicate
     * @return java.util.Collection<E>
     * @author zak
     * @date 2020/8/24 9:44
     */
    public static <E> Collection<E> removeAll(Collection<E> collection, Predicate<E> predicate) {
        collection.removeAll(sub(collection, predicate));
        return collection;
    }

    /**
     * 将targetList中的元素安装sourceList中的元素的顺序进行排序
     *
     * @param sourceList 源list
     * @param targetList 目标list
     * @param function1  源list中元素顺序的参考字段
     * @param function2  目标list中元素顺序的参考字段
     * @return java.util.List<E2>
     * @author zak
     * @date 2019/12/27 16:07
     */
    public static <E1, E2> List<E2> shadowSort(
            final List<E1> sourceList,
            final Function<E1, Object> function1,
            final List<E2> targetList,
            final Function<E2, Object> function2
    ) {
        targetList.sort((o1, o2) -> firstIndex(sourceList, e1 -> Objects.equals(function1.apply(e1), function2.apply(o1)))
                - firstIndex(sourceList, e1 -> Objects.equals(function1.apply(e1), function2.apply(o2))));
        return targetList;
    }

    public static <E1> List<E1> shadowSort(
            final List<E1> sourceList,
            final List<E1> targetList,
            final Function<E1, Object> function
    ) {
        return shadowSort(sourceList, function, targetList, function);
    }

    public static <E1> List<E1> shadowSort(
            final List<E1> sourceList,
            final List<E1> targetList
    ) {
        return shadowSort(sourceList, targetList, e -> e);
    }


    /**
     * 转换集合中的元素
     *
     * @param coll      集合
     * @param converter 转换器
     * @return java.util.List<R>
     * @author Zhu Kaixiao
     * @date 2020/10/20 13:59
     * @deprecated {@link #toList(Collection, Function)}
     */
    public static <E, R> List<R> convert(Collection<E> coll, Function<E, R> converter) {
        return coll.stream().map(converter).collect(Collectors.toList());
    }

    /**
     * @deprecated {@link #toList(Collection, Function)} 不应该有多余的consumer，而是应该直接在mapper中处理
     */
    @Deprecated
    public static <E, R> List<R> convert(Collection<E> coll, Function<E, R> converter, java.util.function.Consumer<R> consumer) {
        List<R> rList = coll.stream().map(ele -> {
            R r = converter.apply(ele);
            consumer.accept(r);
            return r;
        }).collect(Collectors.toList());
        return rList;
    }


    /**
     * 转换list
     *
     * @param coll
     * @return java.util.List<R>
     * @author zak
     * @date 2020/8/24 10:22
     */
    public static <E, R> List<R> toList(Collection<E> coll, Function<? super E, ? extends R> mapper) {
        return coll.stream().map(mapper).collect(Collectors.toList());
    }

    public static <E> List<E> toList(Collection<E> coll) {
        if (coll instanceof List) {
            return (List<E>) coll;
        } else {
            return new ArrayList<>(coll);
        }
    }

    public static <E, R> Set<R> toSet(Collection<E> coll, Function<? super E, ? extends R> mapper) {
        return coll.stream().map(mapper).collect(Collectors.toSet());
    }

    public static <T, K, V> Map<K, V> toMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        Map<? extends K, ? extends V> map = collection.stream().collect(Collectors.toMap(keyMapper, valueMapper));
        return (Map<K, V>) map;
    }


    /**
     * 两个不同的类的对象进行比较
     */
    @FunctionalInterface
    public interface CrossClassComparator<T1, T2> {
        /**
         * 相同返回0 o1比o2小返回负数, o1比o2大返回正数
         *
         * @param o1
         * @param o2
         * @return int
         * @author zak
         * @date 2019/12/26 17:46
         */
        int compare(T1 o1, T2 o2);
    }


    public static <T> List<T> addFirst(List<T> list, T first) {
        if (list instanceof Deque) {
            Deque<T> deque = (Deque<T>) list;
            deque.addFirst(first);
        } else {
            list.add(0, first);
        }
        return list;
    }


    /**
     * 两个集合中，以指定的字段比较后取集合1-集合2后的集合（差集）
     *
     * @param coll1    集合一
     * @param coll2    集合二
     * @param function 指定字段方法
     * @param <E>      集合元素泛型
     * @return 集合1-集合2后的集合
     */
    public static <E> List<E> remain(Collection<E> coll1, Collection<E> coll2, Function<E, ?> function) {
        return remain(coll1, coll2, function, function);
    }

    public static <E> E setIfAbsent(List<E> list, int idx, E ele) {
        int size = list.size();
        if (idx >= size) {
            E[] arr = (E[]) new Object[idx - size + 1];
            list.addAll(Arrays.asList(arr));
            list.set(idx, ele);
            return ele;
        } else if (idx == size) {
            list.add(ele);
            return ele;
        } else {
            E obj = list.get(idx);
            if (obj == null) {
                list.set(idx, ele);
                return ele;
            }
            return obj;
        }
    }

    public static <E> E computeIfAbsent(List<E> list, int idx, Function<Integer, E> func) {
        int size = list.size();

        if (idx > size) {
            E[] arr = (E[]) new Object[idx - size + 1];
            list.addAll(Arrays.asList(arr));
            E ele = func.apply(idx);
            list.set(idx, ele);
            return ele;
        } else if (idx == size) {
            E ele = func.apply(idx);
            list.add(ele);
            return ele;
        } else {
            E obj = list.get(idx);
            if (obj == null) {
                E ele = func.apply(idx);
                list.set(idx, ele);
                return ele;
            }
            return obj;
        }
    }

    /**
     * 两个集合中，以指定的字段比较后取集合1-集合2后的集合（差集）
     *
     * @param coll1     集合一
     * @param coll2     集合二
     * @param function1 集合一指定字段方法
     * @param function2 集合二指定字段方法
     * @param <E1>      集合1元素泛型
     * @param <E2>      集合2元素泛型
     * @return 集合1-集合2后的集合
     */
    public static <E1, E2> List<E1> remain(Collection<E1> coll1, Collection<E2> coll2, Function<E1, ?> function1, Function<E2, ?> function2) {
        Set<?> set = toSet(coll2, function2);
        // 去掉null
        set.remove(null);
        List<E1> list = coll1.stream()
                .filter(e -> !set.contains(function1.apply(e)))
                .collect(Collectors.toList());

        return list;
    }


    public static <E> List<E> merge(Collection<? extends Collection<E>> colls) {
        List<E> newList = colls
                .stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        return newList;
    }

    /**
     * 把多个集合中的元素全部都放到一个新的list中
     * 注意：
     * 全部元素放到同一个list后会去重
     * 如不需要去重，请使用{@link #concat(Collection[])}
     *
     * @param colls
     * @return java.util.List<E>
     * @author Zhu Kaixiao
     * @date 2020/11/10 9:55
     */
    public static <E> List<E> merge(Collection<E>... colls) {
        List<E> newList = Arrays
                .stream(colls)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        return newList;
    }


    /**
     * 把多个集合中的元素全部都放到一个新的list中
     * 注意：
     * 只是全部放到一个新的list中，并不会去重，
     * 如需要去重，请使用{@link #merge(Collection[])}
     *
     * @param colls
     * @return java.util.List<E>
     * @author Zhu Kaixiao
     * @date 2020/11/10 9:55
     */
    public static <E> List<E> concat(Collection<E>... colls) {
        List<E> newList = Arrays
                .stream(colls)
                .filter(Objects::nonNull)
                .flatMap(coll -> coll.stream())
                .collect(Collectors.toList());
        return newList;
    }


}
