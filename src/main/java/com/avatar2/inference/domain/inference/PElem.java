package com.avatar2.inference.domain.inference;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PElem implements Cloneable{
    private String id;
    private String x;
    private String y;

    @Builder
    public PElem(String id, String x, String y){
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public PElem clone() throws CloneNotSupportedException {
        return (PElem) super.clone();
    }
}
