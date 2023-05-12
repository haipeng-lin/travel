package com.wen.shuzhi.rusticTourism.controller;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.loginRegister.service.Impl.UserServiceImpl;
import com.wen.shuzhi.loginRegister.utils.UserHolder;
import com.wen.shuzhi.rusticTourism.entity.*;
import com.wen.shuzhi.rusticTourism.service.Impl.*;
import com.wen.shuzhi.rusticTourism.utils.PageUtils;
import com.wen.shuzhi.rusticTourism.utils.UploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
public class AttractionController {
    @Autowired
    private AttractionServiceImpl attractionService;

    @Autowired
    private ClickServiceImpl clickService;

    @Autowired
    private LikeServiceImpl likesService;

    @Autowired
    private FavoriteServiceImpl favoriteService;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private BrowseTimeServiceImpl browseTimeService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserAttractionTagServiceImpl userAttractionTagService;

    @Autowired
    private AttractionDataChangeServiceImpl attractionDataChangeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${photo.file.dir}") //获取application.yml配置文件中的属性
    public String realPath;     //获取文件上传后的路径

    //1、添加景点
    @PostMapping("/attraction")
    public R addAttraction(Attraction attraction,
                           @RequestParam(value="imageFile",required=false) MultipartFile multipartFile,
                           @RequestParam(value="pn",required=false,defaultValue = "1") Integer pn) throws IOException {

        log.info("提交的景点信息:"+ attraction.toString());

        //1、检查是否已经填了全部信息
        if(attraction.getAttractionName().isEmpty() || attraction.getLocation().isEmpty() ||
                attraction.getIntroduction().isEmpty() || attraction.getStrategy().isEmpty() ||
                attraction.getLikes()==null || attraction.getCollections()==null ||
                attraction.getComments()==null || attraction.getClicks()==null ||
                attraction.getBrowseTime()==null || attraction.getKeyWord().isEmpty()||
                attraction.getTag().isEmpty() || multipartFile==null){

            //1.1、有信息尚未填写
            return new R(false,"添加失败,景点信息还未完全填写,请检查");
        }

        //1.2、已经填写全部信息


        //2、设置照片访问路径和插入时间

        //2.1、设置照片访问路径
        // （1）根据时间戳生成照片新文件名
        String newFileName = new UploadUtils().getNewFileName(multipartFile,realPath);
        log.info("上传的图片在服务器的路径===="+realPath);
        log.info("上传的图片在服务器对应的文件名称为"+newFileName);

        //（2）设置照片的访问路径
        String imageUrl="http://localhost:8081/imgs/"+newFileName;
        log.info("存储到数据库的图片访问路径为"+imageUrl);
        attraction.setImageUrl(imageUrl);

        //2.2、设置时间
        attraction.setAddTime(new java.sql.Date(new Date().getTime()));

        //3、保存到数据库
        log.info("即将保存的景点信息:"+ attraction.toString());
        int i = attractionService.saveAttraction(attraction);
        if(i != 1){
            //3.1、保存失败
            log.info("保存景点失败");
            return new R(false,"保存景点失败,系统错误了,请稍后再试");
        }
        //3.2、保存成功
        log.info("保存景点成功");
        return new R(true,pn,"保存景点成功");
    }

    //2、删除景点
    @DeleteMapping("/attraction")
    public R deleteAttraction(@RequestParam(value = "attractionId",required = false)Integer attractionId,
                              @RequestParam(value="pn",required = false,defaultValue = "1")Integer pn){

        log.info("要删除的景点的attractionId:"+attractionId);
        log.info("要删除的景点所在的页面:"+pn);

        //1、判断要删除的景点是否存在
        Attraction attraction = attractionService.getAttractionByAttractionId(attractionId);
        if(attraction==null){
            //1、景点不存在
            return new R(false,"删除失败,该景点不存在");
        }

        //2、在删除之前获得删除景点名称
        String attractionName = attraction.getAttractionName();

        //3、判断是否删除成功
        int ret = attractionService.deleteAttraction(attractionId);
        if(ret!=1){
            //3.1、删除失败
            log.info("删除失败");
            return new R(false,pn,"删除景点失败,系统错误了,请稍后再试");
        }
        //3.2、删除成功
        log.info("删除成功");
        return new R(true,pn,"成功删除名称为"+attractionName+"的景点信息！");
    }

    //3、分页查找景点-景点名称、位置、标签
    @GetMapping("/attraction")
    public R showAttractions(@RequestParam(value = "attractionsName",required = false)String attractionsName,
                             @RequestParam(value ="location",required = false)String location,
                             @RequestParam(value ="tag",required = false)String tag,
                             @RequestParam(value="pn",required = false,defaultValue = "1")Integer pn){

        log.info("景点名称:"+attractionsName+",省份:"+location+",查询的第几页:"+pn);

        //1、分页构造器
        Page<Attraction> searchPage = new Page<Attraction>(pn,7);
        log.info("要查找的景点名称是否为空:"+String.valueOf(StringUtils.isEmpty(attractionsName)));
        log.info("要查找的景点所在省份是否为空:"+String.valueOf(StringUtils.isEmpty(location)));
        log.info("要查找的景点的标签是否为空:"+String.valueOf(StringUtils.isEmpty(tag)));

        //1、条件查询构造器
        QueryWrapper<Attraction> wrapper = new QueryWrapper<Attraction>();

        //有景点名称
        if(!StringUtils.isEmpty(attractionsName)){
            wrapper.like("attraction_name",attractionsName);
        }
        //有省份
        if(!StringUtils.isEmpty(location)){
            wrapper.like("location",location);
        }
        if(!StringUtils.isEmpty(tag)){
            wrapper.like("tag",tag);
        }


        Page<Attraction> page = attractionService.page(searchPage,wrapper);
        log.info("page对象如下:"+page.toString());

        return new R(true,page,"景点信息搜索结果如下");
    }

    //4、修改景点
    @PutMapping("/attraction")
    public R modifyAttraction(Attraction attraction,
                              @RequestParam(value="imageFile",required=false) MultipartFile multipartFile,
                              @RequestParam(value="pn",required = false,defaultValue = "1")Integer pn) throws IOException {


        //1、检查是否已经填了全部信息
        if(attraction.getAttractionName().isEmpty() || attraction.getLocation().isEmpty() ||
                attraction.getIntroduction().isEmpty() || attraction.getStrategy().isEmpty() ||
                attraction.getLikes()==null || attraction.getCollections()==null ||
                attraction.getComments()==null || attraction.getClicks()==null ||
                attraction.getBrowseTime()==null || attraction.getKeyWord().isEmpty()||
                attraction.getTag().isEmpty()){

            //1.1、有信息尚未填写
            return new R(false,"修改失败,景点信息还未完全填写,请检查");
        }

        log.info("即将要修改的信息为:"+ attraction);
        String attractionName = attraction.getAttractionName();

        //2、判断是否有上传照片文件
        if(multipartFile!=null&&!multipartFile.isEmpty()){

            //2.1、有照片上传、重新设置图片路径
            //（1）根据时间戳生成照片新文件名
            String newFileName = new UploadUtils().getNewFileName(multipartFile,realPath);
            log.info("上传的图片到服务器的路径:"+realPath);

            //（2）设置照片的访问路径
            log.info("上传的图片在服务器对应的文件名称:"+newFileName);
            String imageUrl="http://localhost:8081/imgs/"+newFileName;
            log.info("存储到数据库的图片访问路径为"+imageUrl);
            attraction.setImageUrl(imageUrl);
        }

        //2.2、没有照片上传，则沿用之前的图片即可，保存到数据库

        //3、设置最新更新日期
        attraction.setUpdateTime(new java.sql.Date(new Date().getTime()));

        log.info("即将保存的景点信息:"+ attraction.toString());
        int ret = attractionService.modifyAttraction(attraction);
        if(ret!=1){
            //2、保存失败
            return new R(false,"修改失败,系统出错了,请稍后再试");
        }

        //3、保存成功
        return new R(true, pn,"成功修改景点名称为"+ attractionName+"的景点信息");
    }




    //（7）查询景点数量和点击数
    @GetMapping("/getAttractionNumAndClick")
    public R getAttractionNumAndClick(){
        Map<String, String> ret = attractionService.getAttractionNumAndClick();
        System.out.println("景点数量和点击数为:"+ret);
        return new R(true,ret,"景点数量和点击数信息如下");
    }


    //6、查看景点详细信息+评论，对应的景点点击数+1，并记录到景点数据变化表中
    @GetMapping(value="/detailAttraction")
    public R viewDetail1(@RequestParam(value="attractionId",required = true)Integer attractionId,
                         @RequestParam(value="userId",required = true)Integer userId,
                         @RequestParam(value="pn",required=false,defaultValue = "1") Integer pn){

        //1、判断点击的景点是否为空
        Attraction attraction = attractionService.getAttractionByAttractionId(attractionId);
        if(attraction==null){
            return new R(false,"点击的景点不存在,请重试");
        }
        log.info("根据id查出来的景点信息:"+ attraction);


        //2、该景点的点击次数+1
        attractionService.addAttractionClickNum(attractionId);

        //3、添加到用户点击表中
        Click click = clickService.getClickByAttractionIdAndUserId(attractionId, userId);
        //判断该用户是否点击过
        if(click == null){ //没点击过 创建一个Clicks类
            clickService.insertClick(new Click(attractionId,userId,1,new Date(),null));
        }else{ //点击过  点击次数+1
            click.setClicks(click.getClicks()+1);
            clickService.updateClick(click);
        }

        //4、添加到景点点击表中
        AttractionDataChange adc = attractionDataChangeService.getAttractionDataChangeByAttractionIdAndDate(attractionId, new java.sql.Date(new Date().getTime()));
        log.info("查询的景点点击表信息为:"+adc);
        if(adc==null){
            //今天该景点还没有任何操作，新建一个景点数据变化表
            attractionDataChangeService.insertAttractionDataChange(
                    new AttractionDataChange(attraction.getAttractionId(),attraction.getAttractionName(),
                            1,0,0,0,
                            0,attraction.getTag(), new java.sql.Date(new Date().getTime() )));
        }else{
            //今天该景点已经存在，点击数+1
            attractionDataChangeService.updateAttractionDataChange(new AttractionDataChange(adc.getAttractionDataChangeId(),
                    adc.getAttractionId(),adc.getAttractionName(), adc.getClicks()+1,
                    adc.getLikes(),adc.getCollections(),adc.getComments(),
                    adc.getBrowseTime(),adc.getTag(), new java.sql.Date(new Date().getTime())));
        }


        return new R(true,attraction,"景点名称为"+attraction.getAttractionName()+"的信息如下");

    }

    //7、获取用户对某一景点的浏览时长
    @GetMapping("/getBrowseTime")
    public R getBrowseTime(@RequestParam("attractionId")Integer attractionId,
                              @RequestParam("userId")Integer userId,
                              @RequestParam("browseTime")Integer browseTime){

        log.info("userId==="+userId);
        log.info("attractionsId==="+attractionId);
        log.info("browseTime==="+browseTime);

        //1、判断浏览的景点是否为空
        Attraction attraction = attractionService.getAttractionByAttractionId(attractionId);
        if(attraction==null){
            return new R(false,"增加浏览时长失败，浏览的景点为空");
        }

        //2、记录到browse_time表中
        BrowseTime browse = browseTimeService.getBrowseTimeByAttractionIdAndUserId(attractionId, userId);
        if(browse == null){
            //该用户没浏览过该景点 插入
            browseTimeService.insertBrowseTime(new BrowseTime(attractionId,userId,browseTime,new Date(),null));
        }else{ //浏览过 累加浏览时长
            log.info("查询的浏览时间表为："+browse.toString());
            browse.setBrowseTime(browse.getBrowseTime()+browseTime);
            browseTimeService.updateBrowseTime(browse);
        }

        //3、累加attractions表browse_time字段
        attractionService.addAttractionBrowseTime(attractionId,browseTime);

        //4、保存到景点数据变化表中
        AttractionDataChange adc = attractionDataChangeService.getAttractionDataChangeByAttractionIdAndDate(attractionId, new java.sql.Date(new Date().getTime()));
        log.info("查询的景点点击表信息为:"+adc);
        if(adc==null){
            //今天该景点还没有任何操作，新建一个景点数据变化表
            attractionDataChangeService.insertAttractionDataChange(
                    new AttractionDataChange(attraction.getAttractionId(),attraction.getAttractionName(),0
                            ,0,0,0,
                            browseTime,attraction.getTag(), new java.sql.Date(new Date().getTime() )));
        }else{
            //今天该景点已经存在，点击数+1
            attractionDataChangeService.updateAttractionDataChange(new AttractionDataChange(adc.getAttractionDataChangeId(),
                    adc.getAttractionId(),adc.getAttractionName(), adc.getClicks(),
                    adc.getLikes(),adc.getCollections(),adc.getComments(),
                    adc.getBrowseTime()+browseTime,adc.getTag(), new java.sql.Date(new Date().getTime())));
        }
        return new R(true,"增加浏览时长成功！！！");
    }


    //8、判断是否点赞过：有点赞过返回true，没有返回false
    @GetMapping("/isLike")
    public boolean isLike(@RequestParam("attractionId")Integer attractionId,
                          @RequestParam("userId")Integer userId){

        log.info("判断是否点赞过的userId为:"+userId);
        log.info("判断是否点赞过的attractionId为:"+attractionId);

        //1、通过redis判断当前登录用户是否已经点赞
        String key = "attractions:like"+attractionId+":"+userId;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key,String.valueOf(userId));
        if(BooleanUtil.isFalse(isMember)){
            //1.1、还没点赞，返回false
            return false;
        }else{
            //1.2、已经点赞过了，返回true
            return true;
        }
    }

    //9、判断是否收藏过：有收藏返回true，没有返回false
    @GetMapping("/isFavorite")
    public boolean isFavorite(@RequestParam("attractionId")Integer attractionId,
                                @RequestParam("userId")Integer userId){

        log.info("判断是否收藏过的userId为:"+userId);
        log.info("判断是否收藏过的attractionId为:"+attractionId);

        //1、通过redis判断当前登录用户是否已经收藏
        String key = "attractions:favorite"+attractionId+":"+userId;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key,String.valueOf(userId));
        if(BooleanUtil.isFalse(isMember)){
            //1.1、还没收藏，返回false
            return false;
        }else{
            //1.2、已经收藏过了，返回true
            return true;
        }
    }


    //10、获取景点的id 点赞数+1
    @GetMapping("/likeCountAddSub")
    public R likeCountAddSub(@RequestParam("attractionId")Integer attractionId,
                             @RequestParam("userId")Integer userId){

        log.info("进入点赞");

        log.info("用户id为:"+userId);
        log.info("要点赞的景点id为:"+attractionId);
        //1、判断要点赞的景点是否为空
        Attraction attraction = attractionService.getAttractionByAttractionId(attractionId);
        if(attraction==null){
            return new R(false,"点赞失败，点赞的景点为空");
        }

        //2. 判断当前登录用户是否已经点赞
        String key = "attractions:like"+attractionId+":"+userId;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key,String.valueOf(userId));
        if(BooleanUtil.isFalse(isMember)){
            //2. 如果未点赞 则可以点赞
            //2.1 数据库点赞数+1
            int i = attractionService.likeAddOne(attractionId);
            log.info(String.valueOf(i));
            if(i == 1){ //修改点赞数成功 返回1
                //2.2 保存用户到Redis的set集合中
                log.info("用户没点过赞，可以点赞");
                stringRedisTemplate.opsForSet().add(key, String.valueOf(userId));

                //2.3、保存到likes表中
                likesService.saveLike(new Like(attractionId,userId,new Date(),null));

                int likedNum = commentService.getLikedNumByAttractionsId(attractionId);
                log.info("点赞数:"+likedNum);

                //2.4、保存到景点数据变化表中
                AttractionDataChange adc = attractionDataChangeService.getAttractionDataChangeByAttractionIdAndDate(attractionId, new java.sql.Date(new Date().getTime()));
                log.info("查询的景点点击表信息为:"+adc);
                if(adc==null){
                    //今天该景点还没有任何操作，新建一个景点数据变化表
                    attractionDataChangeService.insertAttractionDataChange(
                            new AttractionDataChange(attraction.getAttractionId(),attraction.getAttractionName(),0
                                    ,1,0,0,
                                    0,attraction.getTag(), new java.sql.Date(new Date().getTime() )));
                }else{
                    //今天该景点已经存在，点击数+1
                    attractionDataChangeService.updateAttractionDataChange(new AttractionDataChange(adc.getAttractionDataChangeId(),
                            adc.getAttractionId(),adc.getAttractionName(), adc.getClicks(),
                            adc.getLikes()+1,adc.getCollections(),adc.getComments(),
                            adc.getBrowseTime(),adc.getTag(), new java.sql.Date(new Date().getTime())));
                }



                //2.5 更新用户最近的行为时间 无则创建 有则更新时间累加权重+1
                String attractionName = attractionService.getAttractionNameById(attractionId);
                String attractionTag = attractionService.getAttractionTagById(attractionId);
                UserAttractionTag userAttractionTag = userAttractionTagService.getUserAttractionTagByUserIdAndAttractionName(userId, attractionName);
                if(userAttractionTag == null){ //没有该景点的行为
                    userAttractionTagService.insertUserAttractionTag(new UserAttractionTag(userId,attractionName,attractionTag, (double) 1,new Date(),null));
                }else{ //有该景点的行为

                    userAttractionTag.setWeight(userAttractionTag.getWeight()+1);
                    userAttractionTag.setUpdateTime(new Date());
                    userAttractionTagService.updateUserAttractionTag(userAttractionTag);
                }

                return new R(true,likedNum,"点赞成功！");
            }else {
                return new R(false, "系统出错了,请稍后再试");
            }
        }else{
            log.info("用户已经点赞过了，不可以点赞");
            //4. 如果已点赞 取消点赞
            //4.1 数据库点赞数-1
            int i = attractionService.likeSubOne(attractionId);
            //数据库删除对应点赞数据
            likesService.deleteLike(attractionId,userId);

            log.info(String.valueOf(i));
            if(i == 1){
                //4.2 把用户从Redis的set集合移除
                log.info("用户点过赞，不可以点赞");
                stringRedisTemplate.opsForSet().remove(key,String.valueOf(userId));
                int likedNum = commentService.getLikedNumByAttractionsId(attractionId);
                log.info("点赞数:"+likedNum);

                return new R(true,likedNum,"取消点赞！");
            }else {
                return new R(false, "系统出错了,请稍后再试");
            }
        }

    }

    //11、获取景点的id 收藏数+1
    @GetMapping("/favoriteAddSub")
    public R FavoriteAddSub(@RequestParam("attractionId")Integer attractionId
                            ,@RequestParam("userId")Integer userId) {

        log.info("进入收藏");
        //1、判断收藏的景点是否为空
        Attraction attraction = attractionService.getAttractionByAttractionId(attractionId);
        if(attraction==null){
            return new R(false,"收藏失败，收藏的景点为空");
        }

        //2. 判断当前登录用户是否已经收藏
        String key = "attractions:favorite" + attractionId + ":" + userId;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, String.valueOf(userId));
        if (BooleanUtil.isFalse(isMember)) {
            //3、如果未收藏 则可以收藏
            //3.1、数据库收藏数+1
            int i = attractionService.collectionAddOne(attractionId);
            log.info("i====" + i);
            if (i == 1) { //修改收藏数成功 返回1
                //3.2、保存用户到Redis的set集合中
                log.info("用户没收藏过，可以收藏");
                stringRedisTemplate.opsForSet().add(key, String.valueOf(userId));

                //3.3、保存到favorite表中
                favoriteService.saveFavorite(new Favorite(attractionId, userId, new Date(),null));

                int collectionNum = attractionService.getCollectionNumById(attractionId);
                log.info("收藏数为:" + collectionNum);

                //3.4、保存到景点数据变化表中
                AttractionDataChange adc = attractionDataChangeService.getAttractionDataChangeByAttractionIdAndDate(attractionId, new java.sql.Date(new Date().getTime()));
                log.info("查询的景点点击表信息为:"+adc);
                if(adc==null){
                    //今天该景点还没有任何操作，新建一个景点数据变化表
                    attractionDataChangeService.insertAttractionDataChange(
                            new AttractionDataChange(attraction.getAttractionId(),attraction.getAttractionName(),0
                                    ,0,1,0,
                                    0,attraction.getTag(), new java.sql.Date(new Date().getTime() )));
                }else{
                    //今天该景点已经存在，点击数+1
                    attractionDataChangeService.updateAttractionDataChange(new AttractionDataChange(adc.getAttractionDataChangeId(),
                            adc.getAttractionId(),adc.getAttractionName(), adc.getClicks(),
                            adc.getLikes(),adc.getCollections()+1,adc.getComments(),
                            adc.getBrowseTime(),adc.getTag(), new java.sql.Date(new Date().getTime())));
                }

                //3.5、更新用户最近的行为时间 无则创建 有则更新时间累加权重+2
                //2.5 更新用户最近的行为时间 无则创建 有则更新时间累加权重+1
                String attractionName = attractionService.getAttractionNameById(attractionId);
                String attractionTag = attractionService.getAttractionTagById(attractionId);
                UserAttractionTag userAttractionTag = userAttractionTagService.getUserAttractionTagByUserIdAndAttractionName(userId, attractionName);
                if(userAttractionTag == null){ //没有该景点的行为
                    userAttractionTagService.insertUserAttractionTag(new UserAttractionTag(userId,attractionName,attractionTag, (double) 1,new Date(),null));
                }else{ //有该景点的行为

                    userAttractionTag.setWeight(userAttractionTag.getWeight()+2);
                    userAttractionTag.setUpdateTime(new Date());
                    userAttractionTagService.updateUserAttractionTag(userAttractionTag);
                }

                return new R(true, collectionNum, "收藏成功！");
            } else {
                return new R(false, "系统出错了,请稍后再试");
            }
        } else {
            //4. 如果已收藏 取消收藏
            //4.1 数据库收藏数-1
            int i = attractionService.collectionSubOne(attractionId);
            //数据库删除对应收藏数据
            favoriteService.deleteFavorite(attractionId, userId);

            log.info(String.valueOf(i));
            if (i == 1) {
                //4.2 把用户从Redis的set集合移除
                log.info("用户收藏过，取消收藏");
                stringRedisTemplate.opsForSet().remove(key, String.valueOf(userId));

                int collectionNum = attractionService.getCollectionNumById(attractionId);
                return new R(true, collectionNum, "取消收藏！");
            } else {
                return new R(false, "系统出错了,请稍后再试");
            }

        }
    }


    /**
     *  5、大数据统计：共7个方法
     */
    //（1）统计某一个景点类别的点赞、收藏、评论、点击次数、浏览时间
    @GetMapping("/statsAttractions")
    public R statsAttractionByTag(@RequestParam(value="tag",required = true)String tag){

        //1、判断类别是否为空
        if(tag.isEmpty()){
            return new R(false,"填写的类别为空,查找失败");
        }
        //2、统计该类别的信息
        StatsAttraction statsAttractionByTag = attractionService.getStatsAttractionByTag(tag);

        log.info("统计该类别的信息:"+statsAttractionByTag);
        return new R(true,statsAttractionByTag,tag);
    }

    //（2）根据标签分组查询所有景点的点赞、收藏、评论、点击次数、浏览时间
    @GetMapping("/statsAttractionGroupByTag")
    public R statsAttractionGroupByTag(){
        List<StatsAttraction> statsAttractions = attractionService.listStatsAttraction();
        log.info("全部统计的信息:"+statsAttractions);
        return new R(true,statsAttractions,"所有景点类别的点赞、收藏、评论、点击次数统计结果如下");
    }

    //（3）根据景点位置分组查询
    @GetMapping("statsAttractionCountGroupByLocation")
    public R statsAttractionCountGroupByLocation(){
        List<Map<String, Integer>> ret = attractionService.getAttractionCountGroupByLocation();
        log.info("ret:"+ret);
        return new R(true,ret,"各省份乡村旅游景点数量统计结果如下");
    }

    //（4）根据景点标签分组查询景点数量
    @GetMapping("statsAttractionCountGroupByTag")
    public R statsAttractionCountGroupByTag(){
        List<Map<String, Integer>> ret = attractionService.getAttractionCountGroupByTag();
        log.info("ret:"+ret);
        return new R(true,ret,"各类别乡村旅游景点数量统计结果如下");
    }

    //（5）统计某一个具体景点的点赞、收藏、评论、点击次数、浏览时间
    @GetMapping("/statsAttraction")
    public R statsAttractionByAttractionId(@RequestParam(value="attractionId")Integer attractionId){

        //1、判断该景点id是否存在
        StatsAttraction statsAttractionByAttractionId = attractionService.getStatsAttractionByAttractionId(attractionId);
        log.info("该景点的信息:"+statsAttractionByAttractionId);
        return new R(true,statsAttractionByAttractionId,"景点编号为"+attractionId+"的景点信息如下");

    }

    //（6）根据浏览时长降序查询前count个景点信息
    @GetMapping("/getAttractionOrderByBrowseTime")
    public R getAttractionOrderByBrowseTime(@RequestParam(value="count")Integer count){
        List<Map<String, Integer>> ret = attractionService.getAttractionOrderByBrowseTime(count);
        log.info("前"+count+"个浏览时长景点:"+ret);
        return new R(true,ret,"前"+count+"浏览时间最长的景点信息如下");
    }
}
