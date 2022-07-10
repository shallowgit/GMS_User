package gms.cuit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gms.cuit.filter.CustomAuthenticationFilter;
import gms.cuit.service.UsernamePasswordUtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity //@EnableWebMvcSecurity 注解开启Spring Security的功能
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Autowired
    private UsernamePasswordUtilsService usernamePasswordUtilsService;

    //void configure(HttpSecurity http) 这个是我们使用最多的，用来配置 HttpSecurity
    //HttpSecurity 用于构建一个安全过滤器链 SecurityFilterChain
    //SecurityFilterChain 最终被注入核心过滤器。HttpSecurity 有许多我们需要的配置
    //我们可以通过它来进行自定义安全访问策略

    //授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //TODO:去个人中心和提交订单的路径需要认证
        http.authorizeRequests()//定义哪些URL须要被保护、哪些不须要被保护,配置路径拦截，表明路径访问所对应的权限，角色，认证信息
                //antMatcher("/api/**") 过滤掉非/api/开头的请求，如果不用antMatcher，所有请求都会进入
                //authenticated用户登录后可访问
                //.antMatchers("/user/center", "/user/confirmOrder").authenticated()
                .antMatchers("/user/center", "/user/confirmOrder","/user/index"
                        ,"/user/equipment","/user/equipmentLease","/user/notice").authenticated()
                // 剩下的其它请求,都是登录之后就能访问
                .anyRequest().permitAll()//anyRequest 匹配所有请求路径 permitAll 用户可以任意访问
                .and()//连接以上策略的连接器，用来组合安全策略。实际上就是"而且"的意思
                //对应表单认证相关的配置
                //指定支持基于表单的身份验证。如果未指定FormLoginConfigurer#loginPage(String)，
                //则将生成默认登录页面
                .formLogin()
                // usernameParameter就相当于你前端表单上面的name属性的值 默认为username
                .usernameParameter("username")//默认用户名，密码
                // passwordParameter就相当于你前端表单上面的name属性的值 默认为password
                .passwordParameter("password")
                .loginPage("/user/login")//登陆页面
                .loginProcessingUrl("/user/doLogin")//登陆请求处理接口
                .successHandler(new AuthenticationSuccessHandler() {//登录成功处理
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req,
                                                        HttpServletResponse resp,
                                                        Authentication authentication)//当前用户登陆信息
                            throws IOException, ServletException {
                        //将内容指定为JSON格式，以UTF-8字符编码进行编码
                        //告诉客户端（如浏览器）这是JSON格式传输的
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        Map<String, Object> map = new HashMap<>();
                        map.put("status", "2");
                        map.put("info", "登录成功啦~");
                        out.write(new ObjectMapper().writeValueAsString(map));//将map序列化为json，返回给调用者
                        out.flush();
                        out.close();
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {//登录失败的处理器
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req,
                                                        HttpServletResponse resp,
                                                        AuthenticationException e) //获取登陆失败原因
                            throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        Map<String, Object> map = new HashMap<>();
                        map.put("status", "1");
                        map.put("info", "登录失败啦~");
                        out.write(new ObjectMapper().writeValueAsString(map));
                        out.flush();
                        out.close();
                    }
                })
                .and()
                //添加退出登录支持。当使用WebSecurityConfigurerAdapter时，这将自动应用。
                //默认情况是，访问URL”/logout”，使HTTP Session无效来清除用户，
                //清除已配置的任何#rememberMe()身份验证，清除SecurityContextHolder，
                //然后重定向到”/login?success”
                .logout()
                .logoutUrl("/user/logout")//注销登陆请求url
                .logoutSuccessHandler(new LogoutSuccessHandler() {//注销成功处理
                    @Override
                    public void onLogoutSuccess(HttpServletRequest httpServletRequest,
                                                HttpServletResponse resp,
                                                Authentication authentication)// Authentication 保存了你刚刚登录成功的用户信息
                            throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        Map<String, Object> map = new HashMap<>();
                        map.put("info", "注销成功啦~");
                        out.write(new ObjectMapper().writeValueAsString(map));
                        out.flush();
                        out.close();
                    }
                })
                .and()
                //添加 CSRF 支持，使用WebSecurityConfigurerAdapter时，默认启用
                .csrf().disable()//表示关闭csrf（Cross-site request forgery）
                .rememberMe()//开启cookie保存用户数据 允许通过remember-me登录的用户访问
                .and()
                //在指定的Filter类的位置添加过滤器
                //UsernamePasswordAuthenticationFilter是登陆用户密码验证过滤器
                .addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    //AuthenticationManagerBuilder（身份验证管理生成器）
    //void configure(AuthenticationManagerBuilder auth) 用来配置认证管理器AuthenticationManager
    //说白了就是所有 UserDetails 相关的它都管，包含 PasswordEncoder 密码等

    //这段配置呢, 配置的是认证信息, AuthenticationManagerBuilder 这个类,
    //就是AuthenticationManager的建造者, 我们只需要向这个类中, 配置用户信息,
    //就能生成对应的AuthenticationManager, 这个类也提过,是用户身份的管理者, 是认证的入口,
    //因此,我们需要通过这个配置,向security提供真实的用户身份。

    // 这是AuthenticationManager的辅助构造方法

    //定义认证规则
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //usernamePasswordUtilsService就是自己写的类, 这个类的作用就是去获取用户信息,比如从数据库中获取。
        //这样的话,AuthenticationManager在认证用户身份信息的时候，就会从中获取用户身份,
        //和从http中拿的用户身份做对比。
        auth.userDetailsService(usernamePasswordUtilsService);
    }

    //接口AuthenticationManager,顾名思义，即为“身份认证管理器”，
    //说白了，就是各种类型登录方式、或者Authentication（*AuthenticationToken）辅助认证Provider的管理对象。
    //比如，如果只是采用表单登录认证，那么完全可以使用默认的AuthenticationManager，
    //认证用户势必要提供UserDetailsService、PasswordEncoder，如此一来，系统会根据这两个对象，
    //非常贴心的自动初始化默认的AuthenticationManager

    //接口AuthenticationManager,用于处理一个认证请求，也就是Spring Security中的Authentication认证令牌
    //AuthenticationManager（接口）是认证相关的核心接口，也是发起认证的出发点
    @Bean
    CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
        //开发者如需使用AuthenticationManager，则可以通过覆盖此方法，
        //将configure(AuthenticationManagerBuilder)方法构造的AuthenticationManager暴露为Bean

        //这里设置自带的AuthenticationManager，否则要自己写一个
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }
}
