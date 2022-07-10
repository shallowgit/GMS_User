package gms.cuit.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

//UsernamePasswordAuthenticationFilter.java 这个类是处理身份验证表单提交
//其中的方法 attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 就是获取用户名和密码的方法

// 可以在此方法内覆写尝试进行登录认证的逻辑，登录成功之后等操作不在此方法内
// 如果使用此过滤器来触发登录认证流程，注意登录请求数据格式的问题
// 此过滤器的用户名密码默认从request.getParameter()获取，但是这种
// 读取方式不能读取到如 application/json 等 post 请求数据，需要把
// 用户名密码的读取逻辑修改为到流中读取request.getInputStream()

//创建filter,将spring security中获得用户名密码的方式改为json获取
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if(!request.getMethod().equals("POST")){
            throw new AuthenticationServiceException(
                    "Authentication method not supported" + request.getMethod()
            );
        }
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            String username = null;
            String password = null;
            try{
                Map<String,String> map = new ObjectMapper().readValue(request.getInputStream(),Map.class);
                username = map.get("username");
                password = map.get("password");

            }catch (Exception e){
                e.printStackTrace();
            }
            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }

            username = username.trim();

            //UsernamePasswordAuthenticationToken继承AbstractAuthenticationToken实现Authentication
            //所以当在页面中输入用户名和密码之后首先会进入到UsernamePasswordAuthenticationToken验证(Authentication)，
            //然后生成的Authentication会被交由AuthenticationManager来进行管理
            //而AuthenticationManager管理一系列的AuthenticationProvider，
            //而每一个Provider都会通UserDetailsService和UserDetail来返回一个
            //以UsernamePasswordAuthenticationToken实现的带用户名和密码以及权限的Authentication
            //封装用户名密码
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
        else {
            return super.attemptAuthentication(request, response);
        }
    }
}

