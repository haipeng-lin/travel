package com.wen.shuzhi.loginRegister.interceptor;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.wen.shuzhi.loginRegister.entity.UserDTO;
import com.wen.shuzhi.loginRegister.utils.RedisConstants;
import com.wen.shuzhi.loginRegister.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.wen.shuzhi.loginRegister.utils.RedisConstants.LOGIN_USER_KEY;


//拦截一切路径，效果：刷新token有效期，无论有无用户都会放行
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {

    /**
     * 不能@Autowired注入，因为RefreshTokenInterceptor是我们自己创建的，不在Spring容器中
     * 通过MvcConfig中自动注入StringReddisTemplate，通过有参构造器传递过来
     */
    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1. 获取请求头中的token
        String token = request.getHeader("token");
        log.info("拦截器--获取请求头中的token为==="+token);


        //2、请求头中token为空，需要登录，加上标记登录
        if(StrUtil.isBlank(token)){
            log.info("请求头中token为空，需要登录，加上标记登录");
            request.setAttribute("mark","login");
            return true;
        }

        //3、根据login:token:token值 去redis中查询是否存在该用户信息
       String key = LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash()
                .entries(key);
        log.info("redis中查询是否存在该用户信息"+userMap.toString());

        //3.1、用户不存在于redis中，需要登录，加上标记登录
        if(userMap.isEmpty()){
            UserHolder.removeUser();
            request.setAttribute("mark","login");
            return true;
        }

        //3.2、用户存于redis中，不需要登录

        //4、刷新token有效期：30分钟
        log.info("刷新token有效期");
        stringRedisTemplate.expire(key, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

        //5、放行
        return true;
    }

    /**
     *  调用时间：DispatcherServlet进行视图的渲染之后
     *  请求结束，把保存的用户信息清除掉
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
