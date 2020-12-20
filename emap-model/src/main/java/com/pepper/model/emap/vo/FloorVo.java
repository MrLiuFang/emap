package com.pepper.model.emap.vo;

import com.pepper.model.emap.lift.Floor;

public class FloorVo  extends Floor {

    private Boolean isRight = false;

    public Boolean getRight() {
        return isRight;
    }

    public void setRight(Boolean right) {
        isRight = right;
    }
}
