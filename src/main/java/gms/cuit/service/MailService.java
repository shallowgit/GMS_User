package gms.cuit.service;

import gms.cuit.dto.MailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * @author story
 * @date 2020/5/22
 */
@Service
public class MailService {

    //邮件发送实例
    @Autowired
    private JavaMailSender mailSender;

    //Environment继承了PropertyResolver
    //PropertyResolver用于解析properties，针对于任何底层资源的接口
    @Autowired
    private Environment env;

    //解决了邮件附件文件名过长不能正常显示读取、中文附件乱码的问题
    static {
        System.setProperty("mail.mime.splitlongparameters", "false");
    }

    /**
     * 发送简单文本文件
     */
    @Async
    public void sendSimpleEmail(final MailDto dto){
        try {
            // 构建一个邮件对象
            SimpleMailMessage message=new SimpleMailMessage();
            // 设置邮件发送者，这个跟application.properties中设置的要一致
            message.setFrom(env.getProperty("mail.send.from"));//根据Key获取值
            // 设置邮件接收者，可以有多个接收者，中间用逗号隔开，以下类似
            // message.setTo("1*****@qq.com","2*****qq.com");
            message.setTo(dto.getTos());
            // 设置邮件主题
            message.setSubject(dto.getSubject());
            // 设置邮件的正文
            message.setText(dto.getContent());
            // 发送邮件
            mailSender.send(message);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送带html邮件
     * @param dto
     */
    @Async
    public void sendHTMLMail(final MailDto dto){
        try {
            MimeMessage message=mailSender.createMimeMessage();
            //如果要支持内联元素(HTML)和附件就必须给定第二个参数为true
            MimeMessageHelper messageHelper=new MimeMessageHelper(message,true,"utf-8");
            messageHelper.setFrom(env.getProperty("mail.send.from"));
            messageHelper.setTo(dto.getTos());
            messageHelper.setSubject(dto.getSubject());
            messageHelper.setText(dto.getContent(),true);//true代表支持html，邮件内容
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
