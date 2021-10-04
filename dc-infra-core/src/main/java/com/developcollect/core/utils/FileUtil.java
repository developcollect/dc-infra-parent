package com.developcollect.core.utils;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 文件工具类
 *
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(FileUtil.class);


    static final String SPECIAL_S1 = "\0";
    static final String SPECIAL_S2 = "/";
    static final String SPECIAL_S3 = "\\";

    static final String SPECIAL_S5 = "../";
    static final String SPECIAL_S6 = "..\\";
    static final String SPECIAL_S7 = "|";
    static final String SPECIAL_S8 = "\\|";
    static final String SPECIAL_S9 = "glob:";
    static final String SPECIAL_S10 = "regex:";
    static Integer int1024 = 1024;
    static Float float1024 = 1024.0f;


    private static Map<String, String> fileTypeMap = new HashMap<String, String>();
    private static Map<String, List<String>> whiteList = new HashMap<String, List<String>>();
    private static List<String> blackList = new ArrayList<>();


    /**
     * 根据文件大小转换为B、KB、MB、GB单位字符串显示
     *
     * @param fileSize 文件的大小,单位: byte
     * @return 返回 转换后带有单位的字符串
     */
    public static String humanSize(long fileSize) {
        String strFileSize;
        if (fileSize < int1024) {
            strFileSize = fileSize + "B";
        } else if (fileSize < int1024 * int1024) {
            strFileSize = Math.ceil(fileSize / float1024 * 100) / 100 + "KB";
        } else if (fileSize < int1024 * int1024 * int1024) {
            strFileSize = Math.ceil(fileSize / (int1024 * float1024) * 100) / 100 + "MB";
        } else {
            strFileSize = Math.ceil(fileSize / (int1024 * int1024 * float1024) * 100) / 100 + "GB";
        }
        return strFileSize;
    }

    public static String humanSize(File file) {
        return humanSize(size(file));
    }


    /**
     * 根据文件头特征获取文件的真实类型
     *
     * @param file 文件对象
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/11/23 14:30
     * @deprecated 用 {@link #getType(File)}
     */
    @Deprecated
    public static String getRealType(File file) {
        return getType(file);
    }

    /**
     * 从输入流中读取前10个字节数据作为文件特征进行识别
     * 从而获取文件的真实类型
     * 当无法识别时返回空字符串
     *
     * @param in 输入流
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 15:36
     * @deprecated 用 {@link #getType(InputStream)}
     */
    @Deprecated
    public static String getRealType(InputStream in) {
        return getType(in);
    }

    public static String getType(InputStream in) {
        return FileTypeUtil.getType(in);
    }


    /**
     * 以指定的后缀名创建临时文件<br>
     * 创建后的文件名为 [Random].[suffix]
     * 创建的文件在 System.getProperty("java.io.tmpdir") 文件夹下
     *
     * @param suffix
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 14:35
     */
    public static File createTempFile(String suffix) {
        File tempFile = createTempFileIn(getTmpDirPath(), suffix);
        return tempFile;
    }

    /**
     * 在指定目录下创建临时文件
     *
     * @param dir
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 15:15
     */
    public static File createTempFileIn(String dir) {
        File tempFile = createTempFile("tempfile", null, new File(dir), true);
        return tempFile;
    }

    /**
     * 在指定目录下以指定后缀名创建临时文件
     *
     * @param dir
     * @param suffix
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 15:17
     */
    public static File createTempFileIn(String dir, String suffix) {
        if (!suffix.startsWith(StrUtil.DOT)) {
            suffix = StrUtil.DOT + suffix;
        }
        File tempFile = createTempFile("tempfile", suffix, new File(dir), true);
        return tempFile;
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 [Random].tmp
     * 创建的文件在 System.getProperty("java.io.tmpdir") 文件夹下
     *
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 14:35
     */
    public static File createTempFile() {
        File tempFile = createTempFileIn(cn.hutool.core.io.FileUtil.getTmpDirPath(), ".tmp");
        return tempFile;
    }

    /**
     * 输入流转字节输出流
     *
     * @param inputStream InputStream
     * @Title: convertInputStreamToByte
     * @return: ByteArrayOutputStream
     */
    public static ByteArrayOutputStream convertInputStreamToByte(InputStream inputStream) {
        try {
            ByteArrayOutputStream outByte = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                outByte.write(buffer, 0, len);
            }
            outByte.flush();
            return outByte;
        } catch (Exception e) {
            log.debug("文件流转换出错{}", e.getMessage());
            return null;
        }
    }

    /**
     * 是否非法文件名或者路径
     *
     * @param basePath 限定路径（选择文件或者路径只能是在这下面） 可为空，为空则不验证限定路径
     * @param filename 文件名或者路径 为空串则返回true 不可为空 为空则抛出异常 非法请求
     * @Title: isValidFilename
     */
    public static void isValidFilename(String basePath, String filename) {
        if (StrUtil.isBlank(filename)) {
            throw new IllegalArgumentException("filename or path is valid!");
        }
        if (filename.contains(SPECIAL_S5) || filename.contains(SPECIAL_S6) || (filename.indexOf(SPECIAL_S1) != -1)) {
            throw new IllegalArgumentException("filename or path is valid!");
        } else {
            if (StrUtil.isNotBlank(basePath)) {
                if (!filename.startsWith(basePath)) {
                    throw new IllegalArgumentException("filename or path is valid!");
                }
            }
        }
    }

    /**
     * 格式化文件名或者路徑名
     *
     * @param filename 文件名或者路径名
     * @Title: normalizeFilename
     * @return: String
     */
    public static String normalizeFilename(String filename) {
        if (StrUtil.isBlank(filename)) {
            return filename;
        }
        return java.text.Normalizer.normalize(filename, java.text.Normalizer.Form.NFKD);
    }


    /**
     * 二进制数组转十六进制
     *
     * @param src 文件数组
     * @return
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (null == src || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取文件真实类型
     *
     * @param fileHeaderCode 文件头
     * @return
     */
    private static String getFileSuffix(String fileHeaderCode) {
        for (Map.Entry<String, String> entry : fileTypeMap.entrySet()) {
            String key = entry.getKey();

            boolean match = key.length() > fileHeaderCode.length()
                    ? key.toLowerCase().startsWith(fileHeaderCode.toLowerCase())
                    : fileHeaderCode.toLowerCase().startsWith(key.toLowerCase());

            if (match) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * 获取文件头
     *
     * @param in 輸入流
     * @Title: getFileHeaderCode
     * @return: String
     */
    private static String getFileHeaderCode(InputStream in) {
        byte[] b = new byte[10];
        String fileCode = "";
        try {
            in.read(b, 0, b.length);
            fileCode = bytesToHexString(b);
        } catch (IOException e) {
            log.error("IOException:", e);
        }
        return fileCode;
    }


    /**
     * 修改文件的后缀为该文件的真实类型
     *
     * @param file
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 15:03
     */
    public static File renameToRealType(File file) {
        String newSuffix = "." + FileUtil.getRealType(file);
        String canonicalPath = FileUtil.getCanonicalPath(file);
        if (canonicalPath.endsWith(newSuffix)) {
            return file;
        }
        String newPath = StrUtil.subBefore(canonicalPath, ".", true) + newSuffix;
        rename(file, newPath, false, true);
        return new File(newPath);
    }


    // region -------------------------------- loopDirs --------------------------------

    /**
     * 递归遍历指定文件夹下的所有文件夹
     *
     * @param path
     * @return java.util.List<java.io.File>
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:52
     */
    public static List<File> loopDirs(String path) {
        return loopDirs(file(path));
    }

    public static List<File> loopDirs(File file) {
        return loopDirs(file, null);
    }

    /**
     * 递归遍历指定文件夹下的所有符合过滤器过滤规则的文件夹
     *
     * @param path
     * @param fileFilter
     * @return java.util.List<java.io.File>
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:52
     */
    public static List<File> loopDirs(String path, FileFilter fileFilter) {
        return loopDirs(file(path), fileFilter);
    }

    public static List<File> loopDirs(File file, FileFilter fileFilter) {
        return loopDirs(file, fileFilter, 0);
    }

    private static List<File> loopDirs(File file, FileFilter fileFilter, int deep) {
        final List<File> fileList = new ArrayList<>();
        if (null == file || false == file.exists()) {
            return fileList;
        }

        if (file.isDirectory()) {
            if (null == fileFilter || fileFilter.accept(file)) {
                fileList.add(file);
            }

            final File[] subFiles = file.listFiles();
            if (ArrayUtil.isNotEmpty(subFiles)) {
                for (File tmp : subFiles) {
                    fileList.addAll(loopDirs(tmp, fileFilter, ++deep));
                }
            }
        }

        return fileList;
    }

    // endregion


    // region -------------------------------- loopDirsAndFiles --------------------------------

    /**
     * 递归遍历指定文件夹下所有的文件夹和文件
     *
     * @param path
     * @return java.util.List<java.io.File>
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:53
     */
    public static List<File> loopDirsAndFiles(String path) {
        return loopDirsAndFiles(file(path));
    }

    public static List<File> loopDirsAndFiles(File file) {
        return loopDirsAndFiles(file, null);
    }

    /**
     * 递归遍历指定文件夹下所有的符合过滤器过滤规则的文件夹和文件
     *
     * @param path
     * @return java.util.List<java.io.File>
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:53
     */
    public static List<File> loopDirsAndFiles(String path, FileFilter fileFilter) {
        return loopDirsAndFiles(file(path), fileFilter);
    }

    public static List<File> loopDirsAndFiles(File file, FileFilter fileFilter) {
        return loopDirsAndFiles(file, fileFilter, 0);
    }


    private static List<File> loopDirsAndFiles(File file, FileFilter fileFilter, int deep) {
        final List<File> fileList = new ArrayList<>();
        if (null == file || false == file.exists()) {
            return fileList;
        }

        if (file.isDirectory()) {
            boolean add = deep > 0 && (null == fileFilter || fileFilter.accept(file));
            if (add) {
                fileList.add(file);
            }

            final File[] subFiles = file.listFiles();
            if (ArrayUtil.isNotEmpty(subFiles)) {
                for (File tmp : subFiles) {
                    fileList.addAll(loopDirsAndFiles(tmp, fileFilter, ++deep));
                }
            }
        } else {
            if (null == fileFilter || fileFilter.accept(file)) {
                fileList.add(file);
            }
        }

        return fileList;
    }

    // endregion


    // region -------------------------------- loopDirsByPattern --------------------------------

    /**
     * 递归遍历指定文件夹下所有的符合通配符规则的文件夹
     *
     * @param rootPath
     * @param pathPattern 通配符
     * @return java.util.List<java.io.File>
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:53
     */
    public static List<File> loopDirsByPattern(String rootPath, String pathPattern) {
        return loopDirsByPattern(file(rootPath), pathPattern);
    }

    public static List<File> loopDirsByPattern(String rootPath, List<String> pathPatterns) {
        return loopDirsByPattern(file(rootPath), pathPatterns);
    }

    public static List<File> loopDirsByPattern(File file, String pathPattern) {
        if (pathPattern.contains(SPECIAL_S7)) {
            return loopDirsByPattern(file, Arrays.asList(pathPattern.split(SPECIAL_S8)));
        }
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(dressPathPattern(pathPattern));
        List<File> files = FileUtil.loopDirs(file, pathname -> matcher.matches(Paths.get(pathname.toURI())));
        return files;
    }

    public static List<File> loopDirsByPattern(File file, List<String> pathPatterns) {
        List<PathMatcher> matchers = pathPatterns.stream()
                .map(FileUtil::dressPathPattern)
                .map(p -> FileSystems.getDefault().getPathMatcher(p))
                .collect(Collectors.toList());


        List<File> files = FileUtil.loopDirs(file, pathname -> {
            for (PathMatcher matcher : matchers) {
                if (matcher.matches(Paths.get(pathname.toURI()))) {
                    return true;
                }
            }
            return false;
        });
        return files;
    }

    // endregion


    // region -------------------------------- loopFilesByPattern --------------------------------

    public static List<File> loopFilesByPattern(String rootPath, String pathPattern) {
        return loopFilesByPattern(file(rootPath), pathPattern);
    }

    public static List<File> loopFilesByPattern(String rootPath, List<String> pathPatterns) {
        return loopFilesByPattern(file(rootPath), pathPatterns);
    }

    public static List<File> loopFilesByPattern(File file, String pathPattern) {
        if (pathPattern.contains(SPECIAL_S7)) {
            return loopFilesByPattern(file, Arrays.asList(pathPattern.split(SPECIAL_S8)));
        }
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(dressPathPattern(pathPattern));
        List<File> files = FileUtil.loopFiles(file, pathname -> matcher.matches(Paths.get(pathname.toURI())));
        return files;
    }

    public static List<File> loopFilesByPattern(File file, List<String> pathPatterns) {
        List<PathMatcher> matchers = pathPatterns.stream()
                .map(FileUtil::dressPathPattern)
                .map(p -> FileSystems.getDefault().getPathMatcher(p))
                .collect(Collectors.toList());


        List<File> files = FileUtil.loopFiles(file, pathname -> {
            for (PathMatcher matcher : matchers) {
                if (matcher.matches(Paths.get(pathname.toURI()))) {
                    return true;
                }
            }
            return false;
        });
        return files;
    }

    // endregion


    // region -------------------------------- loopDirsAndFilesByPattern --------------------------------

    public static List<File> loopDirsAndFilesByPattern(String rootPath, String pathPattern) {
        return loopDirsAndFilesByPattern(file(rootPath), pathPattern);
    }

    public static List<File> loopDirsAndFilesByPattern(String rootPath, List<String> pathPatterns) {
        return loopDirsAndFilesByPattern(file(rootPath), pathPatterns);
    }

    public static List<File> loopDirsAndFilesByPattern(File file, String pathPattern) {
        if (pathPattern.contains(SPECIAL_S7)) {
            return loopDirsAndFilesByPattern(file, Arrays.asList(pathPattern.split(SPECIAL_S8)));
        }
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(dressPathPattern(pathPattern));
        List<File> files = FileUtil.loopDirsAndFiles(file, pathname -> {
            System.out.println(pathname.getAbsolutePath());
            return matcher.matches(Paths.get(pathname.toURI()));
        });
        return files;
    }

    public static List<File> loopDirsAndFilesByPattern(File file, List<String> pathPatterns) {
        List<PathMatcher> matchers = pathPatterns.stream()
                .map(FileUtil::dressPathPattern)
                .map(p -> FileSystems.getDefault().getPathMatcher(p))
                .collect(Collectors.toList());


        List<File> files = FileUtil.loopDirsAndFiles(file, pathname -> {
            for (PathMatcher matcher : matchers) {
                if (matcher.matches(Paths.get(pathname.toURI()))) {
                    return true;
                }
            }
            return false;
        });
        return files;
    }

    // endregion


    /**
     * 修饰路径通配符，文件通配符支持glob语法和regex语法，默认使用glob语法
     *
     * @param pathPattern
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:55
     */
    public static String dressPathPattern(String pathPattern) {
        if (!pathPattern.startsWith(SPECIAL_S9) && !pathPattern.startsWith(SPECIAL_S10)) {
            pathPattern = SPECIAL_S9 + pathPattern;
        }
        return pathPattern;
    }

    /**
     * 读取文件中所有的值
     *
     * @param file
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:56
     */
    public static String readAll(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader utf8Reader = getUtf8Reader(file)) {
            String tmp;
            while ((tmp = utf8Reader.readLine()) != null) {
                sb.append(tmp).append("\n");
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return sb.toString();
    }

    public static void writeAll(String content, File file) {
        if (!exist(file)) {
            touch(file);
        }
        try (BufferedWriter writer = getWriter(file, StandardCharsets.UTF_8, false)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }


    /**
     * 判断指定文件夹下是否有文件夹，不递归判断
     *
     * @param dir
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:56
     */
    public static boolean hasDir(File dir) {
        if (dir == null || dir.isFile() || isDirEmpty(dir)) {
            return false;
        }
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir.toPath(), entry -> entry.toFile().isDirectory());
            return directoryStream.iterator().hasNext();
        } catch (IOException e) {
            return LambdaUtil.raise(e);
        }
    }

    /**
     * 判断指定文件夹下是否有文件，不递归判断
     * 注意：文件夹下必须有文件才返回true，即使文件夹中有嵌套文件夹也返回false
     *
     * @param dir 文件夹
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:56
     */
    public static boolean hasFile(File dir) {
        if (dir == null || dir.isFile() || isDirEmpty(dir)) {
            return false;
        }
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir.toPath(), entry -> entry.toFile().isFile());
            return directoryStream.iterator().hasNext();
        } catch (IOException e) {
            return LambdaUtil.raise(e);
        }
    }

    /**
     * 列出指定文件夹下的所有文件夹，不会递归列出
     * <p>
     * 相当于  ls -l | grep ^d 命令
     *
     * @param dir
     * @return java.util.List<java.io.File>
     */
    public static List<File> lsDirs(File dir) {
        return lsDirs(dir, f -> true);
    }

    /**
     * 列出指定文件夹下所有的文件夹
     *
     * @param dir
     * @param fileFilter
     * @return java.util.List<java.io.File>
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:57
     */
    public static List<File> lsDirs(File dir, FileFilter fileFilter) {
        return lsAll(dir, f -> f.isDirectory() && fileFilter.accept(f));
    }

    /**
     * 列出指定文件夹下所有的文件
     *
     * @param dir
     * @return java.util.List<java.io.File>
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:57
     */
    public static List<File> lsFiles(File dir) {
        return lsFiles(dir, f -> true);
    }


    public static List<File> lsFiles(File dir, FileFilter fileFilter) {
        return lsAll(dir, f -> f.isFile() && fileFilter.accept(f));
    }

    /**
     * 列出指定文件夹下所有的文件和文件夹
     *
     * @param dir
     * @return java.util.List<java.io.File>
     * @author Zhu Kaixiao
     * @date 2020/10/24 11:57
     */
    public static List<File> lsAll(File dir, FileFilter fileFilter) {
        if (dir == null || !dir.isDirectory()) {
            return Collections.emptyList();
        }
        File[] files = dir.listFiles();
        if (ArrayUtil.isEmpty(files)) {
            return Collections.emptyList();
        }

        List<File> dirs = Arrays.stream(files)
                .filter(fileFilter::accept)
                .collect(Collectors.toList());
        return dirs;
    }

    public static List<File> lsAll(File dir) {
        return lsAll(dir, f -> true);
    }


    /**
     * 获取子文件从父文件夹起始的路径
     *
     * @param parent
     * @param child
     * @return java.lang.String
     */
    public static String relativePath(File parent, File child) {
        String parentAbsolutePath = parent.getAbsolutePath();
        return child.getAbsolutePath().substring(parentAbsolutePath.length());
    }


    /**
     * 同步源文件和目标文件
     * 同步前：
     * a,b  ==   a,c,d
     * 同步后：
     * a,b,c,d  ==   a,b,c,d
     * @param source
     * @param target
     * @author Zhu Kaixiao
     * @date 2020/10/27 10:05
     */
    public static void sync(File source, File target) {
        if (source.isDirectory() && target.isDirectory()) {

            Set<String> sourceFiles = CollectionUtil.newHashSet(source.list());
            Set<String> targetFiles = CollectionUtil.newHashSet(target.list());

            // remove files from target that are not in source
            for (String targetFile : targetFiles) {
                if (!sourceFiles.contains(targetFile)) {
                    del(new File(target, targetFile));
                }
            }

            for (String sourceFile : sourceFiles) {
                File file = new File(source, sourceFile);
                File file2 = new File(target, sourceFile);
                if (file.isFile()) {
                    copyIfChanged(file, file2);
                } else {
                    file2.mkdir();
                    sync(file, file2);
                }
            }
        } else if (source.isFile() && target.isFile()) {
            copyIfChanged(source, target);
        }
    }

    /**
     * 复制源文件到目标文件，如果目标文件不存在，直接复制
     * 如果目标文件相对源文件发生变动，用源文件覆盖，否则不做任何操作
     *
     * @param source
     * @param target
     * @author Zhu Kaixiao
     * @date 2020/10/27 10:02
     */
    public static void copyIfChanged(File source, File target) {
        if (target.exists()) {
            if (source.length() == target.length() && checksumCRC32(source) == checksumCRC32(target)) {
                return;
            } else {
                target.delete();
            }
        }
        if (!source.renameTo(target)) {
            move(source, target, true);
        }
    }


    public static void walkFiles(String path, Consumer<File> consumer) {
        walkFiles(new File(path), consumer);
    }
}
