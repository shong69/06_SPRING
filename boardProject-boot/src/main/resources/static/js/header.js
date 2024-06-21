//공공데이터 
//async, awaiy : 비동기 처리 패턴
//비동기를 마치 동기처럼(실행순서를 지켜서) 사용하는 방법
// async : 비동기가 수행되는 함수 정의 부분 앞에 붙여서 사용하는 키워드(비동기 요청을 수행할것이라 선언하는 것임)
// awit : promise를 리텅하는 비동기 요청 앞에 붙여 사용하는 키워드(응답이 올 때까지 기다리겠다는 의미)





//비동기 요청 1번째 함수
//서비스키 config.properties에서 얻어오기

async function getServiceKey() {
    try{

        //패치 요청 보내기
        const response = await fetch("/getServiceKey");
        return response.text();

    }catch(err){
        console.log("getServicekey의 에러 : " + err);
    }
}

//오늘 날짜를 YYYYMMDD 형식으로 리턴하는 함수
function getCurrentDate(){
    const today = new Date();
    const year = today.getFullYear();
    const month = ('0'+(today.getMonth()+1)).slice(-2); 
    //빈칸 포함 4글자가 나오기 때문에 뒤의 숫자 2자리만 추출
    const day =('0'+today.getDate()).slice(-2);

    return `${year}${month}${day}`;
}



//공공데이터 날씨 api 정보를 얻어올 함수
async function fetchData() {
    //날짜 받아와서 '발표일자'와 연결하기
    const currentDate = getCurrentDate(); //20240525
    const serviceKey = await getServiceKey();

    console.log("serviceKey : ", serviceKey);
    //요청주소 값 가져오기
    const url = 'http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst';

    //URLSearchParams : URL의 쿼리 문자열을 쉽게 다룰 수 있게 해주는 내장 객체
    //단, 사용시 decode 서비스키를 이용해야함 -> URLSearchParams가 데이터를 인코딩하기 때문에
    const queryParams = new URLSearchParams({
        serviceKey : serviceKey,
        pageNo : 1,
        numOfRows : 10,
        dataType : 'JSON',
        base_date : currentDate,
        base_time : '0500',
        nx: 60,
        ny: 127
    }); 

    console.log(`${url}?${queryParams}`);

    //fetch 요청

    //비동기 요청2 번째
    try{
        const response = await fetch(`${url}?${queryParams}`) 
        const result = await response.json();
        
        //reduce 함수 : 10개의 데이터에 data란 이름으로 접근하여 acc라는 빈공간에 그 값들을 넣는다
        const obj = result.response.body.items.item.reduce((acc, data) => {
            acc[data.category] = data.fcstValue;
            return acc;
        }, {});
   
        // console.log(obj);
        const sky = {
            "1" : "맑음",
            "3" : "구름많음",
            "4" : "흐림"
        }
        // 화면에 뿌리기..
        const weatherInfo = document.querySelector(".weather-info");
       
        // i 요소 생성 (아이콘)
        const icon = document.createElement('i');
       
        // span 요소 생성 (날씨 정보)
        const span = document.createElement('span');
      
        // 첫 번째 p 요소 생성 (기온 정보)
        const p1 = document.createElement('p');
        // 두 번째 p 요소 생성 (비 올 확률 정보)
        const p2 = document.createElement('p');
        // 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        // 강수 형태에 따라 icon 지정
        if( obj.PTY == 0 ) { // 강수 없음
            switch(obj.SKY) { // SKY 상태에 따라 아이콘 지정
                case "1" : 
                    icon.className = 'fa-solid fa-sun'; break;
   
                case "3" :
                    icon.className = 'fa-solid fa-cloud'; break;
               
                case "4" :
                    icon.className = 'fa-solid fa-cloud-sun'; break;
   
            }
        } else if(obj.PTY == 3) { // 눈 올 때
            icon.className = 'fa-solid fa-snowflake';
        } else { // 그외 비올때
            icon.className = 'fa-solid fa-cloud-rain';
        }
        // 하늘 정보
        span.innerText = sky[obj.SKY];
        // 기온
        p1.innerText = `기온 : ${obj.TMP}℃`;
       
        // 강수확률
        p2.innerText = `강수 확률 : ${obj.POP}%`;
        // div에 자식 요소들 추가
        weatherInfo.appendChild(icon);
        weatherInfo.appendChild(span);
        weatherInfo.appendChild(p1);
        weatherInfo.appendChild(p2);



    }catch(err){
        console.log(err);
    }
}

fetchData();