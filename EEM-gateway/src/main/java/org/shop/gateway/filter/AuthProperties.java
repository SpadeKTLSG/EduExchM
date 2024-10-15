package org.shop.gateway.filter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private List<String> excludePaths;

}
