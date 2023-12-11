import base64
import glob
import os
import random
import re
import string
from urllib.parse import urlparse

from icrawler import ImageDownloader
from icrawler.builtin import GoogleImageCrawler
import setting

IMAGE_DIR = setting.IMAGE_DIR

unique_image_name = None

### 텍스트 및 이미지 관련 ###
# 이미지 인코딩 후 파일명에 들어갈 무작위 문자열 생성
class Base64NameDownloader(ImageDownloader):
    def get_filename(self, task, default_ext):
        url_path = urlparse(task['file_url'])[2]
        if '.' in url_path:
            extension = url_path.split('.')[-1]
            if extension.lower() not in ['jpg', 'jpeg', 'png', 'bmp', 'tiff', 'gif', 'ppm', 'pgm']:
                extension = default_ext
        else:
            extension = default_ext

        filename = base64.b64encode(url_path.encode()).decode()
        return "p_" + unique_image_name + '{}.{}'.format(filename, extension)

# 이미지 파일명이 겹치지 않도록 고유한 문자열 일부 넣기
def refresh_unique_image_name():
    global unique_image_name
    unique_image_name = ''.join(random.choice(string.ascii_uppercase + string.ascii_lowercase + string.digits)
                                for _ in range(16))
    return

# 이미지 크롤링
def image_crawler(keyword):
    refresh_unique_image_name()

    google_crawler = GoogleImageCrawler(downloader_cls=Base64NameDownloader, storage={'root_dir': IMAGE_DIR})
    google_crawler.crawl(keyword=keyword, max_num=5)

    dir_path = os.path.dirname(os.path.realpath(__file__))
    file_name = glob.glob(f"p_{unique_image_name}*")

    img_path = os.path.join(dir_path, file_name[0])
    return img_path