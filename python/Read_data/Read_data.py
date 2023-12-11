import re

class Imagecaptioning_data:
    # word 파일에서 이미지 캡셔닝을 진행 한 부분에 대해 처리하기 위해 사용하는 클래스
    # 아직 해당 클래스를 어떤 식으로 동작에 활용할지가 가장 고민이 되는 부분임
    class_variable = "이미지 캡셔닝을 한 데이터를 정리하는 클래스입니다"

    # 초기화 메서드 (생성자)
    def __init__(self, arg1, arg2, arg3):
        self.instance_name = arg1
        self.instance_caption = arg2
        self.instance_path = arg3

    # 클래스 메서드(class method)
    @classmethod
    def class_method(cls):
        return cls.class_variable

    # 인스턴스 메서드(instance method)
    def instance_method(self):
        return f"Instance variables: {self.instance_name}, {self.instance_caption}, {self.instance_path}"

    def get_name(self):
        # get,set 함수 구현 #
        name = self.instance_name
        return name

    def set_name(self,name):
        self.instance_name = name

    def get_caption(self):
        caption = self.instance_caption
        return caption

    def set_caption(self,caption):
        self.instance_caption = caption

    def get_path(self):
        path = self.instance_path
        return path

    def set_path(self, path):
        self.instance_path = path

def read_txt(file_path):
    #파일 경로를 받아서 txt파일의 내용을 str클래스의 변수로 리턴해주는 함수
    file_contents = ""
    try:
        with open(file_path,'r',encoding='UTF8') as file:
            file_contents = file.read()

    except FileNotFoundError:
        print("파일을 찾을 수 없습니다")

    return file_contents


def write_txt(file_path,file_content):
    #개행문자를 포함해서 txt파일을 작성하는 함수
    try:
        with open(file_path,'w',encoding='UTF8') as file:
            for line in file_content:
                file.write(line+'\n')
    except Exception as e:
        print("파일을 읽을 수 없습니다")

    return file_path

def write_txt_no_enter(file_path,file_content):
    #개행문자를 포함하지 않고 txt파일을 작성하는 함수
    try:
        with open(file_path,'w',encoding='UTF8') as file:
            for line in file_content:
                file.write(line)
    except Exception as e:
        print("파일을 읽을 수 없습니다")

    return file_path
#데이터 처리를 위해 사용하는 함수
#1개로 합치는 게 더 편할듯 하다
def find_txt_caption(txt_data):
    #캡션에 해당하는 부분을 빼고 str변수에 저장하는 함수
    split_data=txt_data.split('\n')
    findstring = "<image"
    #캡션의 시작 부분에 해당하는 부분을 선언
    first_data = []
    second_data = []
    txt_data_2 =[]

    for line in split_data:
        matches = re.findall(findstring,line)
        #캡션의 시작과 같은지 판별한뒤
        if matches:
            #같다면 캡션 부분에 저장하고
            first_data.append(line)
        else :
            #같지 않다면 캡션이 아닌 텍스트 부분에 저장한다
            txt_data_2.append(line)

    for line in first_data:
        first_data_split = line.split(',')
        #캡션 부분에서 현재는 ,를 기준으로 분류를 했는데 캡션에 ,이 포함되면 수정을 진행해야함
        second_data.append(first_data_split)

    return second_data,txt_data_2
    #캡션과 캡션이 아닌 부분을 2개로 나누어서 저장


def save_caption(second_data):
    #캡션의 정보를 Imagecaptioning_data클래스에 저장하는 함수
    caption_list = []
    for line in second_data:
        name = line[0].replace("<image: ","")
        caption = line[1].replace("caption: ","")
        path = line[2].replace("path: ","")
        obj = Imagecaptioning_data(name,caption,path)
        caption_list.append(obj)

    return caption_list
