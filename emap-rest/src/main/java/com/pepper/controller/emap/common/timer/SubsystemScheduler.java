package com.pepper.controller.emap.common.timer;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pepper.model.emap.subsystem.Subsystem;
import com.pepper.service.emap.subsystem.SubsystemService;

@Component
@Order(value=Ordered.LOWEST_PRECEDENCE)
public class SubsystemScheduler {
	
	@Reference
	private SubsystemService subsystemService;

	@Scheduled(fixedRate = 60000)
	public void scheduled() {
		List<Subsystem> list = subsystemService.findAll();
		for(Subsystem subsystem : list) {
			try {
				if(Objects.isNull(subsystem.getAddress())||Objects.isNull(subsystem.getProt())) {
					continue;
				}
				Socket socket = new Socket(subsystem.getAddress(), subsystem.getProt());
				if(socket.isConnected()) {
					subsystem.setIsOnLine(true);
				}else {
					subsystem.setIsOnLine(false);
				}
				subsystemService.update(subsystem);
			} catch (IOException e) {
			}
			
		}
	}
}
