# 🍳 Recipe 최고 가장한 App

**냉장고 속 재료로 무엇을 만들지 고민될 때, AI가 추천해주는 레시피 앱!**

---

## 🧠 개발 동기

냉장고 속 남은 재료나 유통기한이 임박한 식재료를 효율적으로 소비하고,  
**AI를 활용한 레시피 추천**과 **사용자 간 음식 소통 공간**을 제공하기 위해 개발되었습니다.

---

## 🚀 주요 기능

- 📋 게시판을 통한 사용자 소통 및 정보 공유  
- 🤖 AI 기반 레시피 추천 시스템  
- 🔍 요리 재료 기반 검색 기능 제공

---

## 🛠️ 기술 스택

### 🔧 Backend
- Spring Boot `v3.3.4`
- Spring Security `v6.3.3`
- Spring Data JPA
- JWT (JJWT) `v0.12.3`
- MySQL
- FastAPI `v0.115.6`
- faiss `v1.9.0`
- LangChain `v0.3.11`
- OpenAI API `v0.2.12`

### 💻 Frontend
- Vue.js `v3.2.13`

---

## 🔐 인증 및 보안

- Spring Security + JWT 기반 인증 및 권한 관리
- CSRF, CORS 보안 설정

---

## ⚙️ 공통 로직 및 최적화

- Pagination 시 발생하는 N+1 문제 해결
- Fetch Join, Batch Size 적용
- 게시글 추천 기능에 Optimistic Lock 적용
- RAG 기반 검색을 통한 AI 레시피 정확도 향상

---

## 🧱 아키텍처 구성

![architecture](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FsD6d6%2FbtsNlW4GgCc%2FR9VRHMBtsEO7UOJgiqKC2K%2Fimg.png)

---

## 🗂️ ERD

![ERD](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcibtAM%2FbtsNnn64cGg%2FIdXO32kEQJAgSpfePvrQ40%2Fimg.png)

---

## 🎨 UI 미리보기

| 회원가입 | 로그인 | 메인 화면 |
|----------|--------|-----------|
| ![회원가입](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FRzuPr%2FbtsNiwE56Wf%2FAK2enz6HMRMIkxuK8hfoo0%2Fimg.png) | ![로그인](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbGIkMz%2FbtsNlLbgwwP%2FKvWootwiwPabfG7rOiPXMk%2Fimg.png) | ![메인화면](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbAO2Hl%2FbtsNmC495yB%2F78B8kFvTrjGTuVA58Ci0L1%2Fimg.png) |

| 글 작성 | 게시글 보기 | 레시피 생성 |
|---------|-------------|--------------|
| ![글작성](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FXQZXV%2FbtsNnvYh23H%2FxXUyowFSUSBcHbNOORawB1%2Fimg.png) | ![게시글보기](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FtbW88%2FbtsNjJKKZ1j%2Fms93TZY3dypf9qubQGojA1%2Fimg.png) | ![레시피생성](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fda6NkO%2FbtsNmhAIuO2%2FmABrsa8UMnl2W1xIIqPec0%2Fimg.png)