package com.blossom.documentdemo.design_pattern.decorate;

//添加鱼骨灯的思域,鱼骨灯的价格是1500
public class AddLamp implements Decorate {

    private Car car;

    public AddLamp(Car car) {
        this.car = car;
    }

    @Override
    public int price() {
        return car.price() + 1500;
    }

    @Override
    public String description() {
        return car.description() + ":我加装了鱼骨灯！";
    }
}
