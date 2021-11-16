package dimage.searchpic.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.persistence.EntityManager;

@Configuration
public class QueryDslConfig {
    @Autowired EntityManager em;
    @Bean
    public JPAQueryFactory querydsl() {
        return new JPAQueryFactory(em);
    }
}