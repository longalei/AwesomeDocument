package com.blossom.documentdemo.design_pattern.decorate;

public class SiYu implements Car {

    private final int price = 10000;
    private final String desc = "这是一辆基本的思域！";


    @Override
    public int price() {
        return price;
    }

    @Override
    public String description() {
        return desc;
    }
}
