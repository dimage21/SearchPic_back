package dimage.searchpic.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지
@Target(ElementType.PARAMETER) // 파라미터에만 붙일 수 있게
public @interface CurrentMember {
}