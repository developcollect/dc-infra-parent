package com.developcollect.web.common.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IDsDTO<ID extends Serializable> {

    private List<ID> ids;

}
