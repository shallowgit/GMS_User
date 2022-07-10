package gms.cuit.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

//我们是将消息发送到exchange，然后由于exchange与某个routingKey绑定路由到某个队列queue
//故而当消息到达exchange后，将自然而然的被路由到指定的queue中，等待被监听消费。
/**
 * 通用化 Rabbitmq 配置
 */
//自动注入RabbitMQ一些组件的配置
//包括其“单一实例消费者”配置、“多实例消费者”配置以及用于发送消息的操作组件实例“RabbitTemplate”的配置
@Configuration
public class RabbitmqConfig {

    @Autowired
    private Environment env;

    //使用CachingConnectionFactory 作为连接工厂
    //在spring-rabbit中，管理消息协商器(broker)连接的核心组件是ConnectionFactory这个接口。
    //ConnectionFactory提供了
    //org.springframework.amqp.rabbit.connection.Connection(com.rabbitmq.client.Connection的包装类)实例的连接与管理。
    //而CachingConnectionFactory是ConnectionFactory的在Spring AMQP中唯一实现，它创建一个连接代理，使程序可以共享的连接。
    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 单一消费者
     *
     * @return
     */
    //这个bean只会在consumer端通过@RabbitListener注解的方式接收消息的时候使用
    //每个@RabbitListener注解方法都会由RabbitListenerContainerFactory创建一个MessageListenerContainer，负责接收消息。
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);//设置spring-amqp的ConnectionFactory
        //配置了MessageConverter在发送和接收消息时候就能自动完成Message和自定义java对象类的自动转换
        //默认采用的是SimpleMessageConverter他就直接将java对象序列化。
        //但是并不推荐直接使用，因为会只限于java平台。
        //推荐使用JsonMessageConverter、Jackson2JsonMessageConverter，
        //这两个是都将java对象转化为json再转为byte[]来构造Message对象，
        //前一个用的是jackson json lib，后一个用的是jackson 2 json lib。
        factory.setMessageConverter(new Jackson2JsonMessageConverter());//消息序列化类型
        factory.setConcurrentConsumers(1);//默认消费者数量
        factory.setMaxConcurrentConsumers(1);//最大消费者数量
        factory.setPrefetchCount(1);//每次给消费者发送的消息数量
        factory.setTxSize(1);//设置事务当中可以处理的消息数量
        return factory;
    }

    /**
     * 多个消费者
     *
     * @return
     */
    //@Bean 注解是 Spring 容器中自带的注解，其作用就是将我们应用程序中的类，或者方法来注入到 Spring 容器中
    //作为 Spring 配置的一部分。
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //确认消费模式-NONE
        //NONE：无应答，rabbitmq默认consumer正确处理所有请求。
        //AcknowledgeMode.NONE：自动确认
        //AcknowledgeMode.AUTO：根据情况确认
        //AcknowledgeMode.MANUAL：手动确认
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        //int.class是指定返回值的类型
        factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.concurrency", int.class));
        factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.max-concurrency", int.class));
        factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.simple.prefetch", int.class));
        return factory;
    }

    //确认消息发送配置
    @Bean
    public RabbitTemplate rabbitTemplate() {
        connectionFactory.setPublisherConfirms(true);// 开启消息发送至 RabbitMQ 的回调
        connectionFactory.setPublisherReturns(true);// 开启消息发送至队列失败的回调
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 为 true 时，消息通过交换器无法匹配到队列时会返回给生产者，为 false 时，匹配不到会直接丢弃
        rabbitTemplate.setMandatory(true);
        // 设置消息从生产者发送至 rabbitmq broker 成功的回调 （保证信息到达 broker）
        //confirm-callback回调是检验消息是否达到交换机的一个机制
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("消息发送成功");
            }
        });
        // 设置信息从交换机发送至 queue 失败的回调
        //Return-Callback是校验消息是否到达队列的一个机制
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("消息丢失");
            }
        });
        return rabbitTemplate;
    }

    //准备开始使用RabbitMQ实现消息的发送和接收
    //首先，需要在RabbitmqConfig配置类中创建队列、交换机、路由以及绑定等Bean组件

    //采用路由模式
    //往directExchange交换机中发送消息，使用direct.a.key作为路由规则。
    //生产者：rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, "direct.a.key", context);
    //topic.A,topic.B两个队列都绑定了交换机directExchange，但他们的路由规则不同，
    //a队列用了direct.a.key,b队列用了direct.b.key，
    //这种情况下，生产者使用direct.a.key作为路由规则，就只有a队列能收到消息，b队列则收不到消息。
    //如果a，b队列都关联了direct.a.key路由规则，则上面的生产者发送消息时，a，b队列都能收到消息，
    //这样就有类似fanout交换机的功能了。

    //异步发送订单成功的邮箱通知的消息模型
    //创建队列
    @Bean
    public Queue successEmailQueue() {
        //创建并返回队列实例
        return new Queue(env.getProperty("order.booking.success.email.queue"), true);
    }

    //创建一个topic交换机
    //接收消息,按特定的策略转发到 Queue 进行存储。类似交换机,将各个消息分发到相应的队列中。
    //根据消息携带的路由键(routing key)将消息投递给对应队列
    //durable:
    //是否持久化,设为true表示持久化,将交换器存盘,服务器重启不会丢信息.
    //autoDelete:
    //是否自动删除,设为TRUE则自动删除,自删除前提是至少有一个队列或者交换器与这交换器绑定,
    //之后所有与这个交换器绑定的队列或者交换器都解绑,一般设为false.

    //创建交换机
    @Bean
    public TopicExchange successEmailExchange() {
        //创建并返回交换机实例
        return new TopicExchange(env.getProperty("order.booking.success.email.exchange"), true, false);
    }

    //用fluent API的方式将Queue绑定到DirectExchange
    //可以绑定多个队列，这里绑定一个

    //创建绑定
    @Bean
    public Binding successEmailBinding() {
        //创建并返回队列交换机和路由的绑定实例
        return BindingBuilder.bind(successEmailQueue()).to(successEmailExchange()).with(env.getProperty("order.booking.success.email.routing.key"));
    }

    //异步发送注册成功的邮箱通知的消息模型
    @Bean
    public Queue successRegisterQueue() {
        return new Queue(env.getProperty("register.success.email.queue"), true);
    }

    @Bean
    public TopicExchange successRegisterExchange() {
        return new TopicExchange(env.getProperty("register.success.email.exchange"), true, false);
    }

    @Bean
    public Binding successRegisterBinding() {
        return BindingBuilder.bind(successRegisterQueue()).to(successRegisterExchange()).with(env.getProperty("register.success.email.routing.key"));
    }
}
