package com.developcollect.web.common.http;

import java.io.IOException;

public interface MutableRequest {


    void setBody(byte[] bytes) throws IOException;

}
