package com.blossom.documentdemo.design_pattern.decorate;

import org.junit.Test;

public class AddLampTest {

    @Test
    public void testDecorate() {
        SiYu siYu = new SiYu();
        AddLamp addLamp = new AddLamp(siYu);
        System.out.print("我的价格是：" + addLamp.price() + "，并且" + addLamp.description());
    }

}