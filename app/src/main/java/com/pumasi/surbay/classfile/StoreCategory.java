package com.pumasi.surbay.classfile;

public class StoreCategory {
    private Integer type;
    private String category;

    public StoreCategory(Integer type, String category) {
        this.type = type;
        this.category = category;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
