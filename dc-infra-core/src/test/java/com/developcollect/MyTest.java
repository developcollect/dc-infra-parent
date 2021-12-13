package com.developcollect;


import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 */

public class MyTest {


    public static void main(String[] args) {
        String filePath = "C:\\Users\\cy\\Desktop\\新建文本文档.txt";
        MyTest main = new MyTest();
//        main.getModel(filePath);
        main.getResultMapXML(filePath);
    }


    private void getModel(String filePath) {
        System.out.println("//------------以下是生成的Model代码-----------");
        InputStream is = null;
        String propertyName;
        String typeName;
        String commentStr;
        try {
            is = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8), 512);
            // 读取一行，存储于字符串列表中
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.length() <= 1) {
                    continue;
                }
                //  生成Model, private String aaColumn; //a_column的注释
                if (line.contains("CREATE")) {
                    System.out.println();
                    System.out.println(line.substring(line.indexOf("`") + 1, line.lastIndexOf("`")));
                }
                propertyName = this.getPropertyName(line);
                typeName = this.getType(line);
                if (!"NULL".equals(typeName) && !"NULL".equals(propertyName)) {
                    commentStr = this.getComment(line);
                    System.out.println("private " + typeName + " " + propertyName + ";  //" + commentStr);
                }
            }
        } catch (IOException fnfe) {
            fnfe.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//getModel

    /**
     * 根据SQL获取 ResultMap XML代码，支持多个建表语句
     * 输入：建表语句的文件路径
     * 输出：ResultMap
     *
     * @param filePath
     */
    private void getResultMapXML(String filePath) {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8), 512);
            // 读取一行，存储于字符串列表中
            System.out.println();
            System.out.println("<!--  ----------以下是生成的XML代码---------  -->");
            String tableName;
            String typeName;
            String propertyName;
            String columnName;
            StringBuilder sbTableName = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if (line.length() == 0 || line.contains("-") || line.contains("DROP")) {
                    continue;
                }
                if (line.contains("CREATE")) {
                    tableName = line.substring(line.indexOf("`") + 1, line.lastIndexOf("`"));
                    tableName = this.getTableName(tableName);
                    sbTableName.append("<resultMap id=\"").append(tableName).append("Map\"  ").append("class=\"").append(tableName).append("\">");
                    System.out.println(sbTableName.toString());
                    sbTableName.delete(0, sbTableName.length());
                }
                //生成xml <result property="abcProperty" column="abc_property"/>
                propertyName = this.getPropertyName(line);
                if (!"NULL".equals(propertyName)) {
                    columnName = this.getColumnName(line);
                    typeName = this.getType(line);
                    System.out.print("<result property=" + "\"" + propertyName + "\""
                            + " column=" + "\"" + columnName + "\"");
                    if ("Date".equals(typeName)) {
                        System.out.println("  javaType=\"java.util.Date\""
                                + "/>");
                    } else {
                        System.out.println("/>");
                    }
                }
                if (line.contains("ENGINE=")) {
                    System.out.println("</resultMap>");
                    System.out.println("");
                }
            }//for
        } catch (IOException fnfe) {
            fnfe.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//getResultMapXML

    /**
     * 获取对应的java类型
     *
     * @param line
     * @return
     */
    private String getType(String line) {
        if (line.length() > 1 && !line.contains("CREATE") && !line.contains("PRIMARY") && !line.contains("KEY") && !line.contains("ENGINE=")) {
            line = line.substring(line.lastIndexOf("`") + 2, line.length());
            line = line.substring(0, line.indexOf(" "));
            if (line.contains("bigint")) {
                return "Long";
            } else if (line.contains("char")) {//此时也包括 varchar类型
                return "String";
            } else if (line.contains("double")) {
                return "Double";
            } else if (line.contains("int")) {//此时也包括 tinyint类型
                return "Integer";
            } else if (line.contains("date")) {//此时也包括 datetime类型
                return "Date";
            }
        }
        return "NULL";
    }

    /**
     * 把字段名转化为驼峰命名的属性名,abc_property ->abcProperty
     *
     * @param line
     * @return
     */
    private String getPropertyName(String line) {
        if (line.length() > 1 && !line.contains("CREATE") && !line.contains("PRIMARY") && !line.contains("KEY") && !line.contains("ENGINE=")) {
            String tempDelimiter = line.substring(line.indexOf("`") + 1, line.lastIndexOf("`"));
            String tempDelimiterArray[] = tempDelimiter.split("_");//如果不包含“_”，此时就含有字符串一个元素
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < tempDelimiterArray.length; i++) {
                if (i == 0) {
                    sb.append(tempDelimiterArray[i]);
                }
                if (i != 0) {  //除第一个单词外，其他的单词的首字母大写,String substring(int beginIndex)
                    sb.append(tempDelimiterArray[i].substring(0, 1).toUpperCase() + tempDelimiterArray[i].substring(1));
                }
            }//for
            return sb.toString();
        }
        return "NULL";
    }

    /**
     * 获取SQL的注释
     *
     * @param line
     * @return
     */
    private String getComment(String line) {
        if (line.contains("COMMENT")) {
            return line.substring(line.indexOf("'") + 1, line.lastIndexOf("',"));
        } else if (line.contains("AUTO_INCREMENT")) {
            return "主键Id";
        } else if (line.contains("created_time")) {
            return "创建时间";
        } else if (line.contains("updated_time")) {
            return "修改时间";
        }
        return "NULL";
    }

    /**
     * 获取sql的字段名.`abc_property` -> abc_property
     *
     * @param line
     * @return
     */
    private String getColumnName(String line) {
        line = line.substring(line.indexOf("`") + 1, line.lastIndexOf("`"));
        return line;
    }

    /**
     * 转化为驼峰命名的表名,abc_table -> AbcTable
     *
     * @param tempDelimiter
     * @return
     */
    private String getTableName(String tempDelimiter) {
        String tempDelimiterArray[] = tempDelimiter.split("_");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tempDelimiterArray.length; i++) {
            sb.append(tempDelimiterArray[i].substring(0, 1).toUpperCase() + tempDelimiterArray[i].substring(1));
        }//for
        return sb.toString();
    }

}


