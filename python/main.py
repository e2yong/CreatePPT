import os
from ImageCaptioning.image_captioning import *

from fastapi import FastAPI, UploadFile
import uvicorn

app = FastAPI() # FastAPI 인스턴스 생성

# /
@app.get("/")
async def root():
    return {"message": "Hello World"}

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