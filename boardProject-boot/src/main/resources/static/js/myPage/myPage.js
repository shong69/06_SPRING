/*회원 정보 수정 페이지*/

const updateInfo = document.querySelector("#updateInfo"); //form 태그




//#updateInfo 요소가 존재할 때만 수행하기
if(updateInfo != null){

    //form 제출 시
    updateInfo.addEventListener("submit", e=>{

        const memberNickname =  document.querySelector("#memberNickname");
        const memberTel = document.querySelector("#memberTel");
        const memberAddress = document.querySelectorAll("[name='memberAddress']");

        //닉네임 유효성 검사
        if(memberNickname.value.trim().length === 0){
            alert("닉네임을 입력해 주세요");
            e.preventDefault(); //submit 막기
            return;
        }

        //닉네임 정규식
        let regExp = /^[가-힣\w\d]{2,10}$/;  //regExp 재활용 하게 let으로 바꿈
        if(!regExp.test(memberNickname.value)){
            alert("닉네임이 유효하지 않습니다.");
            e.preventDefault(); //제출 막기
            return;
        }

        //**************닉네임 중복검사는 알아서 해봐라 */
        //테스트 시 db와 닉네임 중복 안되게 하기
        //****************************************** */


        //전화번호 유효성 검사하기
        if(memberTel.value.trim().length===0){
            alert("전화번호를 입력해주세요");
            e.preventDefault();
            return;
        }

        //정규표현식 검사
        regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;
        if(!regExp.test(memberTel.value)){
            alert("전화번호가 유효하지 않습니다.");
            e.preventDefault();
            return;
        }


        //주소 유효성 검사
        //입력이 없으면 전부 검사 X
        //입력 시 세 input 전부 검사

        const addr0 = memberAddress[0].value.trim.length==0; //boolean 값이 들어간다
        const addr1 = memberAddress[1].value.trim.length==0;
        const addr2 = memberAddress[2].value.trim.length==0;
        
        //모두 true인 경우만 true 저장 된다
        const result1 = addr0 && addr1 && addr2; //아무것도 입력 X. true인 상태
        //모두 false인 경우에만 true 저장 된다
        const result2 = !(addr0 || addr1 || addr2); //모두 다 입력함

        //모두 입력 또는 모두 미입력이 아니면
        if(!(result1 || result2)){
            alert("주소를 모두 작성 또는 미작성 해주세요");
            e.preventDefault();
        }
    });
}



/****************비밀번호 변경 페이지**************** */ 


const changePw = document.querySelector("#changePw");

if(changePw != null){ //chagnePW 가 null이 아닐 때 -> 요소가 존재할 때

    changePw.addEventListener("submit", e=>{

        const currentPw = document.querySelector("#currentPw");
        const newPw = document.querySelector("#newPw");
        const newPwConfirm = document.querySelector("#newPwConfirm");


        // - 값을 모두 입력했는가 따져주기
        let str; //상황별 문구 넣어줄거임 (undefined 상태)

        if(currentPw.value.trim().legnth==0) str= "현재 비밀번호를 입력해주세요";
        else if(newPw.value.trim().length==0) str = "새 비밀번호를 입력해주세요";
        else if(newPwConfirm.value.trim().length==0) str = "새 비밀번호 확인을 입력해주세요";

        if(str != undefined){ //if 문 중 하나가 실행된 경우

            alert(str);
            e.preventDefault();
            return;
        }

        //새 비밀번호 정규식 검사
        const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;

        if(!regExp.test(newPw.value)){
            alert("새 비밀번호가 유효하지 않습니다");
            e.preventDefault();
            return;
        }


        //새 비밀번호 == 새비밀번호 확인 
        if(newPw.value != newPwConfirm.value){
            alert("새 비밀번호가 일치하지 않습니다");
            e.preventDefault();
            return;
        }

        //나머지 경우는 정상인 경우. update하러 controller에 가야한다


    });
    
}


// ----------------------------------------------------------------
/* 탈퇴 유효성 검사 */
//탈퇴 form 태그

const secession = document.querySelector("#secession");

if(secession != null) {
    secession.addEventListener("submit", e=>{

        const memberPw = document.querySelector("#memberPw");
        const agree = document.querySelector("#agree"); //체크박스 
        
        // - 비밀번호 입력 되었는지 확인
        if(memberPw.value.trim().length==0) {
            alert("비밀번호를 입력해 주세요");
            e.preventDefault();
            return;
        }

        //약관 동의 체크 확인
        //checkbox 또는 radio는 checked 속성 이용 가능
        // - checked -> 체크 시 true, 미체크 시 false 반환
        if(!agree.checked){ //false일 때

            alert("약관에 동의해주세요");
            e.preventDefault();
            return;
        }

        //정말 탈퇴할 것인지 묻기
        if(!confirm("정말 탈퇴하시겠습니까?")) {
            alert("취소되었습니다.");
            e.preventDefault();
            return;
        }

    });
}