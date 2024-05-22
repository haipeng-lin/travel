package com.wen.shuzhi.rusticTourism.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.rusticTourism.entity.Blog;
import com.wen.shuzhi.rusticTourism.mapper.BlogMapper;
import com.wen.shuzhi.rusticTourism.service.BlogService;
import com.wen.shuzhi.rusticTourism.utils.TimeUtils;
import com.wen.shuzhi.rusticTourism.utils.UploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * author: wen
 * 游记的一系列操作
 */
@Slf4j
@RestController
@RequestMapping("/blog")
public class BlogController {


    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogMapper blogMapper;

    @Value("${photo.file.dir}") //获取application.yml配置文件中的属性
    public String realPath;     //获取文件上传后的路径

    /**
     * 发布游记
     */
    @PostMapping("/blog")
    public R postBlog(Blog blog) {

        //1. 判断title head_image_url content 三者是否为null
        //多重if判断 - 可优化
        if (blog.getContent() == null) {
            return new R(false, "游记内容不能为空!");
        }
        if (blog.getHeadImageUrl() == null) {
            return new R(false, "游记头图不能为空!");
        }
        if (blog.getTitle() == null) {
            return new R(false, "游记标题不能为空!");
        }


        //2. blog对象参数赋值
        //2.1 获取当前时间 并转换为String
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());

        blog.setBrowseCount(0);
        blog.setLiked(0);
        blog.setCollection(0);
        blog.setAddTime(time);
        blog.setUpdateTime(time);

        //3. 保存到数据库中
        int res = blogMapper.insert(blog);
        if (res == 1) {
            log.info("发布成功");
        }

        log.info(new R(true, blog, "发布的游记如下").toString());

        return new R(true, blog, "发布的游记如下");
    }

    /**
     * 展示所有游记
     */
    @RequestMapping("/showAllBlog")
    public R showAllBlog() {
        List<Blog> blogList = blogService.list();

//        List<Blog> blogs = blogMapper.selectList(null);


        return new R(true, blogList, "展示所有的blog");
    }

    /**
     * 游记详情
     */
    @RequestMapping("/blogDetail/{blogId}")
    public R blogDetail(@PathVariable("blogId") Integer blogId, Model model) {
        //1. 根据blogId获取blog对象
        Blog blog = blogMapper.selectById(blogId);

        //2. 游记的浏览量+1
        UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("blog_id", blogId).set("browse_count", blog.getBrowseCount() + 1);

        int res = blogMapper.update(null, updateWrapper);

        if (res != 1) {
            return new R(false, "查询失败");
        }

        return new R(true, blog, "该游记详情");
    }

    /**
     * 游记展示 - 最近(时间降序)
     */
    @RequestMapping("/showBlogByTimeDesc")
    public R showBlogByTimeDesc() {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();

        queryWrapper.select().orderByDesc("add_time");

        List<Blog> blogList = blogMapper.selectList(queryWrapper);

        //处理时间字段 去除尾部的.0
        for (Blog blog : blogList) {
            String addTime = TimeUtils.dealWithTime(blog.getAddTime());
            String updateTime = TimeUtils.dealWithTime(blog.getUpdateTime());
            blog.setAddTime(addTime);
            blog.setUpdateTime(updateTime);
        }

        return new R(true, blogList, "游记按时间降序");
    }


    /**
     *  上传图片，并返回指定的格式
     *  格式：
     *      成功：{
     *              "errno": 0, // 注意：值是数字，不能是字符串
     *              "data": {
     *                  "url": "xxx", // 图片 src ，必须
     *                  "alt": "yyy", // 图片描述文字，非必须
     *                  "href": "zzz" // 图片的链接，非必须
     *              }
     *            }
     *      失败：
     *            {
     *                "errno": 1, // 只要不等于 0 就行
     *                "message": "失败信息"
     *            }
     * }
     */
    @PostMapping("upload-img")
    public HashMap uploadImg(@RequestParam(value="file") MultipartFile imageFile) throws IOException {

        HashMap<String,Object> ret=new HashMap<>();
        if(imageFile==null||imageFile.isEmpty()){
            ret.put("errno",1);
            ret.put("message","上传失败，没有接收到上传的文件");
            return ret;
        }

        // （1）根据时间戳生成照片新文件名
        String newFileName = new UploadUtils().getNewFileName(imageFile,realPath);
        log.info("上传的图片在服务器的路径===="+realPath);
        log.info("上传的图片在服务器对应的文件名称为"+newFileName);

        //（2）生成照片的访问路径
        String imageUrl="http://localhost:8081/imgs/"+newFileName;
        log.info("存储到数据库的图片访问路径为"+imageUrl);

        ret.put("errno",0);

        HashMap<String, Object> dataRet= new HashMap<>();
        dataRet.put("url",imageUrl);
        ret.put("data",dataRet);
        return ret;

    }

}