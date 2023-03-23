package hello;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class CommandLineBean {
//    --url=devdb --username=dev_user --password=dev_pw mode=on
    private final ApplicationArguments appArgs;

    public CommandLineBean(ApplicationArguments appArgs) {
        this.appArgs = appArgs;
    }

    @PostConstruct
    public void init() {
        log.info("sourceArgs={}", List.of(appArgs.getSourceArgs()));
        log.info("optionNames={}", appArgs.getOptionNames());

        Set<String> optionNames = appArgs.getOptionNames();
        for (String optionName : optionNames) {
            log.info("optionName {}={}", optionName, appArgs.getOptionValues(optionName));
        }
    }
}
