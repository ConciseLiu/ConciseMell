import com.leyou.LeyouUserApplication;
import com.leyou.common.utils.NumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouUserApplication.class)
public class VerifyCodeTest {

    @Autowired
    public StringRedisTemplate redisTemplate;

    public static final String  VERIFY_CODE_REDIS_PRE   =   "verify:code:phone:";
    public static final Integer VERIFY_CODE_LEN         =   4;
    public static final Integer VERIFY_CODE_EXPIRE_TIME =   5;
    @Test
    public void verifyCode() {
        String code = NumberUtils.generateCode(4);
        System.out.println(code);
    }

    @Test
    public void getVerifyCode() {
        Object o = redisTemplate.opsForValue().get(VERIFY_CODE_EXPIRE_TIME + "13310870479");
        System.out.println(o);
        String key = "verify13310118704791111";
        this.redisTemplate.opsForValue().set("key", "1234");
        System.out.println(this.redisTemplate.opsForValue().get(key));
    }
}
