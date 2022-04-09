# 서픽 (Search Picture)

### 📃 프로젝트 소개
- Mobilenet을 활용한 이미지 인식 딥러닝을 이용하여 사진 속 장소와 유사한 장소를 추천해 줍니다.
- 사용자 편의성을 위해 지도를 통해 포토스팟들을 한 눈에 볼 수 있고 포토스팟의 공유 및 검색이 가능합니다.

### 🛠 기술 스택
- BackEnd : Java, SpringBoot, Spring Data JPA, QueryDSL, Swagger, JUnit5
- Database: MariaDB
- Infa: AWS EC2, AWS S3, nginx


### 📈 Flow Chart
<img width="400" alt="image" src="https://user-images.githubusercontent.com/63828890/162538178-c845f819-3ffb-40b0-9db7-1d8ed67e50c7.png">


### 🔊 주요 서비스
#### 1. 이미지 분석 탭
   <img height="300" src="https://user-images.githubusercontent.com/63828890/162538534-961b6c34-cab6-4da8-a520-bfc32a0ff7d2.png"/> <br>
  - 학습한 ML 모델과 연결하여 학습을 진행한 40개의 장소 중 이미지와 가장 유사한 장소 Top3 확인할 수 있습니다. 
  - 결과 페이지에서 카페/식당, 명소로 나누어 다시 분석을 시도할 수 있습니다.


#### 2. 탐색 탭
  <img height="300" src="https://user-images.githubusercontent.com/63828890/162539228-cdd5b408-5418-4bf3-a2ea-0e02fbbc44b5.png"/><br/>
  - 등록된 포토스팟 데이터 중 가장 인기 있는 6개의 해시태그를 볼 수 있습니다.
  - 해시태그 검색을 통해 등록된 포토스팟 찾을 수 있습니다.


#### 3. 지도 탭
   <img height="300" src="https://user-images.githubusercontent.com/63828890/162544895-dd1c6548-f899-4cf4-8002-d13d3899dcdf.png"/><br/>
   - 현재 위치 주변의 포토스팟을 지도를 통해 확인할 수 있습니다.
   - 전체/내가 업로드한 장소/내가 좋아요한 장소만 필터링하여 확인 가능하며 포토스팟을 등록하여 공유할 수 있습니다.
   
#### 4. 마이페이지 탭
- 프로필 편집 기능과 유저가 등록한 글을 확인할 수 있습니다.

#### Related Repository 링크
- [React-Native Client](https://github.com/dimage21/SearchPic_front)
- [ML Flask Server](https://github.com/dimage21/SeachPic_ML)
