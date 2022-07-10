package gms.cuit.service;

import gms.cuit.entity.Gms_Order;
import gms.cuit.entity.Gms_User;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ发送邮件服务
 * @author story
 * @date 2020/5/23
 */
@Service
public class RabbitSenderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    /**
     * 订单成功异步发送邮件通知消息
     */
    //在通用的消息发送服务类 RabbitSenderService 中写一段发送消息的方法
    //该方法用于接收“订单编号”参数，然后在数据库中查询其对应的详细订单记录
    //将该记录充当“消息”并发送至RabbitMQ的队列中，等待被监听消费
    public void sendOrderSuccessEmailMsg(Gms_Order gms_order){
        try {
            if (gms_order!=null){
                //TODO:rabbitmq发送消息的逻辑
                // 设置消息在传输中的格式，在这里采用JSON的格式进行传输
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                rabbitTemplate.setExchange(env.getProperty("order.booking.success.email.exchange"));
                rabbitTemplate.setRoutingKey(env.getProperty("order.booking.success.email.routing.key"));
                //TODO：将info充当消息发送至队列
                //convertAndSend()  自动 Java 对象包装成 Message 对象，Java 对象需要实现 Serializable 序列化接口
                //convertAndSend() 方法，用于将原始消息进行转换，并且将转换过后的消息发送到 RabbitMQ Server 中
                //第2个参数是消息发送成功后的监听器，对于这个监听器，这里采用了 new MessagePostProcessor 的匿名内部类的形式进行实现，
                //要添加 MessagePostProcessor 消息监听器，需要重写 postProcessMessage 方法，即消息发送成功后的方法，
                //在该方法中，我们可以对消息发送成功后进行进一步的设置，最后将设置好的 Message 进行返回
                rabbitTemplate.convertAndSend(gms_order, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //MessageProperties 实例，用于对消息的 properties 参数进行描述
                        // 获取消息的属性
                        MessageProperties messageProperties = message.getMessageProperties();
                        //设置消息是否持久化。Persistent表示持久化，Non-persistent表示不持久化
                        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        // 设置消息的类型（在这里指定消息类型为gms_order类型）
                        messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, Gms_Order.class);
                        //返回消息实例
                        return message;
                    }
                });
            }
        }catch (Exception e){
            System.out.println("邮件通知-发送消息-发生异常");
            e.printStackTrace();
        }
    }
    public void sendRegisterSuccessEmailMsg(Gms_User gms_user){
        try {
            if (gms_user!=null){
                //TODO:rabbitmq发送消息的逻辑
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                rabbitTemplate.setExchange(env.getProperty("register.success.email.exchange"));
                rabbitTemplate.setRoutingKey(env.getProperty("register.success.email.routing.key"));
                //TODO：将info充当消息发送至队列
                //convertAndSend()  自动 Java 对象包装成 Message 对象，Java 对象需要实现 Serializable 序列化接口
                rabbitTemplate.convertAndSend(gms_user, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        MessageProperties messageProperties = message.getMessageProperties();
                        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, Gms_User.class);
                        return message;
                    }
                });
            }
        }catch (Exception e){
            System.out.println("邮件通知-发送消息-发生异常");
            e.printStackTrace();
        }
    }
}
