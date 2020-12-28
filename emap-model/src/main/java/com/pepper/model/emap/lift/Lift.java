package com.pepper.model.emap.lift;

import com.pepper.core.base.BaseModel;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity()
@Table(name = "t_lift")
@DynamicUpdate(true)
public class Lift extends BaseModel {

    @Column(name = "name")
    private String name;

    @Column (name = "ip")
    private String ip;

    @Column (name = "uart_id")
    private Integer uartId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getUartId() {
        return uartId;
    }

    public void setUartId(Integer uartId) {
        this.uartId = uartId;
    }
}
