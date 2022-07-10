package gms.cuit.mapper;


import gms.cuit.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    //查询场馆所有通知
    List<Gms_Notice> findAllNotice();

    //查询场馆类别
    List<Gms_Type> findAllType();

    //通过日期和场馆名查询
    List<Gms_Vdstate> finAllVenueByTypeAndDate(String venueName,String currentDate);

    //根据场馆id获取场馆信息
    Gms_Venue findVenueById(String id);

    //插入订单
    int addOrder(Gms_Order order);

    //获取当前场馆状态(可预约和已被预约)
    Integer getOrderState(String Venue_Id, String date,Integer st,Integer ed);

    //获取分时状态
    Gms_Vdstate findVdstate(String venue_id, String date);

    //保存修改场馆分时状态
    Integer saveVdstate(@Param("vdstate") String vdstate, @Param("venue_Id")String venue_Id, @Param("date") String date);

    //根据noticeid查询通知消息
    Gms_Notice getNoticeById(String noticeId);

    //个人中心部分
    //获取订单个数
    Integer getCount(String user_Id,String query_key);

    //获取包含指定关键字的某页订单
    List<Gms_Order> findProductByPage(String user_Id,int index,int currentCount,String query_key);

    //通过id修改订单状态
    void delOrderById(String id);

    //登录部分
    //用户登录
    Gms_User doLogin(String id,String password);

    //根据id获取用户
    Gms_User finUserById(String id);

    //修改密码
    void updatePassword(String user_id,String newpassword);

    //用户注册
    Integer register(Gms_User gms_user);
}
