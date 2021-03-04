package com.pepper.service.emap.event.impl;

import java.nio.channels.Channel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;

import com.alibaba.druid.sql.visitor.functions.If;
import com.pepper.dao.emap.node.NodeDao;
import com.pepper.dao.emap.node.NodeGroupDao;
import com.pepper.model.emap.event.EventListGroup;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeGroup;
import com.pepper.service.emap.event.EventListGroupService;
import com.pepper.service.emap.node.NodeService;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.EventListDao;
import com.pepper.model.emap.event.EventList;
import com.pepper.service.emap.event.EventListService;
import org.springframework.util.StringUtils;

@Service(interfaceClass=EventListService.class)
public class EventListServiceImpl extends BaseServiceImpl<EventList> implements EventListService {
	
	@Resource
	private EventListDao eventListDao;

	@Resource
	private NodeGroupDao nodeGroupDao;

	@Resource
	private NodeDao nodeDao;

	@Resource
	private NodeService nodeService;

	@Reference
	private EventListGroupService eventListGroupService;

	@Override
	public List<EventList> findByStatusOrStatus(String status, String status1) {
		return eventListDao.findByStatusOrStatus(status, status1);
	}

	@Override
	public List<EventList> findByStatusNot(String status) {
		return eventListDao.findByStatusNot(status);
	}

	@Override
	public Pager<EventList> List(Pager<EventList> pager, Boolean isUrgent) {
		return eventListDao.List(pager,isUrgent);
	}

	@Override
	public Pager<EventList> findBySourceCodeAndIdNot(String sourceCode, String id,Pager<EventList> pager) {
		Pageable pageable =PageRequest.of(pager.getPageNo()-1, pager.getPageSize());
		Page<EventList> page = eventListDao.findBySourceCodeAndIdNotAndWarningLevelNot(sourceCode, id,0,pageable);
		pager.setResults(page.getContent());
		pager.setTotalRow(Long.valueOf(page.getTotalElements()));
		return pager;
	}

	@Override
	public void handover(String handoverUserId, String currentUserId) {
		eventListDao.handover(handoverUserId, currentUserId);
	}

	@Override
	public Pager<EventList> transferList(Pager<EventList> pager, String dispatchFrom,String eventId,Boolean isUrgent,String nodeName,String eventName,Date startDate,Date endDate) {
		return eventListDao.transferList(pager, dispatchFrom, eventId, isUrgent, nodeName, eventName, startDate, endDate);
	}

	@Override
	public Pager<EventList> doorAttendance(Pager<EventList> pager, String eventListId,String nodeId, Date startDate, Date endDate,
			String staffId) {
		return eventListDao.doorAttendance(pager, eventListId,nodeId, startDate, endDate, staffId);
	}

	@Override
	public Pager<EventList> assistEventList(Pager<EventList> pager, String userId,Boolean isFinish) {
		return eventListDao.assistEventList(pager, userId,isFinish);
	}

	@Override
	public Pager<EventList> historyEventList(Pager<EventList> pager, String event, Integer warningLevel, String node,
			String nodeType, String mapName, String buildName, String stieName, String operator, String status,String eventId,Date startDate,Date endDate, String departmentId) {
		return eventListDao.historyEventList(pager,startDate,endDate, event, warningLevel, node, nodeType, mapName, buildName, stieName, operator, status,null,null,null,null,null,eventId,departmentId);
	}

	@Override
	public Pager<EventList> report(Pager<EventList> pager, Date eventStartDate, Date eventEndDate, String event,
			Integer warningLevel, String node, String nodeTypeId, String mapName, String buildName, String siteName,
			String operatorId, String status, String employeeId,Boolean isOrder,String sortBy,Boolean isSpecial,Boolean isUrgent) {
		return eventListDao.historyEventList(pager, eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId, isOrder,sortBy,isSpecial,isUrgent,null,null);
	}

	@Override
	public EventList findOneByNodeId(String nodeId) {
		return eventListDao.findFirstByNodeId(nodeId);
	}

	@Override
	public Pager<EventList> appList(Pager<EventList> pager, Boolean isFinish, Boolean isUrgent, String eventId, String nodeName,
			String eventName, Date startDate, Date endDate,String currentHandleUser) {
		return eventListDao.appList(pager,isFinish, isUrgent, eventId, nodeName, eventName, startDate, endDate,currentHandleUser);
	}

	@Override
	public Integer toMeNoFiledCount(String userId) {
		return this.eventListDao.countByCurrentHandleUserAndStatusNot(userId,"P");
	}

	@Override
	public Integer todaySpecialCount(Date startDate, Date endDate) {
		return eventListDao.todaySpecialCount(startDate,endDate);
	}

	@Override
	public Integer todayUrgentCount(Date startDate, Date endDate) {
		return this.eventListDao.todayUrgentCount(startDate,endDate);
	}

	@Override
	public Integer todayOrdinaryCount(Date startDate, Date endDate) {
		return this.eventListDao.todayOrdinaryCount(startDate,endDate);
	}

	@Override
	public List<Map<String, Object>> currentMonthCount(Date startDate, Date endDate) {
		return eventListDao.currentMonthCount(startDate,endDate);
	}

	@Override
	public List<Map<String, Object>> currentMonthCount(Date startDate, Date endDate, Boolean isConsole) {
		return eventListDao.currentMonthIsConsoleCount(startDate,endDate);
	}

	@Override
	public List<Map<String, Object>> currentMonthNotRelationNode(Date startDate, Date endDate) {
		return eventListDao.currentMonthNotRelationNode(startDate, endDate);
	}

	@Override
	public List<Map<String, Object>> yearTypeCount(String where, Date startDate, Date endDate) {
		return eventListDao.yearTypeCount(where,startDate,endDate);
	}

	@Override
	public int delete(Date createDate) {
		return eventListDao.deleteByCreateDateLessThanEqual(createDate);
	}

	@Override
	public void otherTreatment(EventList eventList) {
		Node node = nodeDao.findFirstBySourceCode(eventList.getSourceCode());
		List<NodeGroup> list = nodeGroupDao.findAllByNodeId(node.getId());
		String eventGroupId = UUID.randomUUID().toString();
		AtomicReference<String> nodeGroupCode= new AtomicReference<>("");
		list.forEach(nodeGroup -> {
			Optional<Node> optional = nodeDao.findById(nodeGroup.getNodeId());
			if (optional.isPresent() && !Objects.equals(nodeGroup.getNodeId(),node.getId())){
				Node node1 = optional.get();
				EventList eventList1 = new EventList();
				eventList1.setMaster(false);
				eventList1.setSourceCode(node1.getSourceCode());
				eventList1.setWarningLevel(-1);
				eventList1.setEventName("关联事件");
				eventList1.setStatus("W");
				eventList1.setOperator("2c92b9ad70710b0b017089c0d8dc047d");
				eventList1 = this.save(eventList1);
				nodeGroupCode.set(nodeGroup.getCode());
				saveEventListGroup(eventList1.getId(),eventList1.getWarningLevel(),false,eventGroupId,node1.getId(),nodeGroup.getCode());
				if (Objects.nonNull(node1.getOut()) && node1.getOut()) {
					try {
						sendTcp(node1,true);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				updateNodeStatus(node1);
			}
		});
		updateNodeStatus(node);
		saveEventListGroup(eventList.getId(),eventList.getWarningLevel(),true,eventGroupId,node.getId(),nodeGroupCode.get());
	}

	public void sendTcp(Node node,Boolean outIsOn) throws InterruptedException {
		String cmd0 = "000100000008010F006400040100";
		String cmd1 = "000100000008010F006400040101";
		String cmd2 = "000100000008010F006400040102";
		String cmd3 = "000100000008010F006400040103";
		String cmd = "";
		if (Objects.isNull(node.getPort()) || !StringUtils.hasText(node.getOutIp())) {
			return;
		}
		Node node1 = nodeService.findFirstByIpAndPortAndIdNot(node.getOutIp(), node.getPort(), node.getId());
		if (Objects.nonNull(node.getOutPort()) && node.getOutPort() == 1) {
			if (Objects.nonNull(node1) && Objects.isNull(node1.getOutIsOn()) && node1.getOutIsOn()) {
				cmd = cmd3;
			} else {
				cmd = cmd1;
			}
		} else if (Objects.nonNull(node.getOutPort()) && node.getOutPort() == 2) {
			if (Objects.nonNull(node1) && node1.getOutIsOn()) {
				cmd = cmd3;
			} else {
				cmd = cmd2;
			}
		}
		send(node,cmd);
		node.setOutIsOn(outIsOn);
		nodeService.update(node);
	}
	@Override
	public void send(Node node,String cmd) throws InterruptedException {
		try {
			System.out.println("开始发送TCP");
			String host = node.getOutIp();
			int port =node.getPort();
			System.out.println("host->"+host+":"+port);
			// 首先，netty通过ServerBootstrap启动服务端
			Bootstrap client = new Bootstrap();

			//第1步 定义线程组，处理读写和链接事件，没有了accept事件
			EventLoopGroup group = new NioEventLoopGroup();
			client.group(group );

			//第2步 绑定客户端通道
			client.channel(NioSocketChannel.class);

			//第3步 给NIoSocketChannel初始化handler， 处理读写事件
			client.handler(new ChannelInitializer<NioSocketChannel>() {  //通道是NioSocketChannel
				@Override
				protected void initChannel(NioSocketChannel ch) throws Exception {
					//字符串编码器，一定要加在SimpleClientHandler 的上面
					ch.pipeline().addLast(new StringEncoder());
					ch.pipeline().addLast(new DelimiterBasedFrameDecoder(
							Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
					//找到他的管道 增加他的handler
					ch.pipeline().addLast(new ClientHandler());
				}
			});

			//连接服务器
			ChannelFuture future = client.connect(host, port).sync();
			System.out.println("发送指令："+cmd);
			ByteBuf buff = Unpooled.buffer();
			// 对接需要16进制
			buff.writeBytes(ConvertCode.hexString2Bytes(cmd));
			future.channel().writeAndFlush(buff);
			group.shutdownGracefully();


//			Channel channel;
//			final EventLoopGroup group = new NioEventLoopGroup();
//
//			Bootstrap b = new Bootstrap();
//			b.group(group).channel(NioSocketChannel.class)  // 使用NioSocketChannel来作为连接用的channel类
//					.handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
//						@Override
//						public void initChannel(SocketChannel socketChannel) throws Exception {
//							System.out.println("正在连接中...");
//						ChannelPipeline pipeline = socketChannel.pipeline();
////						ByteBuf delimiter = Unpooled.copiedBuffer("\n".getBytes());
//						pipeline
////								.addLast(new DelimiterBasedFrameDecoder(1024,delimiter))
//								.addLast(new StringDecoder(Charset.forName("UTF-8")))
//								.addLast(new StringEncoder(Charset.forName("UTF-8")))
//								.addLast(new ClientHandler());
//
//						}
//					});
//			//发起异步连接请求，绑定连接端口和host信息
//			final ChannelFuture future = b.connect(host, port).sync();
//			String finalCmd = cmd;
//			future.addListener(new ChannelFutureListener() {
//				@Override
//				public void operationComplete(ChannelFuture arg0) throws Exception {
//					if (future.isSuccess()) {
//						System.out.println("连接服务器成功");
//						arg0.channel().writeAndFlush(finalCmd.getBytes());
////						group.shutdownGracefully();
//					} else {
//						System.out.println("连接服务器失败");
//						future.cause().printStackTrace();
//						group.shutdownGracefully(); //关闭线程组
//					}
//				}
//			});

		}catch (Exception ex){
		}
	}

	private void saveEventListGroup(String eventId,Integer warningLevel,Boolean isMaster,String eventGroupId,String nodeId,String nodeGroupCode){
		EventListGroup eventListGroup = new EventListGroup();
		eventListGroup.setEventId(eventId);
		eventListGroup.setLevel(warningLevel);
		eventListGroup.setIsMaster(isMaster);
		eventListGroup.setNodeGroupCode(nodeGroupCode);
		eventListGroup.setStatus("A");
		eventListGroup.setEventGroupId(eventGroupId);
		eventListGroup.setNodeId(nodeId);
		eventListGroupService.save(eventListGroup);
	}

	private void updateNodeStatus(Node node){
		node.setStatusUniversity(9);
		nodeService.update(node);
	}
}
class ClientHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("接受到server响应数据: " + msg);
	}
}