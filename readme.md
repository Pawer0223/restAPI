# 참조 강의
- https://www.inflearn.com/course/spring_rest-api/dashboard
- https://gitlab.com/whiteship

# What?
- Self-Describtive Message와 Hateoas를 만족하는 REST API 개발하기

# Why?
현재 REST API로 불리는 대부분의 API가 Self-Describtive Message와 Hateoas를 만족하지 않는다.

### Self-Describtive Message
- Http의 응답이 무엇을 의미하는지 스스로 설명이 가능해야 한다.

### Hateoas
- 하이퍼미디어(링크)를 통해 애플리케이션 상태 변화가 가능해야 한다.
- 즉, 상태에 따라 이동가능한 링크정보가 응답에 포함되어 있어야한다.
  - 비 로그인 회원은 회원가입을 할 수 있는 링크정보를 응답에서 확인할 수 있어야 한다.
  - 로그인 회원은 이벤트를 등록 할 수 있는 링크정보를 응답에서 확인할 수 있어야 한다.
  - 즉, 로그인회원과 비 로그인 회원이 이동할 수 있는 링크정보는 다르다.

# How To?
### Self-Describtive Message

#### 1. 미디어 타입을 정의하고 IANA에 등록하여 리턴의 Content-Type으로 사용할 수 있다.
#### 2. 응답 헤더에 링크를 추가한다.
- 자기 자신을 나타내는 링크를 의미하는 필드명은 profile 이다.
- 하지만 이 방식은 스팩을 지원하지 않는 브라우저들이 있기 때문에 불안정하다.
#### 3. HAL의 링크 데이터에 profile링크를 추가 (2번의 불안정성을 해결하기 위한 대안)
- <a href="https://stateless.group/hal_specification.html">HAL</a>이란 쉽게 응답포맷에 링크를 나타내는 필드를 추가한 JSON포맷.
- content-type: application/hal+json

3번 방법을 사용.

### Hateoas

#### 1. 데이터에 링크를 나타내는 필드를 추가.
#### 2. 링크 헤더나 Location을 제공.

1번 방법을 사용.

# Need To?
#### 응답 데이터에 link표현을 공통적으로 처리하기위한 라이브러리.
- 코드의 가독성과 일관성을 높일 수 있다.
- gradle 의존성 추가
~~~
    implementation 'org.springframework.boot:spring-boot-starter-hateoas'
~~~

#### Self-Describtive하기 위해서는 API를 설명할 수 있는 문서가 필요하다.
- 이 문서에 대한 정의는 개발자가 직접해야 한다.
- 다만 이러한 문서화 작업을 편리할 수 있게 도와주는 라이브러리를 사용하자.
- gradle 의존성 추가
~~~
    testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'
~~~

# Environment
