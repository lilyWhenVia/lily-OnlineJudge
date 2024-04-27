package com.lily.lilyojjudgeservice.rabbitmq;

import com.lily.lilyojcommon.constant.MqConstant;
import com.lily.lilyojjudgeservice.service.JudgeService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;

/**
 * Created by lily via on 2024/4/27 22:58
 */
@Component
@Slf4j
public class MessageConsumer {

    @Resource
    private JudgeService judgeService;

    /**
     * 定义了接收消息后的处理流程
     * 监听器指定了要监听的队列，ack模式为手动确认
     *
     * @param channel     用于处理返回消息接收状态
     * @param message     图表存入数据库后主键id
     * @param deliveryTag 消息体唯一标签
     */
    @RabbitListener(queues = {MqConstant.QUESTION_QUEUE}, concurrency = "4")
    public void receiveMessage(Channel channel, @NotEmpty String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        // 校验message
        long questionSubmitId = Long.parseLong(message);
        log.info("receive a message {}", questionSubmitId);

        // ...业务代码处理消息...
        judgeService.doJudge(questionSubmitId);

        // 手动确认消息接收情况
        try {
            // 手动确认消息已经被消费， false为不批量确认
            channel.basicAck(deliveryTag, false);
            log.info("message {}: ack succeed", message);
        } catch (IOException e) {
            log.error("message {}: ack failed", message);
            throw new RuntimeException(e);
        }
    }
}

