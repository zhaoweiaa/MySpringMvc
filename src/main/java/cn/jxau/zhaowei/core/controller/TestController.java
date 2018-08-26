package cn.jxau.zhaowei.core.controller;

import cn.jxau.zhaowei.annotation.MyController;
import cn.jxau.zhaowei.annotation.MyRequestMapping;
import cn.jxau.zhaowei.annotation.MyRequestParm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName TestController
 * @Description TODO
 * @Author zhaowei
 * @Date 2018/8/26 16:52
 * @Version 1.0
 */
@MyController
@MyRequestMapping("/test")
public class TestController {
    @MyRequestMapping("/doTest")
    public void test1(HttpServletRequest request, HttpServletResponse response, @MyRequestParm("param") String param){
        System.out.println(param);
        try {
            response.getWriter().write("doTest method success! param:" + param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @MyRequestMapping("/doTest2")
    public void test2(HttpServletRequest request, HttpServletResponse response){
        try {
            response.getWriter().write("doTest2 method success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
