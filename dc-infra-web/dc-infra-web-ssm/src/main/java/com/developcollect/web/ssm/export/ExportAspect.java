package com.developcollect.web.ssm.export;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.developcollect.core.utils.*;
import com.developcollect.core.utils.DateUtil;
import com.developcollect.core.web.common.R;
import com.developcollect.web.common.export.Export;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 导出处理切面
 */
@Slf4j
@Aspect
public class ExportAspect {


    private final HttpServletResponse response;
    private final ThreadLocal<Map<String, Map<String, String>>> enumsThreadLocal = new ThreadLocal<>();

    public ExportAspect(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * 定义切入点
     */
    @Pointcut(value = "@annotation(export)")
    public void serviceStatistics(Export export) {
    }

    /**
     * 环绕通知
     * @param joinPoint 切入点
     * @param export 注解
     * @throws Throwable
     */
    @Around(value = "serviceStatistics(export)", argNames = "joinPoint,export")
    public void around(ProceedingJoinPoint joinPoint, Export export) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof Page) {
                Page pageable = (Page) arg;

                /*这里只是调整为先取第一页 200条，后续会继续循环递归其余页数数据*/
                if (pageable.getCurrent() != 1 || pageable.getSize() < 200) {
                    pageable.setCurrent(1);
                    pageable.setSize(200);
                }
            }
        }

        Object retVal = joinPoint.proceed(args);

        postAdvice(joinPoint, export, retVal);
    }


    //    @AfterReturning(value = "serviceStatistics(excelExport)", returning = "returnValue",
//            argNames = "joinPoint,excelExport,returnValue")
    public void postAdvice(JoinPoint joinPoint, Export export, Object returnValue) throws IOException {
        // 判断返回值是否符合生成excel的条件
        List<Object> objectList = checkTarget(returnValue);
        if (objectList == null) {
            log.error("无法解析的数据: {}", returnValue);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        // 判断是否分页数据
        Page page = getPage(returnValue);

        try (
                OutputStream out = response.getOutputStream()
        ) {
            // 1. 处理文件名
            String excelBaseName = FileNameUtil.mainName(export.filename());
            String title = export.title();
            if (StrUtil.isBlank(excelBaseName)) {
                if (StrUtil.isNotBlank(title)) {
                    excelBaseName = title;
                } else {
                    excelBaseName = "export";
                }
            }
            String excelName = excelBaseName + ".xlsx";
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(excelName, "UTF-8"));
            // 2. 生成excel对象
            Workbook wb = this.genExcel(Arrays.asList(export.heads()), excelBaseName, title);
            /**pos 写入第多少行*/
            int pos = StrUtil.isBlank(title) ? 1 : 2;

            for (; ; ) {
                // 3. 根据注解解析List,
                List<List<Object>> valuesList = fetchValuesList(objectList, export);
                // 4. 写入数据到excel
                this.writeExcel(wb, valuesList, pos);
                // 如果已经到了最后一页， 推出循环
                if (page.getCurrent() >= page.getPages()) {
                    break;
                }
                pos += valuesList.size();
                // 5. 取下一页
                Page nextPage = nextPage(page, joinPoint);
                objectList = checkTarget(nextPage);
                page = nextPage;
            }
            // 6. 写出excel到输出流
            wb.write(out);
            out.flush();
        } catch (IOException e) {
            log.error("excel导出失败: {}#{}",
                    joinPoint.getTarget().getClass().getName(),
                    joinPoint.getSignature().getName(), e);
        } finally {
            enumsThreadLocal.remove();
        }
    }

    /**
     * 写出数据到excel
     *
     * @param wb         excel对象
     * @param valuesList 数据
     * @param pos        上一次写的位置
     * @author Zhu Kaixiao
     * @date 2020/4/29 15:08
     */
    private void writeExcel(Workbook wb, List<List<Object>> valuesList, int pos) {
        Sheet sheet = wb.getSheetAt(0);
        //第五步插入数据
        for (int i = 0; i < valuesList.size(); i++) {
            List<?> vals = valuesList.get(i);
            Row row = sheet.createRow(pos + i);
            for (int j = 0; j < vals.size(); j++) {
                //创建单元格并且添加数据
                Object val = vals.get(j);
                if (val == null) {
                    val = "";
                } else if (val instanceof Date) {
                    val = DateUtil.formatDateTime((Date) val);
                } else if (val instanceof LocalDateTime) {
                    val = DateUtil.formatDateTime((LocalDateTime) val);
                } else if (val instanceof LocalDate) {
                    val = DateUtil.formatDate((LocalDate) val);
                } else if (val instanceof LocalTime) {
                    val = DateUtil.formatTime((LocalTime) val);
                }
                row.createCell(j).setCellValue(val.toString());
                sheet.setColumnWidth(j, Math.max(sheet.getColumnWidth(j), (int) (val.toString().getBytes().length * 1.2d * 256)));
            }
        }
    }

    /**
     * 生成excel文档对象
     *
     * @param headerList 表头信息
     * @param sheetName  页名
     * @return org.apache.poi.ss.usermodel.Workbook
     * @author Zhu Kaixiao
     * @date 2020/4/29 15:08
     */
    private Workbook genExcel(List<String> headerList, String sheetName, String title) {
        //第一步创建workbook
//        HSSFWorkbook wb = new HSSFWorkbook();
//        FileOutputStream fOut = new FileOutputStream(savePath);
        Workbook wb = new SXSSFWorkbook(500);//每次缓存500条到内存，其余写到磁盘。
//        Sheet sheet = workbook.createSheet();
        //第二步创建sheet
        Sheet sheet = wb.createSheet(sheetName);
        Font font = wb.createFont();
        font.setFontName("微软雅黑");
        font.setBold(true);
        CellStyle style = wb.createCellStyle();
        //居中
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);

        int offset = 0;

        // 标题
        if (StrUtil.isNotBlank(title)) {
            Row row = sheet.createRow(offset++);
            Cell cell = row.createCell(0);
            cell.setCellValue(title);

            cell.setCellStyle(style);
            CellRangeAddress cra = new CellRangeAddress(0, 0, 0, headerList.size() - 1);
            sheet.addMergedRegion(cra);
            //注意：边框样式需要重新设置一下
            RegionUtil.setBorderTop(BorderStyle.NONE, cra, sheet);
        }

        //第三步创建行row:添加表头0行
        Row row = sheet.createRow(offset);
        //第四步创建表头单元格, 并设置表头数据
        for (int i = 0, size = headerList.size(); i < size; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(headerList.get(i));

            sheet.setColumnWidth(i, (int) (headerList.get(i).length() * 1.2d * 256));
        }
        return wb;
    }

    /**
     * 获取分页参数
     *
     * @param returnValue
     * @return com.baomidou.mybatisplus.core.metadata.IPage
     * @author Zhu Kaixiao
     * @date 2020/4/29 15:09
     */
    private Page getPage(Object returnValue) {
        Object retData = returnValue;
        if (retData instanceof R) {
            retData = ((R) returnValue).getData();
        }
        if (retData instanceof Page) {
            Page page = (Page) retData;
            return page;
        }

        Page page;
        if (retData instanceof List) {
            List list = (List) retData;
            page = new Page(1, list.size(), list.size());
            page.setRecords(list);
        } else {
            page = new Page(1, 1, 0);
            page.setRecords(Collections.emptyList());
        }

        return page;
    }

    /**
     * 获取下一页数据
     *
     * @param page
     * @param joinPoint
     * @return java.util.List<java.lang.Object>
     * @author Zhu Kaixiao
     * @date 2020/4/29 15:09
     */
    private Page nextPage(Page page, JoinPoint joinPoint) {
        page.setCurrent(page.getCurrent() + 1);

        Object target = joinPoint.getTarget();
        Object[] args = joinPoint.getArgs();

        Method method = getTargetMethod(joinPoint);
        Object invoke = ReflectUtil.invoke(target, method, args);
        if (invoke instanceof R) {
            R r = (R) invoke;
            return (Page) r.getData();
        }
        return (Page) invoke;

    }

    /**
     * 只有返回list, 对象数组、page, 或者返回JcResult但是包装的data是list或对象数组、page才开始生成Excel
     *
     * @param returnValue
     * @return java.util.List<java.lang.Object>
     * @author Zhu Kaixiao
     * @date 2020/3/14 14:33
     */
    private List<Object> checkTarget(Object returnValue) {
        Object retData = returnValue;
        if (retData instanceof R) {
            retData = ((R) returnValue).getData();
        }
        if (ArrayUtil.isArray(retData)) {
            retData = Arrays.asList(retData);
        }
        if (retData instanceof Page) {
            retData = ((Page) retData).getRecords();
        }
        if (!(retData instanceof List)) {
            return null;
        }
        return (List<Object>) retData;
    }


    /**
     * 提取填充excel的值
     *
     * @param objList 对象列表
     * @return java.util.List<java.util.List < java.lang.Object>>
     * @author Zhu Kaixiao
     * @date 2019/6/5 12:04
     **/
    private List<List<Object>> fetchValuesList(List<Object> objList, Export export) {
        // 3. 提取注解信息
        Map<String, Map<String, String>> enumMap = fetchEnums(export);
        List<List<Object>> ret = new ArrayList<>(objList.size());
        final String[] fields = export.fields();

        for (Object obj : objList) {
            List<Object> list = new ArrayList<>(fields.length);
            for (String fieldPath : fields) {
                Object property = BeanUtil.getProperty(obj, fieldPath);
                if (enumMap.containsKey(fieldPath) && property != null) {
                    property = Optional.ofNullable((Object) enumMap.get(fieldPath).get(property.toString())).orElse(property);
                }
                list.add(property);
            }
            ret.add(list);
        }

        return ret;
    }

    /**
     * 从注解中提取枚举字段描述
     *
     * @param export
     * @return java.util.Map<java.lang.String, java.util.Map < java.lang.String, java.lang.String>>
     * @author Zhu Kaixiao
     * @date 2020/4/29 15:10
     */
    private Map<String, Map<String, String>> fetchEnums(Export export) {
        if (enumsThreadLocal.get() != null) {
            return enumsThreadLocal.get();
        }
        com.developcollect.web.common.export.Enum[] enums = export.enums();
        if (enums.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, String>> ret = new HashMap<>(enums.length);
        for (com.developcollect.web.common.export.Enum anEnum : enums) {
            ret.put(anEnum.field(), convertEnumToMap(anEnum));
        }
        enumsThreadLocal.set(ret);
        return ret;
    }

    /**
     * 将注解中的枚举描述转换成map
     *
     * @param anEnum
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author Zhu Kaixiao
     * @date 2020/4/29 15:10
     */
    private Map<String, String> convertEnumToMap(com.developcollect.web.common.export.Enum anEnum) {
        Map<String, String> ret = new HashMap<>(anEnum.values().length);
        if (anEnum.labels().length != anEnum.values().length) {
            throw new IllegalArgumentException("excel导出配置错误，[" + anEnum.field() + "]枚举说明的变量数量和标签数量不一致");
        }
        for (int i = 0; i < anEnum.values().length; i++) {
            ret.put(anEnum.values()[i], anEnum.labels()[i]);
        }
        return ret;
    }


    private Method getTargetMethod(JoinPoint joinPoint) {
        Object mi = ReflectUtil.getFieldValue(joinPoint, "methodInvocation");
        Object method = ReflectUtil.getFieldValue(mi, "method");
        return (Method) method;
    }

}
