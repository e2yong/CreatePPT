import os

from GenerateSummary.Generate_text import *
from GenerateSummary.word import *
from Read_data.Read_data import *

from PPT_Generator_fix import *

from ImageCaptioning.image_captioning import *

from fastapi import FastAPI, UploadFile, Form
import uvicorn

import setting
GPT_KEY = setting.GPT_KEY
EXTRACT_DIR = setting.EXTRACT_DIR
IMAGE_DIR = setting.IMAGE_DIR
RESULT_DIR = setting.RESULT_DIR

app = FastAPI() # FastAPI 인스턴스 생성

# /
@app.get("/")
async def root():
    return {"message": "Hello World"}

# 요약문 생성
# 파일 이름을 받는다.
@app.post("/summary")
async def summary(filename: str = Form(...)):
    client = OpenAI(api_key=GPT_KEY)

    # 요약을 생성할 텍스트 파일
    text_filename = filename
    text_filepath = EXTRACT_DIR + "/"+ filename
    print("텍스트 파일 경로: " + text_filepath)
    print("텍스트 파일 이름: " + text_filename)

    # 파일 열어서 확인
    f = open(text_filepath, 'r', encoding='UTF-8')
    line = f.readline()
    print(line)
    f.close()

    # 파일 안에 텍스트 추출
    raw_txt = read_txt(text_filepath)
    # 텍스트에서 캡션과 나머지 텍스트를 분류
    caption, extract_txt = find_txt_caption(raw_txt)
    caption_list = save_caption(caption)
    # 캡션을 제외한 텍스트 저장 위치 및 이름 지정
    extract_caption_filename = text_filename.replace("_extract.txt", "_caption_extract.txt")
    extract_caption_filepath = EXTRACT_DIR + "/" + extract_caption_filename
    # 경로에 txt파일 저장
    write_txt(extract_caption_filepath, extract_txt)
    
    # 요약문 생성
    print("요약문 생성")
    summary_txt = Generate_Summary(client,extract_txt)
    # 요약문 저장 위치 및 이름 지정
    summary_filename = text_filename.replace("_extract.txt", "_summary.txt")
    summary_filepath = EXTRACT_DIR + "/" + summary_filename
    #요약문 내용 확인용 출력
    print(summary_txt)
    #요약문 내용 txt로 저장
    write_txt_no_enter(summary_filepath,summary_txt)

    #대본 생성 및 docx 파일로 저장
    print("대본 생성 및 저장")
    script_txt = Generate_Script(client,summary_txt,10)
    word_filepath = Write_word(script_txt,text_filename)

    #PPTX를 위한 텍스트 파일 생성
    print("PPT 생성을 위한 텍스트 파일 생성")
    ppt_txt = Generate_PPT(client,summary_txt,10)
    ppttxt_filename = text_filename.replace("_extract.txt", "_pptx.txt")
    ppttxt_filepath = EXTRACT_DIR + "/" + ppttxt_filename
    write_txt_no_enter(ppttxt_filepath,ppt_txt)

    # 만들어진 파일들의 절대경로를 리턴값으로 제공한다
    return {"extractFilePath": extract_caption_filepath,"summaryFilePath" : summary_filepath, "scriptFilePath" : word_filepath,"pptxTextFilePath" : ppttxt_filepath}

# PPT 생성
# 파일 이름을 받는다.
@app.post("/ppt")
async def ppt(filename: str = Form(...)):
    print("연결완료")

    # PPT 생성에 사용할 요약문 파일
    text_filename = filename
    text_filepath = EXTRACT_DIR + filename
    print("텍스트 파일 경로: " + text_filepath)
    print("텍스트 파일 이름: " + text_filename)

    # PPT 생성
    print("PPT 생성 중...")
    generator_main(text_filepath)
    # PPT 저장
    ppt_filename = f"{ppt_name}.pptx"

    # PPT 파일 이름
    print("PPT 저장 완료: " + ppt_filename)

    return {"pptFileName": ppt_filename}

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