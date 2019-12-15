package com.pepper.model.emap.event;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_event_dispatch")
@DynamicUpdate(true)
public class EventDispatch extends BaseModel{

	/**
	 *
	 */
	private static final long serialVersionUID = 8509500080591176473L;

	@Column(name ="task_id")
	private String taskId;

	@Column(name ="event_id")
	private String eventId;

	@Column(name ="event_list_id",nullable=false)
	private String eventListId;
	
	@Column(name ="department_id")
	private String departmentId;

	@Column(name ="handle_time")
	private Date handleTime;

	@Column(name ="operator",nullable=false)
	private String operator;

	@Column(name ="dispatch_from")
	private String dispatchFrom;

	@Column(name ="title")
	private String title;

	@Column(name ="content")
	private String content;

	@Column(name ="assign_date")
	private Date assignDate;

    @Column(name ="image1")
    private String image1;

    @Column(name ="image2")
    private String image2;

    @Column(name ="image3")
    private String image3;

    @Column(name ="voice1")
    private String voice1;

    @Column(name ="video")
    private String video;
    
    @Column(name ="transfer_remarks")
    private String transferRemarks;
    
    @Column(name ="is_employee_transfer")
    private Boolean isEmployeeTransfer;
    
    public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public Boolean getIsEmployeeTransfer() {
		return isEmployeeTransfer;
	}

	public void setIsEmployeeTransfer(Boolean isEmployeeTransfer) {
		this.isEmployeeTransfer = isEmployeeTransfer;
	}

	public String getTransferRemarks() {
		return transferRemarks;
	}

	public void setTransferRemarks(String transferRemarks) {
		this.transferRemarks = transferRemarks;
	}

	public void setImage1 ( String image1 ) {
        this.image1 = image1;
    }

    public void setImage2 ( String image2 ) {
        this.image2 = image2;
    }

    public void setImage3 ( String image3 ) {
        this.image3 = image3;
    }

    public void setVoice1 ( String voice1 ) {
        this.voice1 = voice1;
    }

    public void setVideo ( String video ) {
        this.video = video;
    }

    public String getImage1 ( ) {
        return image1;
    }

    public String getImage2 ( ) {
        return image2;
    }

    public String getImage3 ( ) {
        return image3;
    }

    public String getVoice1 ( ) {
        return voice1;
    }

    public String getVideo ( ) {
        return video;
    }

    public EventDispatch ( ) {
    }

    public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Date getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(Date handleTime) {
		this.handleTime = handleTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getDispatchFrom() {
		return dispatchFrom;
	}

	public void setDispatchFrom(String dispatchFrom) {
		this.dispatchFrom = dispatchFrom;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEventListId() {
		return eventListId;
	}

	public void setEventListId(String eventListId) {
		this.eventListId = eventListId;
	}

	public Date getAssignDate() {
		return assignDate;
	}

	public void setAssignDate(Date assignDate) {
		this.assignDate = assignDate;
	}

}
