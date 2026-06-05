# 🍳 요리 커뮤니티 플랫폼

급증하는 1인 가구의 가장 큰 고민 중 하나는 요리 후 남겨지는 '자투리 식재료'의 처리입니다.
본 서비스는 이러한 식재료 낭비 문제를 해결하기 위해 기획된 웹 애플리케이션입니다.
냉장고 속 재료를 입력하면 AI가 즉석에서 활용 가능한 현실성 있는 레시피를 제안하며,
사용자 간의 레시피 공유 및 소통 기능을 제공하여 자취생과 1인 가구가 함께 식문화를 만들어가는 커뮤니티 공간을 지향합니다.

---

## 🛠️ 기술 스택

### 📌 Backend
- **Spring Boot** `3.3.4`
- **Spring Security** `6.3.3`
- **Spring Data JPA**
- **MySQL**
- **FastAPI** `0.115.6`
- **Faiss** `1.9.0`
- **LangChain** `0.3.11`
- **OpenAI API** `0.2.12`

### 💻 Frontend
- **Vue.js** `3.2.13`

---

## 🎨 주요 화면

### ✅ 인증 화면
| 회원가입 | 로그인 | 메인 화면 |
|----------|--------|-----------|
| ![회원가입](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FRzuPr%2FbtsNiwE56Wf%2FAK2enz6HMRMIkxuK8hfoo0%2Fimg.png) | ![로그인](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbGIkMz%2FbtsNlLbgwwP%2FKvWootwiwPabfG7rOiPXMk%2Fimg.png) | ![메인화면](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbAO2Hl%2FbtsNmC495yB%2F78B8kFvTrjGTuVA58Ci0L1%2Fimg.png) |

---

### 📄 게시판 기능
| 글 작성 | 게시글 보기 |
|---------|-------------|
| ![글작성](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FXQZXV%2FbtsNnvYh23H%2FxXUyowFSUSBcHbNOORawB1%2Fimg.png) | ![게시글보기](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FtbW88%2FbtsNjJKKZ1j%2Fms93TZY3dypf9qubQGojA1%2Fimg.png) |

---

### 🧠 AI 레시피 생성
| 레시피 생성 |
|--------------|
| ![레시피생성](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fda6NkO%2FbtsNmhAIuO2%2FmABrsa8UMnl2W1xIIqPec0%2Fimg.png) |

---

## 📦 아키텍처
![architecture](https://github.com/user-attachments/assets/37845c3a-e52a-4d6f-9922-cf44979cc12b)

---

## 🎯 주요 기능

### 🔐 인증 및 보안
- 로그인: Spring Security + JWT 기반 인증/인가

### 커뮤니티
- 게시글: 추천, 조회수, 이미지 업로드, 글자 커스텀 지원
- 댓글: 계층형 댓글 지원

### 레시피 콘텐츠
- 레시피 추천: AI기반 레시피 추천
- 커뮤니티: 게시글을 통해 음식 얘기를 나눌 수 있는 공간 지원

---

## ⚙️ 핵심 구현

### 1. RAG(Retrieval-Augmented Generation) 시스템 구축을 통한 LLM 답변 신뢰도 향상
- **Problem**: 단순 LLM 호출 시 LLM의 환각 현상과 최신 데이터 부재 문제 발생
- **Action**: 문제를 해결하기 위해 선택지 후보인 ML, 파인튜닝, RAG 중에서 ML/파인 튜닝과 달리 RAG는 모델을 재학습시킬 필요가 없고 Vector DB에 새 데이터를 추가하기만 하면, 다음 검색 때 바로 인덱싱되어 LLM의 컨텍스트로 주입되므로 실시간 데이터 반영에 가장 유리하기에 진입 장벽이 낮고 쉬운 솔루션이라고 판단.
- **Result**: Vector DB 기반 컨텍스트 주입으로, 검증된 레시피 데이터를 기반으로 한 답변 생성 구조 구현

### 2. 비동기 요청 처리 (응답 지연 개선)
- **Problem**: 외부 AI API 호출 지연(2~4초)으로 인해 동기 방식에서 Tomcat 스레드 블로킹 및 동시성 병목 가능성 발생
- **Action**: 기존 동기식 RestTemplate을 걷어내고 리액티브 스택인 WebClient를 도입하여 외부 AI 서버와의 통신을 비동기 논블로킹으로 전환. 외부 AI 서버의 장애가 내부 시스템으로 번지는 것을 막기 위해 타임아웃 설정을 적용
- **Result**: JMeter 기반 로컬 부하 테스트 결과, 동기 방식 대비 평균 응답 속도가 약 9배 개선됨을 확인

### 3. JPA N+1 문제 해결
- **Problem**: 게시글 조회 시 OneToMany 관계에서 연관된 데이터를 가져오기 위한 추가 쿼리가 N번만큼 발생하는 JPA N+1 문제를 식별
- **Action**: Fetch Join 및 Batch Size 최적화 전략을 병행 수립
- **Result**: 수십 번 분할되어 무분별하게 발생하던 DB 조회 쿼리를 단 1~2회의 쿼리로 압축, 대량 데이터 조회 시 발생하는 데이터베이스 I/O 병목을 제거함으로써, API 응답 속도를 개선하고 DB 서버의 CPU 자원 효율성을 대폭 끌어올림