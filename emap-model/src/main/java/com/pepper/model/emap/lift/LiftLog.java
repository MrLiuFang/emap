package com.pepper.model.emap.lift;

import com.pepper.core.base.BaseModel;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity()
@Table(name = "t_lift_log")
@DynamicUpdate(true)
public class LiftLog extends BaseModel {

    @Column(name = "lift_id")
    private String liftId;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "message")
    private String message;

    @Column(name = "iced_id")
    private String icedId;

    public String getLiftId() {
        return liftId;
    }

    public void setLiftId(String liftId) {
        this.liftId = liftId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIcedId() {
        return icedId;
    }

    public void setIcedId(String icedId) {
        this.icedId = icedId;
    }
}
