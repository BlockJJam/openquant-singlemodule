# <span style="color:#0077ff">Open Quant Spring Application


> π **INFO**  
> ν΄λΉ μ νλ¦¬μΌμ΄μμ κΈ°μ μ κ· μ¬μ νλ‘μ νΈμμ **νλ‘ν νμ λ²μ μ REST API** μλ²μμ΅λλ€. νμ¬, ν΄λΉ **μ κ· μ¬μμ΄ μ’λ£**λκ³  **μ¬μ λͺ©μ μΌλ‘ νμ©νμ§ μμ** ν νλ½νμ μ λ΄νλ Spring Boot μμ€μ½λ μμ κ° κ°λ₯ν΄μ‘κ³ , μ΄λ₯Ό κ°μΈ λ¦¬ν¬μ§ν λ¦¬μ μ μ₯νκ² λμμ΅λλ€.


> π‘ **μ£Όμ!**  
> ν΄λΉ μ νλ¦¬μΌμ΄μμ RESTful API Serverλ‘, View file(static file)μ μ κ³΅νμ§ μμ΅λλ€. λ°μ μ΄λ―Έμ§λ€μ Vue.js κ°λ°μμ ν¨κ» νλ‘μ νΈν κ²°κ³Όλ¬Όμλλ€.

### <span style="color:#0077ff">βοΈ Open Quant Objective

- <u>**μ€ν ννΈ**</u>λ ννΈ νΉμ μκ³ λ¦¬μ¦ νΈλ μ΄λ©κ³Ό κ΄λ ¨λ κΈ°μ κ³Ό μ΄μλ€μ λ€λ£¨μ΄ **κ³ κ°μκ² μλ‘μ΄ ν¬μ κ²½νμ μ κ³΅νλ νλ«νΌ μλΉμ€**λ‘, μΈνμ°μ€ νΈλ μ΄λ© μμ€ν μμ€μ κΈ°λ₯κ³Ό μμ λλ₯Ό λ³΄μ₯νλ νλ«νΌμ μ κ³΅ν©λλ€.    


### <span style="color:#0077ff">π΅ Spring Application Objective

- **Spring Application**μ **νΈλ μ΄λ© μμ€νμ νμν λ€μν μ λ³΄**λ₯Ό κ°κ³΅νμ¬ μ κ³΅νκ³  **ννΈ μ λ΅μ© κ°λ° λͺ¨λμ μ¬κ³  ν μ μλ λ§μΌ μμ€ν**μ κ΅¬μΆν¨μΌλ‘μ¨, κ³ κ°μ΄ ν¬μ μ λ΅μ μ½κ² μ€κ³ν  μ μλλ‘ λ°μ΄ν°λ₯Ό μ κ³΅νλ κ²μ΄ μ΅μ’ λͺ©νμλλ€.

## <span style="color:#0077ff">π Spec


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

<u>**Feature 1.** μ¬μ©μ κ΄λ¦¬ API</u>
<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193516456-f382ab5b-47f0-4764-9967-5f861b11c701.gif" width="75%" height="75%">
</div>  

- **μ¬μ©μ κ΄λ¦¬ API μ£Όμ κΈ°λ₯**
  - μ¬μ©μ λ‘κ·ΈμΈ
  - μ¬μ©μ νμκ°μ
  - μ¬μ©μ κ°μΈμ λ³΄ μμ 

- **Spring Security**μ **Jwt**λ₯Ό νμ©νμ¬, **ν ν° κΈ°λ°μ μ¬μ©μ μΈμ¦/μΈκ° κΈ°λ₯**μ κ΅¬ν
  - Spring Securityλ₯Ό ν΅ν΄ DBμ λ±λ‘λ μ¬μ©μ μ λ³΄λ₯Ό κ΄λ¦¬
  - μΈμ¦μ μν΄, TokenProviderλ₯Ό ν΅ν΄ ν ν°μ λ°κΈνλ μμΉμ Spring Securityκ° κ΄λ¦¬νλ κ°μ²΄λ₯Ό μ¬μ©
  - μΈκ°λ₯Ό μν΄, Spring Securityμμ μ κ³΅νλ μΈκ° μ λνμ΄μμ νμ©ν΄ μ κ·Όμ μ ν
  ```java 
  // μΈκ° μ²λ¦¬λ°©μ κ°λ¨ μκ°
  @RequestMapping("/api/wiki/")
  @PreAuthorize("hasAnyRole('ADMIN')")  //ν΄λΉ ν΄λμ€λ "admin"κΆνμ΄ μμ΄μΌ μ κ·Όμ΄ κ°λ₯ 
  public class WikiAdminController {
      private final WikiService wikiService;
  }
  ```



<u>**Feature 2.** Market Data μ κ³΅ API</u>

<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193506800-2e1c6fd3-d5fd-4641-ae07-aa7608d9722c.gif" width="75%" height="75%">
</div>

- λͺ¨λ  μ£Όμ μ’λͺ©μ΄λ¦ μ λ³΄ λ¦¬μ€νΈ μ‘°ν API
- λ¨μΌ μ’λͺ© κ³Όκ±° μΌλ΄ λ°μ΄ν° λ¦¬μ€νΈ μ‘°ν API
  - **νμ΄μ§ μ²λ¦¬**λ₯Ό ν΅ν΄ λ°μ΄ν° νμ μμ μ λ°μ΄ν° λ‘λ μ±λ₯ μ΅μ ν

<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193516965-48b03b13-e5bd-4d08-824f-86e98e78e16c.gif" width="75%" height="75%">
</div>

- κΈ°κ°λ³ μμ΅λ₯  TOP20 νμ΄λΈ μ‘°ν API
  - **Ehcache**λ₯Ό νμ©ν **κ³ μ  μλ΅ λ°μ΄ν° μΊμ±**
  - λ©λͺ¨λ¦¬ 20λΆ μ μ₯ λ° νμ 1MB μ μ₯μΌλ‘ μ ν
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
  - **DB νλ‘μμ **(= PostgreSQL function) νΈμΆμ ν΅ν **μΏΌλ¦¬ μ°μ° μλ κ°μ **
  ```java
    // domainμμ PostgreSQLμ functionμΈ "tb_returns()"λ₯Ό νΈμΆνλ NamedNativeQueryλ₯Ό νΈμΆ
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
  
    // repositoryμμλ domainμ μ€μ λ @NamedNativeQueryμ nameμ΅μμ κ°μ κ·Έλλ‘ λ©μλ μ΄λ¦μΌλ‘ μ§μ  κ°λ₯
    @Query(nativeQuery = true)
    List<ReturnsDto> findReturnsTop20OrderByTerm(@Param("term") String term);
  ```
  



<u>**Feature 3.** κ³Όκ±° μμΈ μ κ³΅ API</u>
<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193517191-d35ac22d-fc3f-47b5-bea3-55cabed0a03a.gif" width="75%" height="75%">
</div>  

- κ³Όκ±° μμΈ μ κ³΅ APIμ 4κ°μ§ κΈ°λ₯
  - μ°¨νΈμ© κ³Όκ±° μΌλ΄ λ°μ΄ν° μ‘°ν κΈ°λ₯
  - μ°¨νΈμ© κ³Όκ±° λΆλ΄ λ°μ΄ν° μ‘°ν κΈ°λ₯
  - ν¬μ μ λ΅ μκ³ λ¦¬μ¦μ© κ³Όκ±° μΌλ΄ λ°μ΄ν° μ‘°ν κΈ°λ₯
  - ν¬μ μ λ΅ μκ³ λ¦¬μ¦μ© κ³Όκ±° λΆλ΄ λ°μ΄ν° μ‘°ν κΈ°λ₯
- νλ©΄μ λ³΄μ΄λ κΈ°λ₯ μ€λͺ
  - μμ **κ³Όκ±° μμΈ μ κ³΅ APIλ₯Ό νμ©**ν μ λ΅ μλ?¬λ μ΄μμ μ€ννμ¬, **μ£Όμμ μ¬κ±°λ νλ μμ μ λνλ΄λ μκ·Έλ**μ΄ μ°¨νΈμ κ·Έλ €μ§ λͺ¨μ΅



<u>**Feature 4.** μν€ API</u>
<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193517258-429a98f1-2efa-4753-abd7-3eb8c1747af3.gif" width="75%" height="75%">
</div>  

- μν€ APIμ **μΌλ° μ¬μ©μ κΆν** μ κ·Ό κ°λ₯ κΈ°λ₯
  - μΉ΄νκ³ λ¦¬ λ° μλΈ μΉ΄νκ³ λ¦¬ μ‘°ν λ° μ ν κΈ°λ₯
  - ν¬μ€νΈ μ‘°ν λ° λͺ©μ°¨ μ ν κΈ°λ₯
- ν¬μ€νΈμ λͺ©μ°¨λ **ν¬μ€νΈλ§λ€ Header(μ λͺ© λ¬Έμ) κ°μ²΄λ₯Ό λ΄μ λ¦¬μ€νΈ νν**λ‘ λ³΄κ΄
  - λ¦¬μ€νΈλ‘ λ΄κΈ΄ λͺ©μ°¨ λ°μ΄ν° μ μ²΄λ₯Ό λ¬Έμμ΄ νμμΌλ‘ DB νμ΄λΈ λ μ½λμ μ μ₯

<div style="float: right"><img src="https://user-images.githubusercontent.com/57485510/193517299-66843184-9c10-488b-9f56-fd841aa6f072.gif" width="75%" height="75%">
</div>  

- μν€ APIμ **κ΄λ¦¬μ κΆν** μ κ·Ό κ°λ₯ κΈ°λ₯(μΌλ° μ¬μ©μκ° μ κ·Ό κ°λ₯ν κΈ°λ₯μ μ μΈν μΆκ° κΈ°λ₯)
  - μΉ΄νκ³ λ¦¬ λ° μλΈ μΉ΄νκ³ λ¦¬ μμΉ μμ  κΈ°λ₯
  - μΉ΄νκ³ λ¦¬ λ° μλΈ μΉ΄νκ³ λ¦¬ μ΄λ¦ μμ  λ° μ­μ  κΈ°λ₯
  - ν¬μ€νΈ μμ±, μμ , μ­μ  κΈ°λ₯



## νλ‘μ νΈ κ°μ  μ¬ν­

- Exceptionμ Exception Adviceλ‘ **μ²΄ν¬μμΈ**λ₯Ό μ²λ¦¬νλλ°, **μΈμ²΄ν¬ μμΈ**μ λν΄μλ μΈμ¬νκ² κ³ λ €νμ§ λͺ»νκΈ° λλ¬Έμ κ°μ μ λν νμμ±μ λκΌμ΅λλ€.
- μμΈ λ°μ΄ν°λ₯Ό μ‘°ννλ μλΉμ€μ νΉμ± μ, **λμ©λ λ°μ΄ν°λ₯Ό μ‘°ννλ μ±λ₯**μ΄ μ€μνμ§λ§ μμ§κΉμ§ **Jmeter**μ κ°μ λΆννμ€νΈλ₯Ό μ§ννμ§ μμκΈ° λλ¬Έμ **μ±λ₯ λΆν νμ€νΈ ν΄μ ν΅ν΄ κ²μ¦**ν΄μΌ νλ€κ³  μκ°ν©λλ€.
- νμ€νΈ μ½λμ μμ΄ μλΉμ€λ³λ‘ λΆμ‘±νκ±°λ, μλ κ²½μ°λ μμ΅λλ€. κ΅¬ννλ κΈ°λ₯μ μμ΄μ λ€μν μν©μ κ³ λ €ν **νμ€νΈ μ½λλ₯Ό λλ €μΌν  νμμ±**μ΄ μλ€κ³  νλ¨λ©λλ€.