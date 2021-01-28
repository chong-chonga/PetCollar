package com.example.response;

import com.example.pojo.Collar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lexin Huang
 */
@ToString
public class CollarResponse extends ReactiveResponse{

    public CollarResponse() {
        super.data = new CollarData();
    }


    public void setData(List<Collar> collars) {
        CollarData data = (CollarData) super.data;
        data.setCollars(collars);
    }

    @Data
    @ToString
    @AllArgsConstructor
    public static class CollarData{
        List<Collar> collars;

        public CollarData(){
            collars = new ArrayList<>();
        }

    }
}
