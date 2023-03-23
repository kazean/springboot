package hello.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.List;
import java.util.Set;

@Slf4j
public class CommandLineV2 {
//    --url=devdb --username=dev_user --password=dev_pw mode=on
    public static void main(String[] args) {
        for (String arg : args) {
            log.info("arg={}", arg);
        }
        DefaultApplicationArguments appArgs = new DefaultApplicationArguments(args);
        List<String> sourceArgs = List.of(appArgs.getSourceArgs());
        List<String> nonOptionArgs = appArgs.getNonOptionArgs();
        Set<String> optionNames = appArgs.getOptionNames();
        log.info("getSourceArgs={}", sourceArgs);
        log.info("nonOptionArgs={}", nonOptionArgs);
        log.info("optionNames={}", optionNames);

        for (String optionName : optionNames) {
            log.info("optionNames {}={}", optionName, appArgs.getOptionValues(optionName));
        }

        log.info("url={}", appArgs.getOptionValues("url"));
        log.info("username={}", appArgs.getOptionValues("username"));
        log.info("password={}", appArgs.getOptionValues("password"));
        log.info("mode={}", appArgs.getOptionValues("mode"));
    }
}
