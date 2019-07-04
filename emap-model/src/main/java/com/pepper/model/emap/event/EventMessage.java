package com.pepper.model.emap.event;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_event_message")
@DynamicUpdate(true)
public class EventMessage extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2896749360306688047L;
	
	private String message;
	
	private String title;
	
	private String userName;
	
	private String userId;
	
	private String eventListId;
	
	private String eventId;
	
	private String mobile;
	
	private String email;
	
	private Integer type;
	
	private Boolean isSend;
	

}
