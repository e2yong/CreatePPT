import setting
import re

# 원본 텍스트 불러오기
def get_original_text(extract_txt):
    with open(f"{extract_txt}", 'r', encoding='UTF8') as file:
        lines = file.readlines()
        return lines

# 텍스트 파일 내 태그 제거 및 분류
def find_text_in_between_tags(text, start_tag, end_tag):
    start_pos = text.find(start_tag)
    end_pos = text.find(end_tag)
    result = []

    while start_pos > -1 and end_pos > -1:
        text_between_tags = text[start_pos + len(start_tag):end_pos]
        result.append(text_between_tags)
        start_pos = text.find(start_tag, end_pos + len(end_tag))
        end_pos = text.find(end_tag, start_pos)

    res1 = "".join(result).replace('- ', '')
    res2 = re.sub(r"\[IMAGE\].*?\[/IMAGE\]", '', res1)

    while res2[0] == '\n':
        res2 = re.sub(r"\n", '', res2, count=1)

    if len(result) > 0:
        return res2
    else:
        return ""