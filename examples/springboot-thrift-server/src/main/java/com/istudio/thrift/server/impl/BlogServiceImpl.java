package com.istudio.thrift.server.impl;

import com.istudio.thrift.service.Blog;
import com.istudio.thrift.service.BlogService;
import com.istudio.thrift.service.UserProfile;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ACA on 2017-5-26.
 */
@Component
@Log4j
public class BlogServiceImpl implements BlogService {

    @Override
    public UserProfile find(int id) {

        log.info("blog service: find " + id);

        UserProfile profile = new UserProfile();
        profile.setId(id);
        profile.setName("name" + id);

        List<Blog> blogs = new ArrayList<>();
        for(int pos = 0; pos < 10; pos++) {

            Blog blog = new Blog();
            blog.setTitle("blog title");
            blog.setContent("blog content");
            blogs.add(blog);
        }

        profile.setBlogs(blogs);

        return profile;
    }
}
