package cn.jxau.zhaowei.Servlet;

import cn.jxau.zhaowei.annotation.MyController;
import cn.jxau.zhaowei.annotation.MyRequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @ClassName MyDispatcherServlet
 * @Description TODO
 * @Author zhaowei
 * @Date 2018/8/26 15:41
 * @Version 1.0
 */
public class MyDispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();

    private List<String> classNames = new ArrayList<>();

    private Map<String, Object> ioc = new HashMap<>();

    private Map<String, Method> handlerMapping = new HashMap<>();

    private Map<String, Object> controllerMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
//        super.init(config);

        //加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //初始化注解类，扫描用户设置的包
        doScanner(properties.getProperty("scanPackage"));

        //3.拿到扫描到的类,通过反射机制,实例化,并且放到ioc容器中(k-v  beanName-bean) beanName默认是首字母小写
        doInstance();

        initHandlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500!! server Exception");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (handlerMapping.isEmpty()){
            return;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();

        url = url.replace(contextPath, "").replaceAll("/+", "/");

        if(!this.handlerMapping.containsKey(url)){
            resp.getWriter().write("404 not found!");
            return;
        }

        Method method = this.handlerMapping.get(url);

        //获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();

        //获取请求参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        //保存参数列表
        Object[] paramValues = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            //根据参数名处理
            String requestParam = parameterTypes[i].getSimpleName();

            if(requestParam.equals("HttpServletRequest")){
                paramValues[i] = req;
                continue;
            }

            if(requestParam.equals("HttpServletResponse")){
                paramValues[i] = resp;
                continue;
            }

            if(requestParam.equals("String")){
                for(Map.Entry<String, String[]> param : parameterMap.entrySet()){
                    String value =Arrays.toString(param.getValue())
                            .replaceAll("\\[|\\]", "").
                                    replaceAll(",\\s", ",");
                    paramValues[i] = value;
                }
            }
        }

        try {
            //反射调用
            method.invoke(this.controllerMap.get(url), paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void initHandlerMapping() {
        if(ioc.isEmpty()){return;}
        for(Map.Entry<String, Object> entry : ioc.entrySet()){
            Class<? extends Object> clazz = entry.getValue().getClass();

            if(!clazz.isAnnotationPresent(MyController.class)){continue;}

            String baseUrl = "";
            if(clazz.isAnnotationPresent(MyRequestMapping.class)){
               MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
                baseUrl = annotation.value();
            }

            Method[] methods = clazz.getMethods();
            for (Method method : methods){
                if(!method.isAnnotationPresent(MyRequestMapping.class)){
                    continue;
                }
                MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                String url = annotation.value();

                url = (baseUrl + "/" + url).replaceAll("/+", "/");
                handlerMapping.put(url, method);
                try {
                    controllerMap.put(url, clazz.getConstructor().newInstance());
                    System.out.println(url + "," + method);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()){ return;}

        for( String className : classNames ){
            try {
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(MyController.class)){
                    ioc.put(toLowerFirstWord(clazz.getSimpleName()), clazz.getConstructor().newInstance());
                }else{
                    continue;
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                continue;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private String toLowerFirstWord(String simpleName) {
        char[] charArray = simpleName.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

    private void doScanner(String scanPackage) {
        URL url  = this.getClass().getClassLoader().getResource( scanPackage.replaceAll("\\.", "/"));
//        URL url  = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        File[] files = dir.listFiles();
        for ( File file : files ){
            if(file.isDirectory()){
                //递归读取包
                doScanner(scanPackage + "." + file.getName());
            } else {
                String className = scanPackage + "."
                        + file.getName().replace(".class", "");
                classNames.add(className);
            }
        }

    }

    private void doLoadConfig(String contextConfigLocation) {
        try (InputStream inputStream = this.getClass()
                .getClassLoader().getResourceAsStream(contextConfigLocation)){
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
