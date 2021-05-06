package com.developcollect.core.db;

import java.util.List;

public interface IPage<T> {
    int getPageNum();
    int getPageSize();
    long getTotal();
    List<T> getContent();
}
