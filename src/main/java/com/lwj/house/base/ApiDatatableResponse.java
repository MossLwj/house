package com.lwj.house.base;

import lombok.Getter;
import lombok.Setter;

/**
 * Datatables响应结构
 *
 * @author lwj
 */
@Getter
@Setter
public class ApiDatatableResponse extends ApiResponse {

    private int draw;

    private long recordsTotal;

    private long recordsFiltered;

    public ApiDatatableResponse(ApiResponse.Status status) {
        this(status.getCode(), status.getStandardMessage(), null);
    }

    public ApiDatatableResponse(int code, String message, Object data) {
        super(code, message, data);
    }
}
