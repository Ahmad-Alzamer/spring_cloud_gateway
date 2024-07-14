import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

public class PasswordEncoderTests {
    @Test
    public void test_not_null(){
        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        var encoded = encoder.encode("test");
        Assertions.assertThat(encoded).isNotNull();
    }
    @Test
    public void test_matches(){
        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        var encoded = encoder.encode("test");
        Assertions.assertThat(encoder.matches("test", encoded )).isTrue();
    }
}
