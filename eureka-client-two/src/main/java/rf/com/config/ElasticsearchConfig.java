package rf.com.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("elasticsearch")
@Data
public class ElasticsearchConfig {
	
	private String scheme;
    private List<String> internalHosts;
    private List<String> externalHosts;
    private int port;
 
    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(internalHosts.get(0), port, scheme),
                new HttpHost(internalHosts.get(1), port, scheme),
                new HttpHost(internalHosts.get(2), port, scheme),
                new HttpHost(externalHosts.get(0), port, scheme),
                new HttpHost(externalHosts.get(1), port, scheme),
                new HttpHost(externalHosts.get(2), port, scheme)
        );

        // Use the internal hosts if the request is coming from the internal network, otherwise use the external hosts
        builder.setNodeSelector(new NodeSelector() {
            @Override
            public void select(Iterable<Node> nodes) {
                if (isRequestFromInternalNetwork()) {
                    List<Node> internalNodes = new ArrayList<>();
                    for (Node node : nodes) {
                        if (internalHosts.contains(node.getHost())) {
                            internalNodes.add(node);
                        }
                    }
                    if (!internalNodes.isEmpty()) {
                        nodes = internalNodes;
                    }
                } else {
                    List<Node> externalNodes = new ArrayList<>();
                    for (Node node : nodes) {
                        if (externalHosts.contains(node.getHost())) {
                            externalNodes.add(node);
                        }
                    }
                    if (!externalNodes.isEmpty()) {
                        nodes = externalNodes;
                    }
                }
            }

        });

        return new RestHighLevelClient(builder);
    }

    private boolean isRequestFromInternalNetwork() {
        String ipAddress = getIpAddress();
        return ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.") || ipAddress.equals("127.0.0.1");
    }

    private String getIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
    
}
