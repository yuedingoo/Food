package com.yueding.food.db;

import org.litepal.crud.DataSupport;

/**
 * Created by yueding on 2017/11/4.
 */

public class Restaurant extends DataSupport {
    private int id;
    private String name;
    private String remarks;
    private int code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
