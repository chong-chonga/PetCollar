package com.example.response;


/**
 * 所有响应消息中 data 的基类, 只提供单一个属性
 * 可以继承此类以用于 ReactiveResponse 的 data 设置
 * 对于额外的属性, 可以由子类额外提供
 * @author Lexin Huang
 * @since 2.0
 */
public abstract class ReactiveData<T>{
    private T val;

    protected void setVal(T v){
        this.val = v;
    }

    public T getVal(){
        return this.val;
    }

    @Override
    public String toString() {
        return "ReactiveData{" +
                "val=" + val +
                '}';
    }
}
