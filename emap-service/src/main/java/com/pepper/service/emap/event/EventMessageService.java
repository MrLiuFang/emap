package com.pepper.service.emap.event;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.EventMessage;

public interface EventMessageService extends BaseService<EventMessage> {

	public List<EventMessage> findEventMessage(String eventListId);
}
