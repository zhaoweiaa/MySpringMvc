package cn.jxau.zhaowei.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyController {
    /**
     * @Author zhaowei
     * @Description //TODO 注册别名
     * @Date 15:34 2018/8/26
     * @Param []
     * @return java.lang.String
     **/
    String value() default "";
}
