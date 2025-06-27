import pandas as pd
#from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import FAISS
from langchain_core.output_parsers import StrOutputParser # LLM으로 부터 나온 응답을 단순 문자열로 변환
from langchain_core.runnables import RunnablePassthrough # 내가 실제로 한 질문 (Rag X)
from langchain_core.prompts import PromptTemplate # 프롬프트(LLM에게 특정 역할 부여)
#from langchain_openai import ChatOpenAI, OpenAIEmbeddings # 언어모델, 임베딩을 OPEN AI 사 이용
from langchain_google_genai import ChatGoogleGenerativeAI, GoogleGenerativeAIEmbeddings # 언어모델, 임베딩을 Google 사 이용
from langchain.docstore.document import Document # 문서형태로 저장하기 위함
from dotenv import load_dotenv # 환경변수 로드(.env)
import os # 환경변수 접근
import asyncio
import logging 
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

# 환경 변수 로드
load_dotenv() # ()안에는 경로를 작성(현재 루트 디렉토리라서 경로는 생략)
openai_api_key = os.getenv("OPENAI_API_KEY")
google_api_key = os.getenv("GOOGLE_API_KEY")

app = FastAPI()

# 로깅 설정
logging.basicConfig(level=logging.INFO) # 로깅 최소 레벨을 INFO로 설정(INFO 레벨 이상의 메시지만 출력)
logger = logging.getLogger(__name__) # __name__에서 자동으로 어떤 파일에서 로그가 발생했는지 파악해줌

# 단계 1: CSV 파일 읽기 및 데이터 병합
csv_files = ["data/test01.csv"] # 파일이 여러 개인 경우 "," 후 작성
dfs = [pd.read_csv(file) for file in csv_files] # 파일이 여러 개인 경우 이들을 읽고 DataFrame 형식으로 리스트 컴프리헨션(리스트에 저장)
combined_df = pd.concat(dfs, ignore_index=True) # 여러 개의 DataFrame을 하나의 DataFrame으로 결합, 인덱스 새로 할당

# 데이터 조합 및 정제
# combined_df["ingredients"] = combined_df["CKG_MTRL_CN"].str.replace(r"\[재료\]\s*", "", regex=True) 필요없는 부분 제거
combined_df["content"] = ( # 각각의 컬럼들을 합쳐 content라는 컬럼으로 생성
    combined_df["CKG_NM"] + " | " + 
    combined_df["CKG_MTH_ACTO_NM"] + " | " +
    combined_df["CKG_STA_ACTO_NM"] + " | " +
    combined_df["CKG_MTRL_CN"]
)

# LangChain의 Document 객체로 변환
# 검색이나 질문-답변에 쓰임
docs = [
    Document(
        page_content=row["content"], # 문서의 본문
        metadata={ # 문서에 대한 추가 정보
            "source": f"file_{i}", # 문서의 출처(몇 번째 행)
            "요리": row["CKG_NM"],
            "카테고리": row["CKG_STA_ACTO_NM"],
            "방법": row["CKG_MTH_ACTO_NM"],
            "재료": row["CKG_MTRL_CN"]
        }
    )
    for i, row in combined_df.iterrows() # 각 행을 반복
]

# 단계 2: 문서 분할 생략 (이미 요리 단위로 분리)
split_documents = docs

# 단계 3: 임베딩(Embedding) 생성
embeddings = GoogleGenerativeAIEmbeddings(model="models/embedding-001", google_api_key=google_api_key)

# 단계 4: DB 생성(Create DB) 및 저장 (FAISS 사용)
# 분할된 문서와, 임베딩 모델을 통한 DB 생성
vectorstore = FAISS.from_documents(documents=split_documents, embedding=embeddings)

# 단계 5: 검색기(Retriever) 생성, 유사도 기반 데이터 5개
retriever = vectorstore.as_retriever(search_type="similarity", search_kwargs={"k": 5})

# 단계 6: 프롬프트 생성(Create Prompt)
# """: 멀티 라인 문자열, 여러 줄에 걸쳐 문자열 작성 가능
prompt = PromptTemplate.from_template(
    """You are an assistant for question-answering tasks. 
Use the following pieces of retrieved context to answer the question. 
If you don't know the answer, just say that you don't know. 
Answer in Korean.

#Context: 
{context}

#Question:
{question}

#Answer:"""
)

# 단계 7: 언어모델(LLM) 생성
llm = ChatGoogleGenerativeAI(model="gemini-1.5-flash", google_api_key=google_api_key, temperature=0)

# 단계 8: 체인(Chain) 생성
chain = (
    {"context": retriever, "question": RunnablePassthrough()}
    | prompt
    | llm
    | StrOutputParser()
)

# AiRequest DTO 정의
class AiRequest(BaseModel):
    cookingMethod: str  # 요리 방법
    cookingCategory: str  # 요리 카테고리
    ingredients: str  # 재료

@app.post("/api/question")
async def ai_service(request: AiRequest):
    # FastAPI에서 받은 AiRequest를 처리
    question = f"요리 방법 '{request.cookingMethod}'을(를) 사용하고, 요리 카테고리 '{request.cookingCategory}'에 맞는 재료 '{request.ingredients}'로 만들 수 있는 요리를 3개만 추천해주세요, 추가로 그 요리를 만들기 위해 필요한 재료를 모두 적어주세요"

    # LangChain 실행
    try:
        response_content = await asyncio.wait_for(chain.ainvoke(question), timeout=30.0)  # 앞서 생성한 chain 실행, 30초 이내 반환(장애전파 방지)
        return response_content  # 문자열로만 반환
    except asyncio.TimeoutError:
        logger.error(f"LLM chain execution timed out after 30 seconds for question: {question}")
        raise HTTPException(status_code=504, detail="요리 추천 서비스 응답이 지연되고 있습니다. 잠시 후 다시 시도해 주세요.")
    except Exception as e:
        logger.error(f"Error occurred while processing the request: {e}")
        raise HTTPException(status_code=500, detail="요리 추천 서비스에 일시적인 오류가 발생했습니다. 나중에 다시 이용해 주세요.")