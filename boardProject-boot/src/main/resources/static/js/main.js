
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


//------------빠른 로그인----------------

const quickLoginBtns = document.querySelectorAll(".quick-login");


quickLoginBtns.forEach( (item, index) => {

    //item : 현재 반복 시 꺼내온 객체
    //index : 현재 반복 중인 인덱스

    //quickLoginBtns 안에 있는 요소인 button 태그를 하나씩 꺼내서 이벤트 리스너 추가함
    item.addEventListener("click",()=>{

        const email = item.innerText; //버튼에 작성된 이메일 얻어오기
        location.href = "/member/quickLogin?memberEmail=" + email; //get 방식으로 href 요청 보내는 방법
    
    });
} );


//-----------회원 목록 조회(비동기)------------
const selectMemberList = document.querySelector("#selectMemberList");
const memberList = document.querySelector("#memberList");

//td 요소를 만들고 text 추가 후 반환하기
const createTd = (text)=>{
    const td = document.createElement("td");
    td.innerText = text; //member[key]를 넣어준다
    return td; //<td>text<td>
}

//조회 버튼 클릭 시
selectMemberList.addEventListener("click", ()=>{

    fetch("/member/memberList")
    .then(resp=>resp.json()) //미리 json형식으로 바꿔줌
    .then(list=>{

        //console.log("result : " , result);
        list.innerHTML="";
        
        //tbody에 들어갈 요소를 만들고 값 세팅 후 추가
        list.forEach((member, index) => {
            const tr = document.createElement("tr");

            const itemArray = ["memberNo", "memberEmail", "memberNickname", "memberDelFl"];

            for(let key of itemArray){
                tr.append(createTd(member[key]))
                //createTd 함수에 키에 해당하는 값이 넘어간다.  \
                //createTd 함수에서 td생성, 해당하는 값 넣어주기를 하고 td를 돌려준다          
            }
            memberList.append(tr);
        }


    )});

});

//-----------------특정 회원 비밀번호 초기화(Ajax)-----------------

const resetPw = document.querySelector("#restPw");
const resetMemberNo = document.querySelector("#resetMemberNo");
const inputNo = resetMemberNo.value;

resetPw.addEventListener("click",()=> {

    if(inputNo.trim().length == 0){
        alert("회원번호 입력해주세요");
        return;
    }
    fetch("/member/resetPw?memberNo="+inputNo)
    .then(resp => resp.text())
    .then(result=>{
        
        if(result>0){
            alert("초기화 성공");
        }else{
            alert("해당 회원이 존재하지 않습니다!");
        }


    });

    /* <POST 형식으로 보내는 방법>
    fetch("member/resetPw",{
        method : "PUT"
        headers : {"Content-Type":"application/json"}
        body : inputNo
    })
    .then(resp =>resp.text())
    .then(result=>
        //result == 컨트롤러로부터 반환받아 text로 파싱한 값
        //"1", "0"

        if(result>0){
            alert("초기화 성공");
        }else{
            alert("해당 회원이 존재하지 않습니다!");
        }

    )
    */
})


//---------------------------특정회원(회원번호) 탈퇴 복구Ajax------------------

const resetorationMemberNo = document.querySelector("#resetorationMemberNo");
const restorationBtn = document.querySelector("#restorationBtn");

restorationBtn.addEventListener("click",()=> {

    fetch("/member/restoreMember?memberNo="+resetorationMemberNo.value)
    .then(resp => resp.text())
    .then(result =>{
        if(result>0) {
            alert("탈퇴 복구 성공!");
        }else{
            alert("탈퇴하지 않거나 존재하지 않는 회원입니다.");
        }
    } )
});












