package com.thimbleware.jmemcached.protocol.text;

import com.thimbleware.jmemcached.Cache;
import com.thimbleware.jmemcached.protocol.MemcachedCommandHandler;
import com.thimbleware.jmemcached.protocol.SessionStatus;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 */
public class MemcachedPipelineFactory implements ChannelPipelineFactory {

    private Cache cache;
    private String version;
    private boolean verbose;
    private int idleTime;

    private int frameSize;
    private DefaultChannelGroup channelGroup;
    private final MemcachedResponseEncoder memcachedResponseEncoder = new MemcachedResponseEncoder();

    private final StringEncoder stringEncoder = new StringEncoder();
    private final MemcachedCommandHandler memcachedCommandHandler;

    public MemcachedPipelineFactory(Cache cache, String version, boolean verbose, int idleTime, int frameSize, DefaultChannelGroup channelGroup) {
        this.cache = cache;
        this.version = version;
        this.verbose = verbose;
        this.idleTime = idleTime;
        this.frameSize = frameSize;
        this.channelGroup = channelGroup;
        memcachedCommandHandler = new MemcachedCommandHandler(this.cache, this.version, this.verbose, this.idleTime, this.channelGroup);
    }

    public ChannelPipeline getPipeline() throws Exception {
        SessionStatus status = new SessionStatus().ready();

        return Channels.pipeline(
                new MemcachedFrameDecoder(status, frameSize),
                new MemcachedCommandDecoder(status),
                memcachedCommandHandler,
                memcachedResponseEncoder,
                stringEncoder);
    }



}
