package dimage.searchpic.config;

import dimage.searchpic.config.auth.AuthInterceptor;
import dimage.searchpic.config.auth.CurrentMember;
import dimage.searchpic.config.auth.CurrentMemberArgumentResolver;
import dimage.searchpic.config.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentMemberArgumentResolver(jwtTokenProvider));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(jwtTokenProvider))
                .addPathPatterns("/**")
                .excludePathPatterns(Arrays.asList("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html",
                       "/webjars/**", "/favicon.ico/**", "/swagger/**", "/configuration/**","/","/csrf"))
                .excludePathPatterns("/login/**");
    }
}