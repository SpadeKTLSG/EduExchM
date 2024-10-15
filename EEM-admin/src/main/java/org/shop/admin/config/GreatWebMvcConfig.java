package org.shop.admin.config;


import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.admin.common.interceptor.AdminRefreshTokenInterceptor;
import org.shop.admin.common.interceptor.GreatLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * WebMvc配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class GreatWebMvcConfig implements WebMvcConfigurer {


    private final StringRedisTemplate stringRedisTemplate;


    /**
     * 注册自定义拦截器: 用户类型包括管理员端和用户端
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        log.debug("自定义管理员端拦截器启动");

        //登录拦截器
        registry.addInterceptor(new GreatLoginInterceptor())

                .excludePathPatterns("/guest/**", // 用户端
                        "/admin.html", "/swagger-ui/**", "/swagger-ui.html", "/doc.html", "/webjars/**", "/swagger-resources/**", "/swagger-ui/**", "/v3/**", "/error", // swagger 3.0 (坑死了)
                        "/admin/employee/login",
                        "/admin/employee/register",
                        "/admin/employee/code"
                ).order(1);

        // token刷新拦截器
        registry.addInterceptor(new AdminRefreshTokenInterceptor(stringRedisTemplate))

                .addPathPatterns("/**")
                .excludePathPatterns("/guest/**",  // 用户端
                        "/admin.html", "/swagger-ui/**", "/swagger-ui.html", "/doc.html", "/webjars/**", "/swagger-resources/**", "/swagger-ui/**", "/v3/**", "/error", // swagger 3.0 (坑死了)
                        "/admin/employee/login",
                        "/admin/employee/register",
                        "/admin/employee/code"
                ).order(0);
    }


    /**
     * 配置消息转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 默认第0个转换器是StringHttpMessageConverter，处理String数据的转换
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(1, new ByteArrayHttpMessageConverter());// 默认第1个转换器是ByteArrayHttpMessageConverter，处理byte[]数据的转换(springdoc问题)
        converters.add(2, fastJsonHttpMessageConverter);// 添加FastJson的转换器, 放在第2个位置
    }


    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/").resourceChain(false);
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
