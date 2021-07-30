package com.developcollect.core.io.resource;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.UrlResource;

import java.net.URL;

public class ResourceUtil extends cn.hutool.core.io.resource.ResourceUtil {

    public static String readUtf8Str(String resource, Class<?> baseClass) {
        URL resource1 = getResource(resource, baseClass);
        Resource resource2  = new UrlResource(resource1);
        return resource2.readUtf8Str();
    }

}
