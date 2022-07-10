package gms.cuit.service;

import gms.cuit.dto.MailDto;
import gms.cuit.entity.Gms_Order;
import gms.cuit.entity.Gms_User;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * RabbitMQ接收消息服务
 *
 * @author story
 * @date 2020/5/23
 */
//在通用的消息接收服务类RabbitReceiverService中实现消息的接收
@Service
public class RabbitReceiverService {

    @Autowired
    private MailService mailService;

    @Autowired
    private Environment env;

    /**
     * 异步邮件通知-接收消息
     */
    //我们可以在spring容器中构建SimpleMessageListenerContainer来消费消息，
    //我们也可以使用@RabbitListener来消费消息
    //@RabbitListener注解指定目标方法来作为消费消息的方法，通过注解参数指定所监听的队列或者Binding。
    //使用@RabbitListener可以设置一个自己明确默认值的RabbitListenerContainerFactory对象。
    //@RabbitListener注解的containerFactory属性可以指定一个RabbitListenerContainerFactory的bean，
    //默认是找名字为rabbitListenerContainerFactory的实例
    @RabbitListener(queues = {"${order.booking.success.email.queue}"}, containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(Gms_Order order) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = format.format(order.getOrder_Date());
            //TODO:真正的发送邮件....
            //第一个参数是格式，自动把第一个参数以后的参数值填入到%s中去
            final String content = String.format(env.getProperty("mail.order.item.success.content"), order.getOrder_Venue().getVenue_Name(), currentDate + " " + order.getOrder_St() + ":00-" + order.getOrder_Ed() + ":00");
            MailDto dto = new MailDto(env.getProperty("mail.order.item.success.subject"), content, new String[]{order.getOrder_User().getUser_Email()});
            mailService.sendHTMLMail(dto);
        } catch (Exception e) {
            System.out.println("邮件通知-接收消息-发生异常");
        }
    }

    @RabbitListener(queues = {"${register.success.email.queue}"}, containerFactory = "singleListenerContainer")
    public void consumeRegisterMsg(Gms_User gms_user) {
        try {
            //TODO:真正的发送邮件....
            final String content = String.format(env.getProperty("mail.register.item.success.content"), gms_user.getUser_Password());
            MailDto dto = new MailDto(env.getProperty("mail.register.item.success.subject"), content, new String[]{gms_user.getUser_Email()});
            mailService.sendHTMLMail(dto);
            System.out.println("注册邮件发送成功");
        } catch (Exception e) {
            System.out.println("邮件通知-接收消息-发生异常");
        }
    }
}

