package gms.cuit.service;

import gms.cuit.entity.Gms_User;
import gms.cuit.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class UsernamePasswordUtilsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (userid == null) {
            throw new UsernameNotFoundException("用户id不存在!");
        }
        Gms_User user = userMapper.finUserById(userid);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在!");
        }

        //添加权限
        authorities.add(new SimpleGrantedAuthority("ROLE_user"));
        return new User(user.getUser_Id(), user.getUser_Password(), authorities);
    }

    //Spring Security使用一个Authentication对象来描述当前用户的相关信息。
    //SecurityContextHolder中持有的是当前用户的SecurityContext，
    //而SecurityContext持有的是代表当前用户相关信息的Authentication的引用。
    //这个Authentication对象不需要我们自己去创建，在与系统交互的过程中，
    //Spring Security会自动为我们创建相应的Authentication对象，然后赋值给当前的SecurityContext。
    //但是往往我们需要在程序中获取当前用户的相关信息，比如最常见的是获取当前登录用户的用户名。
    //在程序的任何地方，通过如下方式我们可以获取到当前用户的用户。
/*    public User getCurrentUser(){
        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }*/
    //通过Authentication.getPrincipal()可以获取到代表当前用户的信息，这个对象通常是UserDetails的实例。
    //获取当前用户的用户名是一种比较常见的需求。
    //此外，调用SecurityContextHolder.getContext()获取SecurityContext时，
    //如果对应的SecurityContext不存在，则Spring Security将为我们建立一个空的SecurityContext并进行返回。

    //获取用户 下订单的时候需要获取user对象，如果只是用户名这里可以不用请求数据库
    public Gms_User getSession() {
        String userid = SecurityContextHolder.getContext().getAuthentication().getName();
        Gms_User user = userMapper.finUserById(userid);
        return user;
    }
}
