
//좋아요 버튼 누르면 비동기로 좋아요 INSERT or DELETE 하기

/* Thymeleaf 코드 해석 순서
 1. th : java코드 + SRPING EL
 2. html 코드 (+css, js)

*/

// 1) 로그인한 회원의 번호 얻어와 ==session에서 가져오기
// session은 서버에서 관리하기 때문에 JS에서 바로 얻어올 수 없다

// 2) 현재 게시글 번호 얻어오기
// 3) 좋아요 여부 알기

//1. id가 boardLike인 요소를 클릭했을 때
const boardLike = document.querySelector("#boardLike");

boardLike.addEventListener("click",e=>{
    // 2. 로그인 상태가 아닌 경우, 동작 x -> alert 띄우기
    if(loginMemberNo == null){
        alert("로그인 후 이용해 주세요");
        return;
    }


    // 3. 준비된 3개의 변수(memberNo, boardNo, likeCheck)를 객체로 저장하기(JSON 변환해서 넘기게)
    const obj={
        "memberNo" : loginMemberNo,
        "boardNo" : boardNo,
        "likeCheck" : likeCheck
    };


    // 4. 좋아요 INSERT/DELETE 비동기 요청
    fetch("/board/like",{
        method : "POST",
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(obj)
    }).then(resp, resp.text()) //반환 결과 text 형태로 변환
    .then(count => {

        if(count == -1){
            console.log("좋아요 처리 실패")
            return;
        }
        // 5. likeCheck 값이 0 -> 1/ 1 -> 0 (클릭할때마다 INSERT/DELETE 번갈아 가며 하도록)
        likeCheck = likeCheck == 0? 1:0;
        
        // 6. 하트를 채웠다 비웠다 바꾸기
        e.target.classList.toggle("fa-solid");
        e.target.classList.toggle("fa-solid");

        //7. 게시글 좋아요 수를 수정하기
        e.target.nextElementSibling.innerText = count;

        

    })





});




//-------------- 게시글 수정 버튼 --------------------
const updateBtn = document.querySelector("#updateBtn");

//updateBtn이 화면상에 존재할 때 ( 없을 때도 이벤트 리스너 존재 시 오류 남)
if(updateBtn != null){
    updateBtn.addEventListener("click",e=>{
        //GET 방식
        //현재 : /board/1/2001?cp=1
        //목표 : /editBoard/1/2001/update?cp=1

        // editBoard로 바꾸고, /update 가 붙어야 함
        location.href = location.pathname.replace('board', 'editBoard')
                        + "/update"
                        +location.search; //쿼리스트링 시작부분을 찾아서 그 자리에 붙여줌
        //현재 경로 중 "board"가 있으면 "editBoard"로 변경






    });
}


