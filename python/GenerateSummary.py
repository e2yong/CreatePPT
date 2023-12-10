import openai
from openai import OpenAI

#요약문을 토대로 대본을 만드는 함수
#프롬프트의 내용에서 추가적으로 수정할 부분은 계속 있을듯함
#시간경우에는 넣어 놓긴 했는데 추후에 빠질수도 있음
def Generate_Script(summary_data, situation, minute, key):
    openai.api_key = key
    message = f"""주어진 {summary_data}를 토대로 대본을 만들어줘
                {situation}을 참조하여 대본을 작성하며, {minute}분 동안 발표를 진행해야 하기 때문에 시간에 맞는 길이를 조절해줘
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
    client = OpenAI(api_key=key)
    response = client.chat.completions.create(
        model="gpt-3.5-turbo-1106",
        messages=[
        {"role": "user", "content": message}
            ]
    )
    return response.choices[0].message.content

#입력받은 기본적인 정보를 토대로 요약문을 생성하는 함수
#ppt와 대본을 만드는 함수와 호환하여 작동될 예정이다
def Generate_Summary(input_data,input_situation,key):
    #word파일의 원문 텍스트들과 상황을 입력으로 받음
    #key값도 일단 입력받는 형태
    openai.api_key = key
    message = f"""주어진 {input_data}를 바탕으로 너가 {input_situation}에 해당한다고 생각했을 때 발표자료를 만들기 위해 필요한
                1번째로 목차와 제목을 생성하며, 목차는 20개 이내의 토큰으로 만들어야한다
                2번째는 목차에 해당하는 내용들 중 제한적인 시간내에서 표현해야할 내용들로 목차의 내용을 채워넣어야 한다.
                단, 주어진 정보의 문장을 그대로 인용하지는 말고 간결하고 명확한 표현으로 수정해서 채워야 한다.목차의 내용은 최소 3문장 이상으로 이루어진 1개의 문단으로 제공해야 하며
                2개 이상의 문단을 제공하는 것도 문제는 없다
                3번째는 앞서 진행한 과정중에서 가장 주제와 밀접하고 중요도가 높은 목차에는 [이미지]라는 내용을 추가해 중요도를 표현해야 한다.
                4번째로는 너가 만들어낸 문단과 문장들을 발표상황에 있다고 생각했을 때 수정할 내용이 있다면 수정을 진행하며,'?'와 '!' 같은 특수기호가 있다면 삭제를 진행해야한다
                5번째로는 목차안에 있는 소제목들 중에서 가장 중요도 수치가 높은 소제목에 내용에 대해 표현할 이미지에 대한 설명을 [IMG]태그 안에 제공해야하며
                각 목차별 중요도를 0~100까지의 수치로 표현해야한다.

                너는 주어진 형식에 맞는 대답을 제공해야해:
                제목 - (제목, 소제목,목차)
                소제목 - (순번,요약내용,중요도)
                요약내용 - (주어진 데이터를 요약한 정보,이미지)

                제목에는 [제목]태그를 앞에 붙여줘
                제목에는 [/제목]태그를 뒤에 붙어줘
                목차에는 [목차]태그를 앞에 붙여줘
                목차에는 [/목차]태그를 뒤에 붙어줘
                소제목에는 [소제목]태그를 앞에 붙여줘
                소제목 내용 뒤에 [순번] 태그와 순번 정보를 붙어줘
                순번 뒤에는 [중요도]태그와 중요도 수치를 봍여줘
                소제목에는 [/소제목]태그를 뒤에 붙어줘
                요약내용에는 [요약내용]태그를 앞에 붙여줘
                요약내용에는 [/요약내용]태그를 뒤에 붙어줘
                이미지에는 [IMG]태그를 앞에 붙여줘
                이미지에는 [/IMG]태그를 뒤에 붙여줘
                소제목이 끝날때 마다 [소제목 단락 종료]라는 태그를 뒤에 붙여줘

                예시 자료 :
                [제목] 달 탐사 2단계 사업 예비타당성 조사 통과 [/제목]
                [목차] 1. 2032년에 독자 탐사선 착륙 예정
                       2. 달 착륙선 개발에 10년간 5303억4000만원 투입
                       3. 우주탐사 로드맵 확정 및 탑재체 개발 추진 예정
                       [/목차]
                [소제목] 1. 2032년에 독자 탐사선 착륙 예정[/소제목]
                [순번] 1번
                [중요도] 55
                [요약내용]
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
                [IMG] 달 착륙선 그림 [/IMG]
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


                """

    client = OpenAI(api_key=key)
    response = client.chat.completions.create(
        model="gpt-3.5-turbo-1106",
        messages=[
        {"role": "user", "content": message}
            ]
    )
    
    return response.choices[0].message.content


def Summary_example():
    #일단 지금은 입력받은 정보로 돌리는 거보다는 임시로 돌려보는게 편할 거 같아서 만든 함수
    #이부분 호출해서 실험해보는게 제일 간단함
    #Gpt이용하는 과정 3가지 임시로 실행하는 예제  코드인데 단순 호출로 사용해도 문제 없음
    key = "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"
    text_data_1 = "디지털데일리 이안나 기자] SK스퀘어가 싱가포르 이커머스 업체 큐텐과의 11번가에 대한 매각 협상을 중단했다.18일 업계에 따르면 11번가 최대 주주인 SK스퀘어는 최근 큐텐에 협상 중단을 통보했다. SK스퀘어는 지난 9월부터 재무적 투자자(FI) 나일홀딩스 컨소시엄이 보유한 지분 18.18%를 두고 지분 교환 방식으로 협상을 진행해왔다.큐텐은 11번가 실사를 마치기도 했지만, 양사가 파악한 11번가 기업가치에 차이가 있던 것으로 파악된다. SK스퀘어와 큐텐은 협상 과정에서 지분 교환 비율을 두고 이견을 좁히지 못한 것으로 알려졌다. 그간 큐텐은 현금이 아닌 양사 지분을 교환하는 ‘주식 스왑’ 방식을 고수해왔다. 현재로선 양측 간 협상이 재개될 가능성은 희박한 것으로 보인다.국민연금, 새마을금고, 사모펀드 운용사인 나일홀딩스는 지난 2018년 5000억원을 투자하며 11번가 지분 18.18%를 취득했다. 당시 조건은 11번가가 5년 내 기업공개(IPO)를 한다는 내용이 담겼다.다만 올해 IPO 시장 침체로 재무적 투자자(FI)와 약속했던 시한까지 IPO를 완료하는 게 사실상 불가능해지면서 SK스퀘어는 11번가 투자유치 방안으로 매각을 고려하게 됐다.이번 큐텐과의 협상이 사실상 결렬되면서 SK스퀘어는 새로운 투자자 또는 지분 인수 희망자를 찾아야 하는 상황이다.다시 물망에 오른 곳은 11번가와 함께 미국 아마존과 중국 알리바바다. 알리바바와 아마존은 SK스퀘어가 지분 인수 희망자를 찾을 때 큐텐과 함께 언급되던 곳들이기도 하다. 아마존은 11번가와 ‘아마존 글로벌 스토어’를 운영하며 전략적 협업 관계를 갖고 있고, 알리바바의 알리익스프레스는 최근 국내 시장에서 사업을 빠르게 확장 중이다.안정은 11번가 대표 [ⓒ 11번가]11번가 인수에 관심을 보이는 곳이 해외기업 중심인 이유는 국내에선 11번가 인수 여력이 있는 대형 회사를 찾기 힘든 탓으로 보인다. 가령 네이버는 올해 수익성 개선 전략으로 돌아섰고 신세계그룹과 롯데는 온라인보다 오프라인 역량 강화에 초점을 맞추는 분위기다.SK스퀘어는 11번가 기업가치를 제대로 평가해 줄 최적의 파트너를 찾기 위해 고심하는 분위기다. 먼저는 11번가에 5000억원을 투자한 사모펀드사들이 투자금 회수를 할 수 있도록 만드는 게 우선이기 때문이다. 새로운 지분 인수 희망자가 결정되지 못할 경우 SK스퀘어는 사모펀드사들과 맺은 조건에 따른 절차를 밟게 된다.계약엔 콜옵션 조항에 따라 SK스퀘어가 사모펀드 지분을 사들이거나, 사모펀드가 SK스퀘어 11번가 지분까지 모두 매각하는 ‘동반매도요구권(Drag-along)’ 조건 등이 포함된 것으로 보인다. 다만 SK스퀘어에 남은 시간은 많지 않아, 연내엔 어떤 결론이든 나올 것이라는 게 업계 관측이다.한편 11번가는 실적 개선을 통해 기업가치 올리기에 주력하고 있다. 금융감독원 전자공시시스템에 따르면 11번가 올해 3분기까지 누적 매출액은 6019억원으로 전년동기대비 27.6% 증가했다. 3분기 누적 영업손실은 910억원으로 전년대기 150억원(14.1%) 줄었다. 당기순손실은 852억원으로 전년 756억원보다 늘었으나, 11번가 측은 “지난해 3분기 반영된 일시적 장부평가액 변동에 따른 것”이라고 설명했다.업계 관계자는 “이커머스 시장은 성장세가 정체되고, 대다수 기업들이 여전히 적자상태”라며 “시장 전반에 자금줄이 막혀있어 11번가를 인수할 만큼 현금 여력을 가진 기업들이 많지 않은 것 같다”고 말했다.이안나 기자(anna@ddaily.co.kr)"
    situation_data = "뉴스 기사에 대한 고등학생의 발표 상황"
    test_minute = 10
    summary_data = Generate_Summary(text_data_1, situation_data, key)
    script_data = Generate_Script(summary_data,situation_data,test_minute,key)
    ppt_data = Generate_PPT(summary_data,test_minute,situation_data,key)
    return summary_data,script_data,ppt_data


#PPT를 만들기위해 pptx의 양식에 맞는 내용으로 출력을 조절하는 함수
#영어로 작성된 함수이기 때문에 한글로 재수정해야함
#추가적으로 이미지와 같은 부분들에 대한 고려도 필요함
def Generate_PPT(summary_data,slide_length,situation,key):
    openai.api_key = key
    message = f"""Create an outline for a slideshow presentation on the topic of {summary_data} which is {slide_length}
        slides long. Make sure it is {slide_length} long.

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

        [L_CS]
        [TITLE]What Is Among Us?[/TITLE]
        [CONTENT]
        1. Among Us is a popular online multiplayer game developed and published by InnerSloth.
        2. The game is set in a space-themed setting where players take on the roles of Crewmates and Impostors.
        3. The objective of Crewmates is to complete tasks and identify the Impostors among them, while the Impostors' goal is to sabotage the spaceship and eliminate the Crewmates without being caught.
        [/CONTENT]

        [SLIDEBREAK]

        [L_IS]
        [TITLE]Features of Among us[/TITLE]
        [CONTENT]
        [IMG] Among us logo [/IMG]
        1. The game is very popular in student.
        2. The game ranking's in steam is maintained for a long time.
        3. Many streamers play Among us a lot.
        [/CONTENT]

        [SLIDEBREAK]

        Elaborate on the Content, provide as much information as possible.
        REMEMBER TO PLACE a [/CONTENT] at the end of the Content.
        Make [TITLE] simply.
        {situation}을 고려해서 내용을 제공해야 하며
        제공한 내용은 한글을 통해 작성해줘
        제공한 소제목의 요약내용안에 [IMG]태그 있다면 [L_CS]태그를 [L_IS] 태그로 수정하고, [IMG] 태크를 [CONTENT]
        태그 안에 추가해줘
        Do not include any special characters (?, !, ., :, ) in the Title.
        Do not include any additional information in your response and stick to the format."""

    client = OpenAI(api_key=key)
    response = client.chat.completions.create(
        model="gpt-3.5-turbo-1106",
        messages=[
        {"role": "user", "content": message}
            ]
    )
    return response.choices[0].message.content