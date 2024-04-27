package com.lily.lilyojquestionservice.rabbitmq;

import com.lily.lilyojcommon.constant.MqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class QuestionSubmitProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message){
        rabbitTemplate.convertAndSend(MqConstant.QUESTION_EXCHANGE, MqConstant.QUESTION_ROUTING_KEY, message);
    }
}