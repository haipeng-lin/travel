package com.wen.shuzhi.loginRegister.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class R {
    private Boolean flag;
    private Object data;
    private String msg;

    public R(){};

    public R(Boolean flag,Object data){
        this.flag=flag;
        this.data=data;
    }

    public R(Boolean flag,String msg){
        this.flag=flag;
        this.msg=msg;
    }


}
