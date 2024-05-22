package com.wen.shuzhi.loginRegister.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.loginRegister.entity.User;
import com.wen.shuzhi.loginRegister.entity.UserDTO;
import com.wen.shuzhi.loginRegister.service.Impl.UserServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Comment;
import com.wen.shuzhi.rusticTourism.entity.Favorite;
import com.wen.shuzhi.rusticTourism.entity.Like;
import com.wen.shuzhi.rusticTourism.service.Impl.CommentServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.FavoriteServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.LikeServiceImpl;
import com.wen.shuzhi.rusticTourism.utils.UploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.wen.shuzhi.loginRegister.utils.RedisConstants.*;


@Slf4j
@RestController
public class UserController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private LikeServiceImpl likeService;

    @Autowired
    private FavoriteServiceImpl favoriteService;

    @Autowired
    private CommentServiceImpl commentService;

    @Value("${photo.file.dir}") //获取application.yml配置文件中的属性
    public String realPath;     //获取文件上传后的路径

    //1、登录
    @Transactional
    @PostMapping("/login")
    public R login(User user){

        String account = user.getAccount();
        String password = user.getPassword();
        String role = user.getRole();

        log.info("account="+account+",password="+password+",role="+role);

        //1、判断账号，密码、角色是否为空
        if(password.isEmpty() || account.isEmpty() || role.isEmpty() ){
            //1.1、账号、密码、角色有一个为空
            return new R(false,"账号或密码或角色不能为空");
        }

        //1.2、账号、密码、角色都不为空

        //2、判断用户或者管理员是否存在
        User loginUser = userService.getUserByAccountAndPasswordAndRole(account, password,role);
        log.info(String.valueOf(loginUser));
        if(loginUser ==null){

            //2.1、不存在该用户或者管理员
            return new R(false,"账号或密码或角色错误");
        }

        //2.2、存在该用户或者管理员

        //3、保存用户信息到redis中
        //3.1、随机生成token 作为登录凭证
        String token = UUID.randomUUID().toString();
        log.info("登录成功后生成的token==="+token);
        //3.2、将User对象转换为Hash存储
        UserDTO userDTO = BeanUtil.copyProperties(loginUser, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));


        //3.3 存储到redis中
        String tokenKey = LOGIN_USER_KEY+token;
        log.info("redis中的tokenKey为:"+tokenKey);

        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);

        //3.4 设置token有效期
        stringRedisTemplate.expire(tokenKey,LOGIN_USER_TTL,TimeUnit.MINUTES);

        HashMap<String, Object> data=new HashMap<>();
        data.put("token",token);
        data.put("userInfo",userDTO);
        log.info("登录成功");
        return new R(true,data,"恭喜你:登录成功");
    }

    //2、增加用户----提供给用户注册
    @PostMapping("/register")
    @Transactional
    public R register(User user,
                      @RequestParam(value="code",required = false) String code,
                      @RequestParam(value="avatarImage",required=false) MultipartFile multipartFile) throws IOException {

        log.info("user==" + user);
        log.info("code=="+code);

        String redisCode = stringRedisTemplate.opsForValue().get(REGISTER_CODE_KEY);
        log.info("redis中获取的验证码：" + redisCode);
        log.info("用户输入的验证码:" + code);

        //1、判断用户是否已经填写了所有信息
        if (user.getAccount().isEmpty() || user.getPassword().isEmpty() || user.getUsername().isEmpty()
                || user.getSex().isEmpty() || user.getAge() == null || user.getEmail().isEmpty()
                || code.isEmpty() || user.getRole().isEmpty() || multipartFile==null){
            return new R(false,"还有信息未填写,请检查");
        }

        //2、判断存储在redis中验证码是否存在
        if(redisCode==null){
            return new R(false,"验证码已过期,请刷新重试");
        }

        //3、判断验证码是否正确
        if (!redisCode.equalsIgnoreCase(code)){
            return new R(false,"验证码输入错误");
        }

        //4、查看用户账号是否重复
        if(userService.getUserByAccount(user.getAccount())!=null){
           return new R(false,"该账号已经有人使用过了");
        }

        //5、设置头像照片访问路径和插入时间

        //5.1、设置头像照片访问路径

        //（1）根据时间戳生成照片新文件名
        String newFileName = new UploadUtils().getNewFileName(multipartFile,realPath);
        log.info("上传的图片在服务器的路径===="+realPath);
        log.info("上传的图片在服务器对应的文件名称为"+newFileName);

        //（2）设置照片的访问路径
        String imageUrl="http://localhost:8081/imgs/"+newFileName;
        log.info("存储到数据库的图片访问路径为"+imageUrl);
        user.setAvatarImageUrl(imageUrl);

        //5.2、设置插入时间
        user.setAddTime(new java.sql.Date(new Date().getTime()));

        //6、插入用户到数据库
        log.info("注册的用户信息："+user);
        int ret = userService.insertUser(user);
        if(ret!=1){
            return new R(false,"注册失败，系统出错了");
        }
        return new R(true,"注册成功");
    }

    //3、增加用户----提供给管理员使用
    @PostMapping("/user")
    @Transactional
    public R insertUser(User user,
                      @RequestParam(value="avatarImage",required=false) MultipartFile multipartFile) throws IOException {

        log.info("user==" + user);

        //1、判断是否已经填写了所有信息
        if (user.getAccount().isEmpty() || user.getPassword().isEmpty() || user.getUsername().isEmpty()
                || user.getSex().isEmpty() || user.getAge() == null || user.getEmail().isEmpty()
                ||  user.getRole().isEmpty() || multipartFile==null){
            return new R(false,"还有信息未填写,请检查");
        }


        //2、查看用户账号是否重复
        if(userService.getUserByAccount(user.getAccount())!=null){
            return new R(false,"该账号已经有人使用过了");
        }

        //3、设置头像照片访问路径和插入时间

        //3.1、设置头像照片访问路径
        //（1）根据时间戳生成照片新文件名
        String newFileName = new UploadUtils().getNewFileName(multipartFile,realPath);
        log.info("上传的图片在服务器的路径===="+realPath);
        log.info("上传的图片在服务器对应的文件名称为"+newFileName);

        //（2）设置照片的访问路径
        String imageUrl="http://localhost:8081/imgs/"+newFileName;
        log.info("存储到数据库的图片访问路径为"+imageUrl);
        user.setAvatarImageUrl(imageUrl);

        //3.2、设置插入时间
        user.setAddTime(new java.sql.Date(new Date().getTime()));

        log.info("注册的用户信息："+user);
        int ret = userService.insertUser(user);
        if(ret!=1){
            return new R(false,"注册失败，系统出错了");
        }
        return new R(true,"注册成功");
    }

    //4、删除用户
    @DeleteMapping("/user")
    public R deleteUser(@RequestParam(value="userId",required = false)Integer userId,
                        @RequestParam(value="pn",required = false)Integer pn){

        //1、判断删除的用户是否存在
        User user = userService.getUserByUserId(userId);
        if(user==null){
            //1.1、用户不存在
            return new R(false,"删除失败,该用户不存在");
        }

        //2、删除之前获得账号
        String account = user.getAccount();

        //3、判断是否删除成功
        int ret = userService.deleteUser(userId);
        if(ret!=1){
            //3.1、删除失败
            return new R(false,"删除失败,系统出错了,请稍后再试");
        }

        //3.2、删除成功
        log.info("删除用户账号:"+account);
        return new R(true,pn,"成功删除用户id为"+userId+",账号为"+ account +"的用户信息");

    }

    //5、分页查找所有用户
    @GetMapping("/user")
    public R showAttractions(@RequestParam(value = "account",required = false)String account,
                             @RequestParam(value ="username",required = false)String username,
                             @RequestParam(value ="role",required = false)String role,
                             @RequestParam(value="pn",required = false,defaultValue = "1")Integer pn){

        log.info("用户账号:"+account+",省份:"+username+",查询的第几页:"+pn);

        //1、分页构造器
        Page<User> searchPage = new Page<User>(pn,8);
        log.info("要查找的用户账号是否为空:"+String.valueOf(StringUtils.isEmpty(account)));
        log.info("要查找的用户名是否为空:"+String.valueOf(StringUtils.isEmpty(username)));

        //1、条件查询构造器
        QueryWrapper<User> wrapper = new QueryWrapper<User>();

        //有账号
        if(!StringUtils.isEmpty(account)){
            wrapper.like("account",account);
        }

        //有用户名
        if(!StringUtils.isEmpty(username)){
            wrapper.like("username",username);
        }

        if(!StringUtils.isEmpty(role)){
            wrapper.eq("role",role);
        }

        Page<User> page = userService.page(searchPage,wrapper);
        log.info("page对象如下:"+page.getRecords().toArray());
        return new R(true,page,"温馨提示:用户搜索结果如下");
    }

    //6、修改用户
    @PutMapping("/user")
    public R modifyUser(User user,
                        @RequestParam(value="avatarImage",required=false) MultipartFile multipartFile,
                        @RequestParam(value="pn",required = false,defaultValue = "1")Integer pn) throws IOException {


        log.info("即将要修改的用户的信息为:"+user);

        //1、判断是否已经填写了所有信息
        if (user.getAccount().isEmpty() || user.getPassword().isEmpty() || user.getUsername().isEmpty()
                || user.getSex().isEmpty() || user.getAge() == null || user.getEmail().isEmpty() || user.getRole().isEmpty()){
            return new R(false,"还有信息未填写,请检查");
        }

        //2、判断该用户是否存在
        if(userService.getUserByUserId(user.getUserId())==null){
            //2.1、用户不存在
            return new R(false,"该用户不存在,请重试");
        }

        //3、查出原来的用户账号
        String account = userService.getUserByUserId(user.getUserId()).getAccount();

        //4、用户修改了账号并账号已经存在
        if((!account.equals(user.getAccount()))&&(userService.getUserByAccount(user.getAccount())!=null)){
            return new R(false,"该账号不可用,请重换");
        }

        //5、判断是否有有上传照片文件
        if(multipartFile!=null&&!multipartFile.isEmpty()){
            //5.1、有照片上传、重新设置图片路径

            //（1）、根据时间戳生成照片新文件名
            String newFileName = new UploadUtils().getNewFileName(multipartFile,realPath);
            log.info("上传的图片到服务器的路径:"+realPath);

            //（2）、设置照片的访问路径
            log.info("上传的图片在服务器对应的文件名称:"+newFileName);
            String imageUrl="http://localhost:8081/imgs/"+newFileName;
            log.info("存储到数据库的图片访问路径为"+imageUrl);
            user.setAvatarImageUrl(imageUrl);
        }

        //1.2、没有照片上传，则沿用之前的图片即可，保存到数据库

        //6、设置最新更新日期
        user.setUpdateTime(new java.sql.Date(new Date().getTime()));
        log.info("即将保存的用户信息:"+ user.toString());

        //5、判断是否修改
        int ret = userService.modifyUser(user);
        if(ret!=1){
            return new R(false,"修改失败,请稍后再试");
        }

        return new R(true, pn,"成功修改用户编号为"+user.getUserId()+"的用户信息");
    }


    //7、检查用户账号是否可用
    @GetMapping("/checkAccount")
    public R checkAccount(@RequestParam(required = false) String account){
        User user = userService.getUserByAccount(account);
        if(user==null&& account.length()!=0){
           return new R(true,"该账号可用");
        }
        return new R(false,"该账号不可用");
    }

    //8、注销
    @GetMapping("/logout")
    public R logout(HttpServletRequest request){

        //1、基于token删除redis中的用户
        String token = request.getHeader("token");
        log.info("token:"+token);
        String key = LOGIN_USER_KEY + token;
        log.info("key:"+key);
        Boolean isDelete = stringRedisTemplate.delete(key);
        log.info("isDelete:"+isDelete);

        //3、请求转发回到首页
        return new R(true,"成功退出系统!");
    }



    //修改密码--有逻辑问题
//    @GetMapping("/modifyPassword")
//    public R ModifyPassword(@RequestParam("userId") Integer userId,
//                            @RequestParam("newPassword") String newPassword){
//
//        int ret = userService.modifyPassword(userId, newPassword);
//        if(ret!=0){
//            return new R(false,"温馨提示:修改错误,输入原密码错误");
//        }
//        return new R(true,"温馨提示:成功修改用户名为"+userServicegetUsername()+"的用户的密码");
//    }



    //获取用户点赞、收藏、评论数量
    @GetMapping("getLikeAndFavoriteAndCommentNum")
    public R getLikeAndFavoriteAndCommentNum(@RequestParam("userId")Integer userId){
        log.info("查询的用户id:"+userId);

        //点赞条件构造器
        QueryWrapper<Like> likeQueryWrapper= new QueryWrapper<>();
        QueryWrapper<Favorite> favoriteQueryWrapper= new QueryWrapper<>();
        QueryWrapper<Comment> commentQueryWrapper= new QueryWrapper<>();

        //加入where条件
        likeQueryWrapper.eq("user_id",userId);
        favoriteQueryWrapper.eq("user_id",userId);
        commentQueryWrapper.eq("user_id",userId);
        int likeCount = likeService.count(likeQueryWrapper);
        int favoriteCount = favoriteService.count(favoriteQueryWrapper);
        int commentCount = commentService.count(commentQueryWrapper);

        log.info("likeCount:"+likeCount);
        log.info("favoriteCount:"+favoriteCount);
        log.info("commentCount:"+commentCount);
        HashMap<String, Integer> ret = new HashMap<>();
        ret.put("likeCount",likeCount);
        ret.put("favoriteCount",favoriteCount);
        ret.put("commentCount",commentCount);
        return new R(true,ret,"获取用户点赞、收藏、评论数量如下");



    }

}
