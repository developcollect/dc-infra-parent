package com.developcollect.core.db;

import java.util.List;

public class Page<T> implements IPage<T> {

    protected List<T> content;
    protected int pageNum;
    protected int pageSize;
    protected long total;


    private Page() {

    }

    @Override
    public int getPageNum() {
        return pageNum;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public List<T> getContent() {
        return content;
    }


    public static <T> Page<T> of(int pageNum, int pageSize, long total, List<T> content) {
        Page<T> page = new Page<>();
        page.pageNum = pageNum;
        page.pageSize = pageSize;
        page.total = total;
        page.content = content;
        return page;
    }
}
