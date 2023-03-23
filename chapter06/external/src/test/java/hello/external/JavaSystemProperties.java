package hello.external;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class JavaSystemProperties {
//     -Durl=devdb -Dusername=dev_user -Dpassword=dev_pw
    public static void main(String[] args) {
        Properties properties = System.getProperties();
        for (Object key : properties.keySet()) {
            log.info("JavaSystemProperty {}={}", key, System.getProperty(String.valueOf(key)));
        }

        log.info("url={}", System.getProperty("url"));
        log.info("username={}", System.getProperty("username"));
        log.info("password={}", System.getProperty("password"));
    }
}
