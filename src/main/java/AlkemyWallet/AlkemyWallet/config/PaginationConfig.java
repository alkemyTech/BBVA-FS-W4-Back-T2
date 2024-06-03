package AlkemyWallet.AlkemyWallet.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:pagination.properties")
public class PaginationConfig {

    @Value("${users.per.page}")
    private int usersPerPage;

}