# 🍳 AI 레시피 추천 & 요리 커뮤니티 웹 애플리케이션

냉장고 속 재료와 유통기한 정보를 기반으로,  
**AI가 현실성 있는 레시피를 추천**하고  
**사용자 간 레시피 공유 및 소통**이 가능한 웹 애플리케이션입니다.

---

## 📌 프로젝트 개요

| 항목 | 내용 |
|------|------|
| **프로젝트명** | Recipe App |
| **목표** | 재료 기반 AI 레시피 추천 + 커뮤니티 기능 제공 |
| **작업 기간** | 2024.09 ~ 2025.01 |
| **인원 구성** | 개인 프로젝트 (1명) |
| **저장소** | [GitHub](https://github.com/leesanghak123/recipeProject) |

---

## 🎯 개발 목적

- 냉장고 속 재료를 효율적으로 소비하고 음식물 쓰레기를 줄이기 위해 설계  
- **RAG 기반 AI**를 활용해 잘못된 정보(환각 현상)를 줄이고 현실성 있는 레시피 제공  
- 커뮤니티형 게시판을 통한 사용자 간 소통 공간 마련  

---

## 🛠️ 기술 스택

### 📌 Backend
- **Spring Boot** `3.3.4`
- **Spring Security** `6.3.3` (JWT)
- **Spring Data JPA**
- **MySQL**
- **FastAPI** `0.115.6`
- **Faiss** `1.9.0`
- **LangChain** `0.3.11`
- **OpenAI API** `0.2.12`

### 💻 Frontend
- **Vue.js** `3.2.13`

---

## 🔐 인증 및 보안
- Spring Security + JWT 기반 인증/인가
- CSRF, CORS 보안 설정
- 게시판 권한 제어

---

## ⚙️ 성능 최적화 & 문제 해결

### 1. RAG 적용 (정확도 향상)
- **상황**: 단순 LLM 호출 시 부정확한 레시피 제안 발생 (환각 현상)
- **접근**: Faiss + LangChain 기반 RAG 적용, 사전 DB 검색 후 LLM 응답
- **결과**: 잘못된 추천 감소, 현실성 있는 레시피 제공

### 2. 비동기 요청 처리 (응답 지연 개선)
- **상황**: AI API 호출 시 2~4초 지연 발생
- **접근**: Spring Boot & FastAPI에서 `WebClient` + `@Async` 비동기 처리, Timeout 설정
- **결과**: 대량 트래픽 시 처리 성능 **900% 개선** (JMeter 부하 테스트 기준)

### 3. JPA N+1 문제 해결
- **상황**: 게시글 조회 시 연관 관계로 인한 쿼리 폭증
- **접근**: `Fetch Join` + `Batch Size` 적용
- **결과**: 동일 요청 시 쿼리 수 대폭 감소, 조회 성능 향상

---

## 📦 아키텍처
![architecture](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https://blog.kakaocdn.net/dn/sD6d6/btsNlW4GgCc/R9VRHMBtsEO7UOJgiqKC2K/img.png)

---

## 🗂️ ERD
![ERD](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbrRfyq%2FbtsOlwW7VkN%2FAAAAAAAAAAAAAAAAAAAAANSTDWiilPD9TkTFvhv0jAHNqJZpdUUsO5oo7p5Hrdqt%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3D5GXvYXgr9GZtf1BaZeh8ez%252BnSG0%253D)

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
| 레시피 생성 | 레시피 생성 중 |
|--------------|-----------------|
| ![레시피생성](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fda6NkO%2FbtsNmhAIuO2%2FmABrsa8UMnl2W1xIIqPec0%2Fimg.png) | ![레시피생성중](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FUqQcd%2FbtsOUWB9j2n%2FAAAAAAAAAAAAAAAAAAAAAC5xK6yDRGoWYy7xZFN7VnAEIbsWH6j6YwEqGFqhM5pS%2Fimg.png) |

---

## 📝 배운 점
- RAG 구조를 활용한 AI 서비스 정확도 개선 경험
- 비동기 요청 처리로 서비스 응답 속도 및 안정성 확보
- JPA 성능 최적화 기법(Fetch Join, Batch Size) 실전 적용