//목록으로 버튼 동작
const goToList = document.querySelector("#goToList");

goToList.addEventListener("click", ()=>{
    location.href='/' ; //메인페이지 요청
    //location.href == 현재 문서의 url을 나타내는 것이다.

});


//완료여부 변경 버튼 동작
const completeBtn = document.querySelector(".complete-btn");

completeBtn.addEventListener("click", (e)=>{
    const todoNo = e.target.dataset.todoNo /*요소.dataset.필드값으로 값을 가져옴->th:data특 */

    //console.log(todoNo); 뜨는지 확인

    //클릭할 때마다 Y<->N으로 변경되도록
    //1.현재 상태를 인식하기
    let complete = e.target.innerText; //버튼에 뜨고 있는 N 혹은 Y

    //2. 삼항연산자로 값 바꿔주기
    complete = (complete === 'Y')? 'N': 'Y'; // === 세개짜리는 일치연산자로 type까지 같은지 확인해줌

    //3. 완료 여부 수정 요청하기
    location.href = `/todo/changeComplete?todoNo=${todoNo}&complete=${complete}`;  //리터럴 템플릿 : 문자열로 인식되면서 변수도 인식됨    
    //todoNo과 완료여부 상태 쿼리 스트링으로 보내주기(리터럴 템플릿 쓴 이유)

    //완료여부 상태를 보내주면서 그 값으로 바꿔달라는 쿼리도 같이 써야 한다

})

//---------------------------------------------------
//수정 버튼 클릭 시 동작

const updateBtn = document.querySelector("#updateBtn")

//e 매개변수 하나니까 화살표 함수 괄호 삭제 가능한
updateBtn.addEventListener("click", e=>{

    //data-todo-no 얻어오기
    const todoNo = e.target.dataset.todoNo;

    location.href=`/todo/update?todoNo=${todoNo}`;
});

//----------------------------------------------------
//삭제 버튼 클릭 시
const deleteBtn = document.querySelector("#deleteBtn");

deleteBtn.addEventListener("click", e=>{

    //삭제 확인 컨펌창 띄우기
    if(confirm("정말 삭제하시겠습니까?")){
        //확인 누르면 true 반환
        location.href=`/todo/delete?todoNo=${e.target.dataset.todoNo}`;
    }
    


})