import com.example.spring_cloud_gateway.config.SecurityConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class TestHeirarchalRoles {
    RoleHierarchy h = new SecurityConfig().roleHierarchy();
    @Test
    public void test(){

        var result = h.getReachableGrantedAuthorities(List.of(new SimpleGrantedAuthority("read")));
        Assertions.assertThat(result)
                .matches( grantedAuthorities -> grantedAuthorities.stream().anyMatch(grantedAuthority -> "read".equals(grantedAuthority.getAuthority())));

        result = h.getReachableGrantedAuthorities(List.of(new SimpleGrantedAuthority("super-user")));
        Assertions.assertThat(result)
                .matches( grantedAuthorities -> grantedAuthorities.stream().anyMatch(grantedAuthority -> "read".equals(grantedAuthority.getAuthority())));

        result = h.getReachableGrantedAuthorities(List.of(new SimpleGrantedAuthority("super-user"),new SimpleGrantedAuthority("read")));
        Assertions.assertThat(result)
                .matches( grantedAuthorities -> grantedAuthorities.stream().anyMatch(grantedAuthority -> "read".equals(grantedAuthority.getAuthority())));

    }

}
