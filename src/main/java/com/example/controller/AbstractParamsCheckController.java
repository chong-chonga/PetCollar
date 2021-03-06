package com.example.controller;

/**
 * @author Lexin Huang
 * 名称参数校验功能的控制器
 */
public abstract class AbstractParamsCheckController<T> {

    /**
     * 创建新对象时的校验方法, 一般由泛型自行规定
     * @param objToCheck 需要校验格式的对象
     */
    protected abstract void entityFormatCheck(T objToCheck);

}
