package cn.jxau.zhaowei.annotation;

import java.lang.annotation.*;

/**
 * @ClassName MyRequestParm
 * @Description TODO
 * @Author zhaowei
 * @Date 2018/8/26 15:38
 * @Version 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParm {
    String value();
}
