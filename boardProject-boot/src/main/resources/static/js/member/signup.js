
/*daum 주소 API 사용하기*/

function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }



            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById("address").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("detailAddress").focus();
        }
    }).open();
}

//주소 검색 버튼 클릭 시 
document.querySelector("#searchAddress").addEventListener("click",execDaumPostcode);
//함수 미리 정의 후 대입하는 방식

//-----------------------------------------------
//****회원가입 유효성 검사  *****//

// 필수 입력 항목의 유효성 검사 여부를 체크하기 위한 객체 생성하기
// - true == 해당 항목은 유효한 형식으로 작성됨
// - false == 해당 항목은 유효하지 않은 형식으로 작성됨

const checkObj = {
    "memberEmail"       : false,
    "memberPw"          : false,
    "memberPwConfirm"   : false,
    "memberNickname"    : false,
    "memberTel"         : false,
    "authKey"           : false
}; //전부 true 값으로 바뀌어야 회원가입 submit 되도록한다

//-----------------------------------
/*이메일 유효성 검사*/

//1) 이메일 유효성 검사에 사용될 요소 얻어오기
const memberEmail = document.querySelector("#memberEmail");
const emailMessage = document.querySelector("#emailMessage"); 

//2) 이메일이 입력될 때마다(keyup/input) 유효성 검사 수행하기
memberEmail.addEventListener("input",e =>{

    //이메일 인증 후 이메일이 변경된 경우 ->나중에 처리

    //작성된 이메일 값 얻어오기
    const inputEamil = e.target.value;

    //3) 입력된 이메일이 없을 경우 따져보기
    if(inputEamil.trim().length === 0){
        emailMessage.innerText = "메일을 받을 수 있는 이메일을 입력해주세요.";  //썻다가 다시 지우는 경우에 다시 바꿔주기 위해서
        
        //메시지에 색상을 추가하는 클래스 모두 제거
        emailMessage.classList.remove('confirm', 'error');

        //이메일 유효성 검사 여부를 false로 변경하기
        checkObj.memberEmail = false;

        //잘못 입력한 띄어쓰기가 있을 경우 없애기
        memberEmail.value="";

        return;
    }

    //4) 작성된 이메일이 있을 때, 정규식 검사 하기
    // (알맞은 형태로 작성했는지 검사)
    const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/; //@naver. -> \.  , {2, } 마지막 .이후로 두글자 이상 적기
    
    
    //입력받은 이메일이 정규식과 일치하지 않는 경우
    //(알맞은 이메일 형태가 아닌 경우)
    if( !regExp.test(inputEamil) ){
        emailMessage.innerText="알맞은 이메일 형식으로 작성해주세요.";
        emailMessage.classList.add('error'); //글자 빨간색 부여 css
        emailMessage.classList.remove('confirm'); //이전에 성공했던 경우 주었던 글자 초록색 css 제거
        checkObj.memberEmail = false; //유효하지 않은 이메일임을 기록하기
        return;
    }

    //5) 유효한 이메일 형식인 경우 1. 중복검사 수행하기
    //비동기(Ajax)이용하기
    fetch("/member/checkEmail?memberEmail="+inputEamil)
    .then(resp => resp.text())
    .then(count => {
        //count : 1이면 중복, 0이면 중복 아님
        if(count==1){
            emailMessage.innerText="이미 사용중인 이메일 입니다.";
            emailMessage.classList.add('error');
            emailMessage.classList.remove('confirm');
            checkObj.memberEmail= false; //중복은 유효하지 않음
            return;
        }

        //중복 X인 경우
         emailMessage.innerText="사용 가능한 이메일 입니다.";
         emailMessage.classList.remove('confirm');
         emailMessage.classList.add('error');
         checkObj.memberEmail=true; //유효한 이메일
    })
    .catch(error=>{
        //fetch() 수행 중 예외 발생 시 처리
        console.log(error);
    });


});

//------------------------------------------
/* 이메일 인증 */

//인증번호 받기 버튼
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");

//인증번호 입력 input
const authkey = document.querySelector("#authKey");

//인증번호 입력 후 확인 버튼
const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn");

//인증번호 관련 메시지 출력 버튼
const authKeyMessage= document.querySelector("#authKeyMessage");


let authTimer; //타이머 역할을 할 setInterval을 저장할 변수
const initMin = 4; //타이머 초기값(분)
const intitSec= 59; //타이머 초기값 (초)
const initTime = "05:00";

//실제 줄어드는 시간을 저장할 변수
let min = initMin;
let sec= intitSec;


//인증번호 받기 버튼 클릭 시
sendAuthKeyBtn.addEventListener("click",()=>{
    checkObj.authKey = false;
    authKeyMessage = "";
    //중복되지 않은 유효한 이메일을 입력한 경우가 아니면
    if(!checkObj.memberEmail){
        alert("유효한 이메일 작성 후 클릭해 주세요");
        return;
    }
    //클릭 시 타이머 숫자 초기화
    min = initMin;
    sec = intitSec;

    //이전 동작중인 인터벌 클리어
    clearInterval(authTimer);

    //************************************************ */

    // 비동기로 서버에서 메일 보내기
    fetch("/email/signup", {
        method : "POST",
        headers : {"Content-Type" : "application/json"},
        body : memberEmail.value
    })
    .then(resp=> resp.text())
    .then(result => {
        if(result == 1){
            console.log("인증 번호 발송 성공");
        }else{
            console.log("인증 번호 발송 실패");
        }
    });



    //************************************************ */


    //메일은 비동기로 서버에서 보내라고 하고
    //화면에서는 타이머 시작하기
    authKeyMessage.innerText=initTime; //05:00 세팅
    authKeyMessage.classList.remove("confirm", "error"); //검정 글씨
    
    alert("인증번호가 발송되었습니다.");

    //setInterval(함수, 지연시간(ms))
    // - 지연시간(ms)만큼 시간이 지날 때마다 함수 수행

    //clearInterval(Interval이 저장된 변수)
    // - 매개변수로 전달받은 interval을 멈춤

    //인증 시간 출력(1초마다 동작)
    authTimer = setInterval(()=>{

        authKeyMessage.innerText=`${addZero(min)}:${addZero(sec)}`;

        //0분 0초인 경우("00:00"출력 후에)
        if(min == 0 && sec == 0){
            checkObj.authKey=false;
            clearInterval(authTimer);
            authKeyMessage.classList.add('error');
            authKeyMessage.classList.remove('confirm');
            return;
        }

        //0초인 경우(0초를 출력한 후)
        if(sec==0){
            sec= 60;
            min--;
        }
        sec--; //1초 감소

    }, 1000); //1초 지연시간

});

//전달받은 숫자가 10 미만인 경우 (한자리) 앞에 0 붙여서 반환
function addZero(number){
    if(number<10) return "0" + number;
    else          return number;
}


//------------------------------------------------------------
// 인증하기 버튼 클릭 시 
// 입력된 인증 번호를 가지고 비동기로 서버에 전달하기
// -> 입력된 인증번호와 발급된 인증번호가 같은지 비교하기
//   같으면 1/ 아니면 0 반환
// 단, 타이머가 00:00초가 아닐때만 수행해줌

checkAuthKeyBtn.addEventListener("click",()=>{

    if(min === 0 && sec === 0){ //타이머가 00:00인 경우
        alert("인증번호 입력 제한시간을 초과하였습니다.");

        return;
    }
    if(authkey.value.length<6){ //인증번호는 6자리임->제대로 입력 안한 경우
        alert("인증번호를 정확히 입력해 주세요");
        return;
    }

    //입력받은 이메일, 인증번호로 객체 생성 ->body에 실어버려
    const obj={
        "email" : memberEmail.value,
        "authKey" : authkey.value
    };

    fetch("/email/checkAuthKey",{
        method : "POST",
        headers: {"Content_Type" : "application/json"},
        body :JSON.stringify(obj) //obj를 JSON으로 변경하기
    })
    .then(resp=>resp.text()) //0이나 1인 단일 숫자값
    .then(result =>{

        // == : 값만 비교 문자"1" == 숫자 1
        // === : 타입 + 값비교 문자 "1" != 숫자 1
        if(result==0){
            alert("인증번호가 일치하지 않습니다!");
            checkObj.authKey = false;
            return;
        }

        clearInterval(authTimer); //타이머 멈추기

        authKeyMessage.innerText = "인증 되었습니다.";
        authKeyMessage.classList.remove('error');
        authKeyMessage.classList.add('confirm');

        checkObj.authKey = true; //인증번호 검사여부 true로 바꾸기
    });

});



//------------------------------------------
/*비밀번호 / 비밀번호 확인 유효성 검사하기*/

//1) 비밀번호 관련 요소 얻어오기
const memberPw = document.querySelector("#memberPw");
const memberPwConfirm = document.querySelector("#memberPwConfirm")
const pwMessage = document.querySelector("#pwMessage");


//5) 비밀번호, 비밀번호 확인란이 같은지 검사하는 함수
const checkPw = () =>{

    //같을 경우
    if(memberPw.value == memberPwConfirm.value){
        pwMessage.innerText="비밀번호가 일치합니다.";
        pwMessage.classList.add('confirm');
        pwMessage.classList.remove('error');
        checkObj.memberPwConfirm= true; //비밀번호 확인 true
        return;
    }
    //다를 경우
    pwMessage.innerText="비밀번호가 일치하지 않습니다.";
    pwMessage.classList.add('error');
    pwMessage.classList.remove('confirm');
    checkObj.memberPwConfirm=false; //비밀번호 확인 false

};


// 2) 비밀번호 유효성 검사
memberPw.addEventListener("input", e=>{
    //입력받은 비밀번호 값
    const inputPw = e.target.value;

    //3) 입력되지 않은 경우
    if(inputPw.trim().length === 0){
        pwMessage.innerText="영어,숫자,특수문자(!,@,#,-,_) 6~20글자 사이로 입력해주세요.";
        pwMessage.classList.remove('confirm', 'error'); //검은 글씨로 나오도록
        checkObj.memberPw=false; //비밀번호가 유효하지 않다고 표시
        memberPw.value=""; //처음에 띄어쓰기 입력 못하게 하기
        return;
    }

    // 4) 입력받은 비밀번호에 정규식 검사하기
    const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;

    if(!regExp.test(inputPw)){ //유효하지 않으면 
        pwMessage.innerText="비밀번호가 유효하지 않습니다.";
        pwMessage.classList.add('error');
        pwMessage.classList.remove('confirm');
        checkObj.memberPw=false;
        return;
    }


    //유효한 경우
    pwMessage.innerText= "유효한 비밀번호 형식입니다.";
    pwMessage.classList.add('confirm');
    pwMessage.classList.remove('error');
    checkObj.memberPw=true;


    //비밀번호 입력 시 확인란의 값과 비교하는 코드 추가

    //비밀번호 확인란에 값이 작성돼있을 때만 비교함
    if(memberPwConfirm.value.length>0){
        checkPw();
    }
});

// 6) 비밀번호 확인란 유효성 검사
//단, 비밀번호(memberPw)가 유효할 때만 검사 수행하기

memberPwConfirm.addEventListener("input", ()=>{
    if(checkObj.memberPw){ //true일 때
        checkPw(); //비교 함수 수행
        return;
    }

    //memberPw가 유효하지 않은 경우
    //memberPwConfirm 검사 X
    checkObj.memberPwConfirm = false;
});



//---------------------------------------------------------
/* 닉네임 유효성 검사 */

const memberNickname = document.querySelector("#memberNickname");
const nickMessage = document.querySelector("#nickMessage");

memberNickname.addEventListener("input",e=>{

    const inputNickname = e.target.value;
    // 1) 입력 안 한 경우
    if(inputNickname.trim().length==0){ 
        nickMessage.innerText = "한글,영어,숫자로만 2~10글자";
        nickMessage.classList.remove('error', 'confirm');
        checkObj.memberNickname=false;
        memberNickname.value="";
        return;
    }

    //정규식 검사
    const regExp = /^[가-힣\w\d]{2,10}$/;

    if(!regExp.test(inputNickname)){ //유효 X
        nickMessage.innerText = "닉네임이 유효하지 않습니다.";
        nickMessage.classList.add('error');
        nickMessage.classList.remove('confirm');
        checkObj.memberEmail = false;
        return;
    }

    //중복 검사
    fetch("/member/checkNickname?memberNickname="+inputNickname)
    .then(resp=>resp.text())
    .then(result=>{

        if(result==1){ //중복 O
            nickMessage.innerText="이미 사용중인 닉네임 입니다."
            nickMessage.classList.add('error');
            nickMessage.classList.remove('confirm');
            checkObj.memberNickname = false;
            return;
        }

        nickMessage.innerText= "사용 가능한 닉네임입니다."
        nickMessage.classList.add('confirm');
        nickMessage.classList.remove('error');
        checkObj.memberNickname = true;
    
    })
    .catch(err=> console.log(err));

});


//--------------------------------------------------
/* 전화번호 유효성 검사 */

const memberTel = document.querySelector("#memberTel");
const telMessage = document.querySelector("#telMessage");

memberTel.addEventListener("click",e=>{

    const inputMemberTel = e.target.value;

    //입력되지 않은 경우
    if(inputMemberTel.trim().length ==0){
        telMessage.innerText = "전화번호를 입력해주세요.(- 제외)";
        telMessage.classList.remove('error', 'confirm');
        checkObj.memberTel = false;
        memberTel.value = "";
        return;
    }
    //정규식 검사
    const regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;

    if(!regExp.test(inputMemberTel)){
        telMessage.innerText = "전화번호가 유효하지 않습니다.";
        telMessage.classList.remove('confirm');
        telMessage.classList.add('error');
        checkObj.memberTel = false;
        return;
    }

    //유효한 경우
    telMessage.innerText="유효한 전화번호 형식입니다.";
    telMessage.classList.add('confirm');
    telMessage.classList.remove('error');
    checkObj.memberTel = true;


    console.log(checkObj);
});

// -----------------------------------------
//회원가입 버튼 클릭 시 전체 유효성 검사 여부 확인
const signUpForm  = document.querySelector("#signUpForm");

//회원가입 폼 제출 시 
signUpForm.addEventListener("submit",e=>{
    //checkOBJ의 저장된 값(value) 중 하나라도 false가 있으면 제출X
    //하나씩 꺼내오기

    //for ~ in 문(객체 전용 향상된 for문)
    for(let key in checkObj){ //checkOBJ 요소의 key 값을 순서대로 꺼내온다

        if( !checkObj[key]){ //false인 경우
            let str; //출력할 메시지를 저장할 변수

            switch(key){
                case "memberEmail" : 
                    str = "이메일이 유효하지 않습니다"; break;
                case "authKey":
                    str = "이메일이 인증되지 않았습니다"; break;
                case "memberPw" :
                    str = "비밀번호가 유효하지 않습니다"; break;
                case "memberPwConfirm":
                    str="비밀번호가 일치하지 않습니다"; break;
                case "memberNickname":
                    str="닉네임이 유효하지 않습니다" ; break;
                case "memberTel":
                    str="전화번호가 유효하지 않습니다"; break;
            }

            alert(str);

            //문제가 되는 쪽으로 포커스 이동하기
            document.getElementById(key).focus();

            e.preventDefault(); //form 태그 기본 이벤트(제출) 막기
            return;
        }

    }

});












