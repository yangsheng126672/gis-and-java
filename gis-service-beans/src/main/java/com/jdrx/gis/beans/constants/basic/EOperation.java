package com.jdrx.gis.beans.constants.basic;

public enum EOperation {
    /**
     *
     */
    INSERT(1, "增加"), UPDATE(2, "修改"), DELETE(3, "删除");

    Integer val;
    String desc;

    EOperation(Integer val, String desc){
        this.val = val;
        this.desc = desc;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
