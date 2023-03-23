# [실습] 05-02 자동구성
## 11. 자동 구성 라이브러리 만들기
- settings.gradle
> rootProject.name = 'memory-v2' 수정
- memory.MemoryAutoConfig
```
@AutoConfiguration
@ConditionalOnPropery(name = "memory", havingValue = "on")
@Bean MemoryFinder, MemoryController
```
- 자동 구성 대상 지정
> resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
>> memory.MemoryAutoConfig

- 빌드