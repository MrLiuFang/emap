package com.pepper.service.emap.event;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.EventDispatch;

public interface EventDispatchService extends BaseService<EventDispatch> {

	public EventDispatch findEventDispatch(String eventId,String operator);
}
