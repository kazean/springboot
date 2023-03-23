# [실습] 05-02 자동구성
## 09.순수 라이브러리 사용하기 2
- project-v1-start > project-v1
- hello.controller
```
/hello > "hello"
```

## 10.순수 라이브러리 사용하기 2
- memory-v1.jar, project-v1 적용
- project-v1/libs
- build.gradle
> implementation files('libs/memory-v1.jar')
- hello.config.MemoryConfig
```
@Bean MemoryFinder, MemoryController
```