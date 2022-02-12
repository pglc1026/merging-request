package com.springboot.sample.bean;

import java.time.LocalDateTime;

/**
 * @description: 消息统一封装类
 * @author: smalljop
 * @create: 2020-01-03 10:20
 **/
public class Message {
    /**
     * 消息唯一标识
     */
    private String id;
    /**
     * 消息渠道 如 订单 支付 代表不同业务类型
     * 为消费时不同类去处理
     */
    private String channel;
    /**
     * 具体消息 json
     */
    private String body;

    /**
     * 延时时间 被消费时间  取当前时间戳 延迟时间
     */
    private Long delayTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", channel='" + channel + '\'' +
                ", body='" + body + '\'' +
                ", delayTime=" + delayTime +
                ", createTime=" + createTime +
                '}';
    }
}
