import os
import setting
from GenerateSummary import *
from GeneratePpt import *
from ImageCaptioning.image_captioning import *

from fastapi import FastAPI, UploadFile, Form
import uvicorn

app = FastAPI() # FastAPI 인스턴스 생성
GPT_KEY = setting.GPT_KEY
EXTRACT_DIR = setting.EXTRACT_DIR
IMAGE_DIR = setting.IMAGE_DIR
RESULT_DIR = setting.RESULT_DIR

# /
@app.get("/")
async def root():
    return {"message": "Hello World"}

# 요약문 생성
# 파일 이름을 받는다.
@app.post("/summary")
async def summary(filename: str = Form(...)):
    # 요약을 생성할 텍스트 파일
    text_filename = filename
    text_filepath = EXTRACT_DIR + filename
    print("텍스트 파일 경로: " + text_filepath)
    print("텍스트 파일 이름: " + text_filename)

    # 파일 열어서 확인
    f = open(text_filepath, 'r', encoding='UTF-8')
    line = f.readline()
    print(line)
    f.close()

    # 만든 함수 동작

    # 결과물을 디렉터리에 저장

    # 요약 파일 이름
    summary_filename = text_filename.replace("_extract.txt", "_summary.txt")
    print("요약 파일 이름: " + summary_filename)

    return {"summary": summary_filename}

# PPT 생성
# 파일 이름을 받는다.
@app.post("/ppt")
async def summary(filename: str = Form(...)):
    # PPT 생성에 사용할 요약문 파일
    text_filename = filename
    text_filepath = EXTRACT_DIR + filename
    print("텍스트 파일 경로: " + text_filepath)
    print("텍스트 파일 이름: " + text_filename)

    # 파일 열어서 확인
    f = open(text_filepath, 'r', encoding='UTF-8')
    line = f.readline()
    print(line)
    f.close()

    # 만든 함수 동작

    # 결과물을 디렉터리에 저장

    # PPT 파일 이름
    ppt_filename = text_filename.replace("_extract.txt", "_summary.txt")
    print("요약 파일 이름: " + ppt_filename)

    return {"ppt": ppt_filename}

# 이미지 캡셔닝
# UploadFile이 File보다 이미지 처리에 적합
@app.post("/caption")
async def caption(file: UploadFile):
    UPLOAD_DIR = "./"

    # 서버에 파일 저장
    contents = await file.read()
    filename = file.filename
    with open(os.path.join(UPLOAD_DIR, filename), "wb") as fp:
        fp.write(contents)
    print("파일 저장: " + filename)

    # 이미지 캡셔닝
    print("이미지 캡셔닝 시작")
    image_path = UPLOAD_DIR + filename
    caption = generate(image_path)
    print("이미지 캡셔닝 종료")

    # 이미지 삭제
    os.remove(filename)
    print("파일 삭제")

    return {"caption": caption}