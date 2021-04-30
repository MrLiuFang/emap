package com.pepper.controller.emap.common.timer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.subsystem.Subsystem;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.subsystem.SubsystemService;

@Component
@Order(value=Ordered.LOWEST_PRECEDENCE)
public class SubsystemScheduler {
	
	@Reference
	private SubsystemService subsystemService;

	@Autowired
	private EventListService eventListService;
	
	@Reference
	private NodeService nodeService;

	@Scheduled(fixedRate = 60000)
	public void scheduled() {
		List<Subsystem> list = subsystemService.findAll();
		for(Subsystem subsystem : list) {
			try {
				if(Objects.isNull(subsystem.getAddress())||Objects.isNull(subsystem.getProt())) {
					continue;
				}
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(subsystem.getAddress(),  subsystem.getProt()), 1000);//设置连接请求超时时间1 s
				if(socket.isConnected()) {
					subsystem.setIsOnLine(true);
					socket.close();
				}else {
					EventList eventList = eventListService.findFirstBySourceCodeAndStatusNot(subsystem.getNodeCode(),"P");
					if(Objects.isNull(eventList)) {
						if(Objects.nonNull(subsystem.getNodeCode())) {
							addEvent(subsystem.getNodeCode());
						}
					}
					subsystem.setIsOnLine(false);

				}
			} catch (IOException e) {
				EventList eventList = eventListService.findFirstBySourceCodeAndStatusNot(subsystem.getNodeCode(),"P");
				if(Objects.isNull(eventList)) {
					if(Objects.nonNull(subsystem.getNodeCode())) {
						addEvent(subsystem.getNodeCode());
					}
				}
				subsystem.setIsOnLine(false);
			}
			subsystemService.update(subsystem);
		}
	}
	
	private void addEvent(String sourceCode) {
		Node node = nodeService.findBySourceCode(sourceCode);
		if(Objects.nonNull(node)) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			EventList eventList = new EventList();
			eventList.setEventDate(formatter.format(new Date()));
			eventList.setSource(node.getSource());
			eventList.setSourceCode(node.getSourceCode());
			eventList.setWarningLevel(5);
			eventList.setStatus("W");
			eventList.setOperator("2c92b9ad70710b0b017089c0d8dc047d");
			eventList.setEventId(UUID.randomUUID().toString());
			eventList.setEventName("subsystem event");
			eventListService.save(eventList);
		}
	}
}
