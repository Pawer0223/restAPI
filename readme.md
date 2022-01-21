- [참조 강의](#-----)
- [What?](#what?)
- [Why?](#why?)
    + [Self-Describtive Message](#self-describtive-message)
    + [Hateoas](#hateoas)
- [How To?](#how-to-)
    + [Self-Describtive Message](#self-describtive-message-1)
      - [1. 미디어 타입을 정의하고 IANA에 등록하여 리턴의 Content-Type으로 사용할 수 있다.](#1--------------iana-----------content-type-----------)
      - [2. 응답 헤더에 링크를 추가한다.](#2----------------)
      - [3. HAL의 링크 데이터에 profile링크를 추가 (2번의 불안정성을 해결하기 위한 대안)](#3-hal----------profile--------2--------------------)
    + [Hateoas](#hateoas-1)
      - [1. 데이터에 링크를 나타내는 필드를 추가.](#1---------------------)
      - [2. 링크 헤더나 Location을 제공.](#2--------location----)
- [Need To?](#need-to-)
      - [응답 데이터에 link표현을 공통적으로 처리하기위한 라이브러리.](#--------link----------------------)
      - [Self-Describtive하기 위해서는 API를 설명할 수 있는 문서가 필요하다.](#self-describtive--------api-------------------)
- [결과 예시](#-----)
      - [Situation](#situation)
      - [Json Result](#json-result)
      - [Self-Describtive 확인](#self-describtive---)
- [Environment](#environment)
      - [Dev](#dev)
      - [Real](#real)
      - [Dependencies](#dependencies)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>

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
~~~ groovy
    implementation 'org.springframework.boot:spring-boot-starter-hateoas'
~~~

#### Self-Describtive하기 위해서는 API를 설명할 수 있는 문서가 필요하다.
- 이 문서에 대한 정의는 개발자가 직접해야 한다.
- 다만 이러한 문서화 작업을 편리할 수 있게 도와주는 라이브러리를 사용하자.
- gradle 의존성 추가
~~~ groovy
    testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'
~~~

# 결과 예시

#### Situation
- 로그인 한 사용자가 새로운 이벤트 정보를 등록한다.

#### Json Result
- `_links` 필드를 통해 hateoas를 만족.
- `_links.profile` 필드를 통해 self-describtive를 만족
  - 해당 필드의 링크를 통해, 현재의 응답을 이해할 수 있는 문서를 확인할 수 있다.(restdocs를 통해 만들어 놓아야 함)
~~~ json
{
  "id" : 73,
  "name" : "spring",
  
  ... (중략) ...
  
  "basePrice" : 100,
  "maxPrice" : 200,
  "limitOfEnrollment" : 100,
  "offline" : true,
  "free" : false,
  "eventStatus" : "DRAFT",
  "manager" : {
    "id" : 72
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/api/events/73"
    },
    "query-events" : {
      "href" : "http://localhost:8080/api/events"
    },
    "update-event" : {
      "href" : "http://localhost:8080/api/events/73"
    },
    "profile" : {
      "href" : "/docs/index.html#resources-events-create"
    }
  }
}
~~~

#### Self-Describtive 확인
![image](https://user-images.githubusercontent.com/26343023/150488777-b4368b33-6a8b-4191-81ca-f04174cd3318.png)

# Environment
<img src="https://img.shields.io/badge/SpringBoot-2.6.2-rgb(243, 156, 18).svg" /> <img src="https://img.shields.io/badge/gradle-7.3.2 -rgb(243, 156, 18).svg" /> <img src="https://img.shields.io/badge/Junit-5.8.2-rgb(243, 156, 18).svg" />

#### Dev
<img src="https://img.shields.io/badge/h2-blue.svg" />

#### Real
<img src="https://img.shields.io/badge/postgresql-red.svg" />

#### Dependencies
- <a href="https://github.com/Pawer0223/restAPI/blob/master/build.gradle">build.gradle</a>
