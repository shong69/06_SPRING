
//아이디 저장 시 아이디 값 항상 떠 있도록

// 쿠키에서 key가 일치하는 value를 얻어오는 함수 만들기
//쿠키에 저장된 값을 꺼내오기 ("K=V", "K=V",...로 나열된 형태)
const getCookie = (key)=>{


    //document.cookie = "test"+"="+"유저일"; -> 쿠키 직접 지정해보기
    const cookies = document.cookie; //브라우저에 저장된 쿠키들 가져오기(직접 세팅한 쿠키만 넘어온다)
    console.log(cookies); //saveId=user01@kh.or.kr; test=유저일

    //cookies 문자열을 배열 형태로 변환하기
    //배열.map(함수) : 배열의 각 요소를 이용해서 작성한 함수를 수행 후 결과값으로 새로운 배열을 만들어서 반환해줌
    const cookieList = cookies.split("; ") //[K=V,K=V,..] 형태로 나눠짐
                        .map(el => el.split("=")); //[["K","V"],["K","V"],...]
    
    //console.log(cookieList);

    //배열 -> 객체로 변환(다루기 쉽도록)
    const obj = {}; //빈 객체 선언

    for(let i = 0; i<cookieList.length;i++){
        const k = cookieList[i][0]; //key값
        const v = cookieList[i][1]; //value값
        obj[k]=v; //객체에 추가하기
    } //{saveId: 'user01@kh.or.kr', test: '유저일'}

    return obj[key]; //함수 getCookie는 매개변수 key에 해당하는 value를 obj에서 반환해준다
}
//console.log(getCookie("NoId")); 없는 id 검색 시 undefined 나옴

const loginEmail = document.querySelector("#loginForm input[name='memberEmail']") 
//id가 loginForm인 엘리먼트의 자식 중 input태그 중에서 name속성이 memberEmail인 애

//로그인 안 된 상태인 경우 (null값에서 바로 수행 시 js에서 에러난다)
if(loginEmail != null){ //로그인 창의 이메일 입력 부분이 화면에 있을 때

    //쿠키 중 key 값이 "saveId"인 요소의 value 얻어오기
    const saveId = getCookie("saveId"); //undefined 또는 email이 넘어옴

    //saveId 값이 있을 경우
    if(saveId != undefined){
        loginEmail.value = saveId; //쿠키에서 얻어온 값을 input에 value로 세팅하기

        //아이디 저장 체크박스에 체크해두기
        document.querySelector("input[name='saveId']").checked=true;

    }
}


//id,pw 미작성 시 로그인 로직이 아예 돌지 않도록 하기
const loginForm = document.querySelector("#loginForm");
const loginPw = document.querySelector("#loginForm input[name='memberPw']");

//#loginForm이 화면에 존재할 때 (==로그인 상태가 아닐 때)
if(loginForm!= null){

    //제출 이벤트 발생 시 
    loginForm.addEventListener("submit", e=>{

        //이메일 미작성
        if(loginEmail.value.trim().length === 0){
            alert("이메일을 작성해주세요.");
            e.preventDefault(); //이벤트(제출) 막기
            
            loginEmail.focus(); //초점 이동
            return;
        } 
        //비밀번호 미작성
        if(loginPw.value.trim().length === 0){
            alert("비밀번호를 작성해주세요.");
            e.preventDefault(); //이벤트(제출) 막기
            loginPw.focus(); //초점 이동
            return;
        } 
    });
}


