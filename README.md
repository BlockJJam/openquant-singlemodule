# <span style="color:#0077ff">Open Quant Spring Application


> 📘 INFO  
> 해당 애플리케이션은 기업 신규 사업 프로젝트에서 **프로토타입 버전의 REST API** 서버였습니다. 현재, 해당 **신규 사업이 종료**되고 **사업 목적으로 활용하지 않아** 팀 허락하에 전담했던 Spring Boot 소스코드 소유가 가능해졌고, 이를 개인 리포지토리에 저장하게 되었습니다.
> 

### <span style="color:#0077ff">☕️ Open Quant Objective

- <u>**오픈 퀀트**</u>는 퀀트 혹은 알고리즘 트레이딩과 관련된 기술과 이슈들을 다루어 **고객에게 새로운 투자 경험을 제공하는 플랫폼 서비스**로, 인하우스 트레이딩 시스템 수준의 기능과 자유도를 보장하는 플랫폼을 제공합니다.    


### <span style="color:#0077ff">🍵 Spring Application Objective

- **Spring Application**은 **트레이딩 시스템에 필요한 다양한 정보**를 가공하여 제공하고 **퀀트 전략용 개발 모듈을 사고 팔 수 있는 마켓 시스템**을 구축함으로써, 고객이 투자 전략을 쉽게 설계할 수 있도록 데이터를 제공하는 것이 최종 목표입니다.

## <span style="color:#0077ff">👜 Spec


<u>**Spring**</u>
- Java :11
- Spring Boot :2.5.3
- Spring Boot Starter :2.5.3  
  - Spring Data Jpa, Spring Security, Log4j2, Cache  
- Ehcache :3.8.0  
- QueryDsl :4.4.0  
- Restdoc :2.0.5  
- jwt :0.11.2

<u>**PostgreSQL**</u>
- 4 version


## <span style="color:#0077ff"> Application Feature

<u>**Feature 1.** 사용자 관리 API</u>
<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193516456-f382ab5b-47f0-4764-9967-5f861b11c701.gif" width="75%" height="75%">
</div>  

- **사용자 관리 API 주요 기능**
  - 사용자 로그인
  - 사용자 회원가입
  - 사용자 개인정보 수정

- **Spring Security**와 **Jwt**를 활용하여, **토큰 기반의 사용자 인증/인가 기능**을 구현
  - Spring Security를 통해 DB에 등록된 사용자 정보를 관리
  - 인증을 위해, TokenProvider를 통해 토큰을 발급하는 위치에 Spring Security가 관리하는 객체를 사용
  - 인가를 위해, Spring Security에서 제공하는 인가 애너테이션을 활용해 접근을 제한
  ```java 
  // 인가 처리방식 간단 소개
  @RequestMapping("/api/wiki/")
  @PreAuthorize("hasAnyRole('ADMIN')")  //해당 클래스는 "admin"권한이 있어야 접근이 가능 
  public class WikiAdminController {
      private final WikiService wikiService;
  }
  ```



<u>**Feature 2.** Market Data 제공 API</u>

<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193506800-2e1c6fd3-d5fd-4641-ae07-aa7608d9722c.gif" width="75%" height="75%">
</div>

- 모든 주식 종목이름 정보 리스트 조회 API
- 단일 종목 과거 일봉 데이터 리스트 조회 API
  - **페이징 처리**를 통해 데이터 필요 시점에 데이터 로드 성능 최적화

<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193516965-48b03b13-e5bd-4d08-824f-86e98e78e16c.gif" width="75%" height="75%">
</div>

- 기간별 수익률 TOP20 테이블 조회 API
  - **Ehcache**를 활용한 **고정 응답 데이터 캐싱**
  - 메모리 20분 저장 및 힙에 1MB 저장으로 제한
  ```xml
    ... 
    <cache-template name="quantDefaultTemplate">
        <expiry>
            <ttl unit="minutes">20</ttl>
        </expiry>
        <heap unit="MB">1</heap>
    </cache-template>
    ...
    ```
    ```java
    //...
    @Cacheable(cacheNames="getReturnsOfStockPrice", key="#date")
    public PriceDto.ReturnsList getReturnsTop20WithDBFunction(String date) {

        PriceDto.ReturnsList retList = new PriceDto.ReturnsList();
        retList.changeReturnsData(
                dayDataRepository.findReturnsTop20OrderByTerm(TermConvertType.find(date).getConvertedTerm()));

        return retList;
    }
    //...
  ``` 
  - **DB 프로시저**(= PostgreSQL function) 호출을 통한 **쿼리 연산 속도 개선**
  ```java
    // domain에서 PostgreSQL의 function인 "tb_returns()"를 호출하는 NamedNativeQuery를 호출
    @NamedNativeQuery(name="DayData.findReturnsTop20OrderByTerm",
          query="select r.code as code, r.name as name, r.weekly as W1, r.monthly as M1, r.monthly3 as M3, r.monthly6 as M6, r.annually as Y1, r.annually3 as Y3 " +
                  "from openquant.tb_returns() r " +
                  //...
          resultSetMapping = "Mapping.ReturnsDto")
      @SqlResultSetMappings({
          @SqlResultSetMapping(
            name = "Mapping.ReturnsDto",
            // ...
          )
    })
    public class DayData {
      // ...
    }
  
    // repository에서는 domain에 설정된 @NamedNativeQuery의 name옵션의 값을 그대로 메서드 이름으로 지정 가능
    @Query(nativeQuery = true)
    List<ReturnsDto> findReturnsTop20OrderByTerm(@Param("term") String term);
  ```
  



<u>**Feature 3.** 과거 시세 제공 API</u>
<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193517191-d35ac22d-fc3f-47b5-bea3-55cabed0a03a.gif" width="75%" height="75%">
</div>  

- 과거 시세 제공 API의 4가지 기능
  - 차트용 과거 일봉 데이터 조회 기능
  - 차트용 과거 분봉 데이터 조회 기능
  - 투자 전략 알고리즘용 과거 일봉 데이터 조회 기능
  - 투자 전략 알고리즘용 과거 분봉 데이터 조회 기능
- 화면에 보이는 기능 설명
  - 위의 **과거 시세 제공 API를 활용**한 전략 시뮬레이션을 실행하여, **주식을 사거나 파는 시점을 나타내는 시그널**이 차트에 그려진 모습



<u>**Feature 4.** 위키 API</u>
<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193517258-429a98f1-2efa-4753-abd7-3eb8c1747af3.gif" width="75%" height="75%">
</div>  

- 위키 API의 **일반 사용자 권한** 접근 가능 기능
  - 카테고리 및 서브 카테고리 조회 및 선택 기능
  - 포스트 조회 및 목차 선택 기능
- 포스트의 목차는 **포스트마다 Header(제목 문자) 객체를 담은 리스트 형태**로 보관
  - 리스트로 담긴 목차 데이터 전체를 문자열 타입으로 DB 테이블 레코드에 저장

<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193517299-66843184-9c10-488b-9f56-fd841aa6f072.gif" width="75%" height="75%">
</div>  

- 위키 API의 **관리자 권한** 접근 가능 기능(일반 사용자가 접근 가능한 기능을 제외한 추가 기능)
  - 카테고리 및 서브 카테고리 위치 수정 기능
  - 카테고리 및 서브 카테고리 이름 수정 및 삭제 기능
  - 포스트 생성, 수정, 삭제 기능



## 프로젝트 개선 사항

- Exception의 Exception Advice로 **체크예외**를 처리하는데, **언체크 예외**에 대해서는 세심하게 고려하지 못했기 때문에 개선에 대한 필요성을 느꼈습니다.
- 시세 데이터를 조회하는 서비스의 특성 상, **대용량 데이터를 조회하는 성능**이 중요하지만 아직까지 **Jmeter**와 같은 부하테스트를 진행하지 않았기 때문에 **성능 부하 테스트 툴을 통해 검증**해야 한다고 생각합니다.
- 테스트 코드의 양이 서비스별로 부족하거나, 없는 경우도 있습니다. 구현하는 기능에 있어서 다양한 상황을 고려한 **테스트 코드를 늘려야할 필요성**이 있다고 판단됩니다.