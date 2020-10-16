package com.pepper.controller.emap.scoket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: mr.liu
 * @create: 2020-10-15 21:32
 **/
@Component
public class ChannelGroupUtil {

    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup("ChannelGroups", GlobalEventExecutor.INSTANCE);

    public static void add(Channel channel) {
        CHANNEL_GROUP.add(channel);
    }

    public static ChannelGroupFuture broadcast(Object msg) {
        return CHANNEL_GROUP.writeAndFlush(msg);
    }

    public static ChannelGroupFuture broadcast(Object msg, ChannelMatcher matcher) {
        return CHANNEL_GROUP.writeAndFlush(msg, matcher);
    }

    public static ChannelGroup flush() {
        return CHANNEL_GROUP.flush();
    }

    public static boolean discard(Channel channel) {
        return CHANNEL_GROUP.remove(channel);
    }

    public static Channel find(ChannelId channelId) {
        return CHANNEL_GROUP.find(channelId);
    }
}
