
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
    .then()
});









