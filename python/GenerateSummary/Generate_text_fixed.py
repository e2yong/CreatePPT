import openai,setting
from openai import OpenAI
GPT_KEY = setting.GPT_KEY
#요약문을 토대로 대본을 만드는 함수
#프롬프트의 내용에서 추가적으로 수정할 부분은 계속 있을듯함
#시간경우에는 넣어 놓긴 했는데 추후에 빠질수도 있음
def Generate_Script(client,summary_data,minute):
    message = f"""주어진 {summary_data}를 토대로 대본을 만들어줘
                {minute}분 동안 발표를 진행해야 하기 때문에 시간에 맞는 길이를 조절해줘
                주어진 정보외에 추가적인 정보는 넣으면 안되며, 주어진 데이터의 형식을 참조하여 작성해줘

                너는 주어진 형식에 맞는 대답을 제공해야해:
                제목 - (제목,소제목,시작표현,마무리 표현)
                소제목 - (내용에 대한 구어체 표현)

                제목에는 [제목]태그를 앞에 붙여줘.
                제목에는 [/제목]태그를 뒤에 붙여줘.
                시작표현에는 [시작하는말]태그를 앞에 붙여줘
                시작표현에는 [/시작하는말]태그를 뒤에 붙여줘
                마무리 표현에는 [마무리하는 말]태그를 앞에 붙여줘
                마무리 표현에는 [/마무리하는 말]태그를 뒤에 붙여줘
                소제목에는[소제목]태그를 앞에 붙여줘
                소제목에는 [/소제목]태그를 뒤에 붙여줘
                내용에 대한 구어체 표현에는 [대본내용]태그를 앞에 붙여줘
                내용에 대한 구어체 표현에는 [/대본내용]태그를 뒤에 붙여줘

                주어진 데이터를 토대로 제목,소제목은 그대로 가져오며, 대본내용은
                요약내용을 구어체로 바꿔서 표현해줘

                예를 들자면 :
                [제목] 물가의 변화 [/제목]
                [시작하는말] 물가는 경제와 환경, 사회등 다양한 분야에 의해서 변화가 일어나게 됩니다.
                이러한 부분들에 대해서 지금부터 알아보겠습니다[/시작하는말]
                [소제목] 경제의 변화에 따른 물가의 변화
                [대본내용] 사람들의 수입에 따라서 물가는 변화하게 됩니다. 경제적으로 여유가 생기는 경우
                사치품과 같은 필요없는 소비가 증가하게 되며, 사치품의 가격이 증가하게 됩니다.
                하지만 경제적인 여유가 감소하게 되면, 사치품과 같은 불필요한 소비가 감소하게 되며,
                필수적인 소비만을 진행하게 됩니다.[/대본내용]
                
                [마무리하는 말]지금까지 발표를 들어주셔서 감사합니다[/마무리하는 말]

                시작표현,마무리 표현, 내용에 대한 구어체 표현은 무조건 구어체로 표현해야함
                '.',','을 제외한 특수기호는 사용하면 안된다
                내용에 대한 구어체 표현은 총 발표시간이 {minute}분인 것을 고려하여 최대한 시간에 맞게 제공해야 하며
                주어진 데이터에 최대한 많은 내용을 포함해야 한다
                시작표현에서는 대본의 내용들을 간단하게 소개하면서 청자들의 흥미를 이끌만한 표현을 사용해야 하며,
                필수적으로 작성되야 한다
    """
    response = client.chat.completions.create(
        model="gpt-3.5-turbo-1106",
        messages=[
        {"role": "user", "content": message}
            ]
    )
    return response.choices[0].message.content
#입력받은 기본적인 정보를 토대로 요약문을 생성하는 함수
#ppt와 대본을 만드는 함수와 호환하여 작동될 예정이다


def Generate_Summary(client, input_data):
    # 추출한 텍스트와 Gpt 사용을 위한 client를 받음
    message = f"""
                주어진 {input_data}를 바탕으로 너가 발표를 한다고 생각했을 때 발표자료를 만들기 위해 필요한 내용을 넣어야 한며 아래와 같은 과정을 거쳐 진행해야 한다.
                1번째로 목차와 제목을 생성하며, 목차는 20개 이내의 토큰으로 만들어야하며, 목차에 들어가는 내용의 수는 5,6개 중 하나로 정해야 한다.
                2번째는 소제목을 생성해야 하며, 소제목에 해당하는 내용을 채워야한다. 소제목과 목차는 일치해야 한다.
                단, 주어진 정보의 문장을 인용해도 되지만, 간결하고 명확한 표현으로 채워야 하며,소제목의 내용은 최대한 많이 넣야아 한다.
                3번째는 너가 만들어낸 문단과 문장들을 발표상황에 있다고 생각했을 때 수정할 내용이 있다면 수정을 진행하며,'?'와 '!' 같은 특수기호가 있다면 삭제를 진행해야한다
                4번째는 소제목의 요약내용 안에는 [IMG] 태그안에 소제목을 표현할 수 내용을 [IMG]태그 안에 넣어 수정을 진행해야 한다

                너는 주어진 형식에 맞는 대답을 제공해야해:
                제목 - (제목, 소제목,목차)
                소제목 - (요약내용,중요도)
                요약내용 - (IMG,주어진 데이터를 요약한 정보)

                제목에는 [제목]태그를 앞에 붙여줘
                제목에는 [/제목]태그를 뒤에 붙어줘
                목차에는 [목차]태그를 앞에 붙여줘
                목차에는 [/목차]태그를 뒤에 붙어줘
                소제목에는 [소제목]태그를 앞에 붙여줘
                순번 뒤에는 [중요도]태그와 중요도 수치를 봍여줘
                소제목에는 [/소제목]태그를 뒤에 붙어줘
                요약내용에는 [요약내용]태그를 앞에 붙여줘
                요약내용에는 [/요약내용]태그를 뒤에 붙어줘
                IMG에는 [IMG]태그를 앞에 붙여줘
                IMG에는 [/IMG]태그를 뒤에 붙여줘
                소제목이 끝날때 마다 [소제목 단락 종료]라는 태그를 뒤에 붙여줘    
                예시 자료의 형식을 무조건 지켜야 한다.
               
                예시 자료 :
                [제목] 달 탐사 2단계 사업 예비타당성 조사 통과 [/제목]
                
                [목차] 1. 2032년에 독자 탐사선 착륙 예정
                       2. 달 착륙선 개발에 10년간 5303억4000만원 투입
                       3. 우주탐사 로드맵 확정 및 탑재체 개발 추진 예정
                       [/목차]
                
                [소제목] 2032년에 독자 탐사선 착륙 예정[/소제목]
                [중요도] 55
                [요약내용]
                [IMG] 다누리 탐사선 이미지 [IMG]
                - 한국의 첫 달 탐사선 '다누리'가 8월 4일에 발사되었다.
                - '달 탐사 2단계 사업'은 2032년에 독자 탐사선을 달에 착륙시키기 위해 예비타당성 조사를 통과했다.
                - 이 사업은 1.8톤급 달 착륙선을 개발하여 정밀한 연착륙을 수행한다.
                - 예상지 주변의 장애물을 탐지하고 회피하는 기능도 탑재할 예정이다.
                [/요약내용]

                [소제목 단락 종료]

                [소제목] 달 착륙선 개발에 10년간 5303억4000만원 투입[/소제목]
                [중요도] 85
                [요약내용]
                [IMG] 달 착륙선 그림[/IMG]
                - 과기정통부는 2024년부터 2033년까지 10년간 5303억4000만원을 달 탐사 2단계 사업에 투자할 예정이다.
                - 예산은 1년 늘어나면서 881억600만원이 줄었다.
                - 달 착륙선 개발을 위해 우주 탐사선 추진시스템과 연착륙을 위한 장애물 탐지 및 회피기술, 항법시스템 등을 국산화할 계획이다.
                [/요약내용]

                [소제목 단락 종료]

                [소제목] 우주탐사 로드맵 확정 및 탑재체 개발 추진 예정[/소제목]
                [요약내용]
                [중요도] 70
                [IMG] 우주 탐사 로드맵 그림[/IMG]
                - 과기정통부는 산학연과 협력하여 달 착륙선의 과학기술 임무를 담은 우주탐사 로드맵을 수립할 예정이다.
                - 로드맵을 기반으로 달 착륙선 탑재체를 추진하며, 내년 초까지 선정돼 개발될 것이다.
                - 달 착륙선의 연착륙 임무는 2031년에 우선 차세대 발사체를 이용해 검증되고, 2032년에는 달 표면 탐사 임무까지 수행한다.
                [/요약내용]

                [소제목 단락 종료]
                [소제목] 우주탐사 로드맵 확정 및 탑재체 개발 추진 예정[/소제목]
                [요약내용]
                [중요도] 70
                [IMG] 우주 탐사 로드맵 그림[/IMG]
                - 과기정통부는 산학연과 협력하여 달 착륙선의 과학기술 임무를 담은 우주탐사 로드맵을 수립할 예정이다.
                - 로드맵을 기반으로 달 착륙선 탑재체를 추진하며, 내년 초까지 선정돼 개발될 것이다.
                - 달 착륙선의 연착륙 임무는 2031년에 우선 차세대 발사체를 이용해 검증되고, 2032년에는 달 표면 탐사 임무까지 수행한다.
                [/요약내용]

                [소제목 단락 종료]
                [소제목] 우주탐사 로드맵 확정 및 탑재체 개발 추진 예정[/소제목]
                [요약내용]
                [중요도] 70
                [IMG] 우주 탐사 로드맵 그림[/IMG]
                - 과기정통부는 산학연과 협력하여 달 착륙선의 과학기술 임무를 담은 우주탐사 로드맵을 수립할 예정이다.
                - 로드맵을 기반으로 달 착륙선 탑재체를 추진하며, 내년 초까지 선정돼 개발될 것이다.
                - 달 착륙선의 연착륙 임무는 2031년에 우선 차세대 발사체를 이용해 검증되고, 2032년에는 달 표면 탐사 임무까지 수행한다.
                [/요약내용]

                [소제목 단락 종료]
                생성한 목차에 맞는 내용은 무조건 들어가야 하며, 내용적으로 어울리지 않는 내용은 추가하면 안된다.

                """

    response = client.chat.completions.create(
        model="gpt-3.5-turbo-1106",
        messages=[
            {"role": "user", "content": message}
        ]
    )
    return response.choices[0].message.content
def Insert_caption(client,summary_data,caption):
    message = f""" 
                {caption}의 caption:에 해당하는 부분이 {summary_data}의 정보들 중 소제목의 요약내용과 연관성이 높은 곳에
                [IMG] 태그를 붙인 뒤에 해당 caption에 해당하는 path를 요약내용 안에 추가한 뒤, [/IMG] 태그를 붙여준다
    
                예를 들자면 :
                [제목] 달 탐사 2단계 사업 예비타당성 조사 통과 [/제목]
                [목차] 1. 2032년에 독자 탐사선 착륙 예정
                       2. 달 착륙선 개발에 10년간 5303억4000만원 투입
                       3. 우주탐사 로드맵 확정 및 탑재체 개발 추진 예정
                       [/목차]
                [소제목] 1. 2032년에 독자 탐사선 착륙 예정[/소제목]
                [순번] 1번
                [중요도] 75
                [요약내용]
                [IMG] C:/Users/dbsxo/IdeaProjects/CreatePPT2/create-ppt/src/main/resources/image/image1.jpeg [IMG]
                - 한국의 첫 달 탐사선 '다누리'가 8월 4일에 발사되었다.
                - '달 탐사 2단계 사업'은 2032년에 독자 탐사선을 달에 착륙시키기 위해 예비타당성 조사를 통과했다.
                - 이 사업은 1.8톤급 달 착륙선을 개발하여 정밀한 연착륙을 수행한다.
                - 예상지 주변의 장애물을 탐지하고 회피하는 기능도 탑재할 예정이다.
                [/요약내용]

                [소제목 단락 종료]

                [소제목] 2. 달 착륙선 개발에 10년간 5303억4000만원 투입[/소제목]
                [순번] 2번
                [중요도] 85
                [요약내용]
                - 과기정통부는 2024년부터 2033년까지 10년간 5303억4000만원을 달 탐사 2단계 사업에 투자할 예정이다.
                - 예산은 1년 늘어나면서 881억600만원이 줄었다.
                - 달 착륙선 개발을 위해 우주 탐사선 추진시스템과 연착륙을 위한 장애물 탐지 및 회피기술, 항법시스템 등을 국산화할 계획이다.
                [/요약내용]

                [소제목 단락 종료]

                [소제목] 3. 우주탐사 로드맵 확정 및 탑재체 개발 추진 예정[/소제목]
                [순번] 3번
                [요약내용]
                [중요도] 60
                - 과기정통부는 산학연과 협력하여 달 착륙선의 과학기술 임무를 담은 우주탐사 로드맵을 수립할 예정이다.
                - 로드맵을 기반으로 달 착륙선 탑재체를 추진하며, 내년 초까지 선정돼 개발될 것이다.
                - 달 착륙선의 연착륙 임무는 2031년에 우선 차세대 발사체를 이용해 검증되고, 2032년에는 달 표면 탐사 임무까지 수행한다.
                [/요약내용]

                [소제목 단락 종료]
    
                
                주어진 데이터에서 추가하는 것을 제외하고는 정보를 수정하면 안된다.
    """

    response = client.chat.completions.create(
        model="gpt-3.5-turbo-1106",
        messages=[
            {"role": "user", "content": message}
        ]
    )
    return response.choices[0].message.content






def Generate_PPT(client, summary_data, slide_length):
    # PPTX를 사용하기 위한 텍스트 형태를 만들어내는 함수
    # 요약 데이터와 슬라이드 길이를 받으며, 10이 default값이다
    message = f"""Create an outline for a slideshow presentation of {summary_data} which is {slide_length}
            slides long. Make sure it is {slide_length} long.
            주어진 정보에서 [IMG]태그가 있는 내용에서는 [TITLE] 위에 무조건[L_IS] 태그를 주어야 한다.
            예를 들면 : 
            [L_IS]
            [TITLE]Features of Among us[/TITLE]
            [CONTENT]
            You are allowed to use the following slide types:
            Title Slide - (Title, Subtitle)
            Content Slide - (Title, Content)
            Image Slide - (Title, Content, Image)
            Thanks Slide - (Title)

            Put this tag before the Title Slide: [L_TS]
            Put this tag before the Content Slide: [L_CS]
            Put this tag before the Image Slide: [L_IS]
            Put this tag before the Thanks Slide: [L_THS]

            Put this tag before the Title: [TITLE]
            Put this tag after the Title: [/TITLE]
            Put this tag before the Subitle: [SUBTITLE]
            Put this tag after the Subtitle: [/SUBTITLE]
            Put this tag before the Content: [CONTENT]
            Put this tag after the Content: [/CONTENT]
            Put this tag before the Image: [IMAGE]
            Put this tag after the Image: [/IMAGE]

            Put "[SLIDEBREAK]" after each slide 

            For example:
            [L_TS]
            [TITLE]Among Us[/TITLE]

            [SLIDEBREAK]
            
            [L_IS]
            [TITLE]Features of Among us[/TITLE]
            [CONTENT]
            [IMAGE] Among us logo [/IMAGE]
            1. The game is very popular in student.
            2. The game ranking's in steam is maintained for a long time.
            3. Many streamers play Among us a lot.
            [/CONTENT]
            
            [SLIDEBREAK]


            Elaborate on the Content, provide as much information as possible.
            REMEMBER TO PLACE a [/CONTENT] at the end of the Content.
            Do not include any special characters (?, !, ., :, ) in the Title.
            Do not include any additional information in your response and stick to the format.
            내용은 한글로 작성해야 한다
            """

    response = client.chat.completions.create(
        model="gpt-3.5-turbo-1106",
        messages=[
            {"role": "user", "content": message}
        ]
    )
    return response.choices[0].message.content