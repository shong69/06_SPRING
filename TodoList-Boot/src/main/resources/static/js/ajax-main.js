/*요소를 얻어와서 변수에 저장하기 */
const totalCount = document.querySelector("#totalCount");
const completeCount = document.querySelector("#completeCount");
const reloadBtn = document.querySelector("#reloadBtn");

//할 일 추가 관련 요소
const todoTitle = document.querySelector("#todoTitle");
const todoContent = document.querySelector("#todoContent");
const addBtn = document.querySelector("#addBtn");

//할 일 목록 조회 관련 요소
const tbody = document.querySelector("#tbody");

//할 일 상세 조회 관련 요소
const popupLayer=document.querySelector("#popupLayer");
const popupTodoNo = document.querySelector("#popupTodoNo");
const popupTodoTitle = document.querySelector("#popupTodoTitle");
const popupComplete = document.querySelector("#popupComplete");
const popupRegDate = document.querySelector("#popupRegDate");
const popupTodoContent = document.querySelector("#popupTodoContent");
const popupClose = document.querySelector("#popupClose");

//상세 조회 버튼
const deleteBtn = document.querySelector("#deleteBtn");
const updateView = document.querySelector("#updateView");
const changeComplete = document.querySelector("#changeComplete");

//수정 레이어 버튼
const updateLayer = document.querySelector("#updateLayer");
const updateTitle = document.querySelector("#updateTitle");
const updateContent = document.querySelector("#updateContent");
const updateBtn = document.querySelector("#updateBtn");
const updateCancel = document.querySelector("#updateCancel");




//전체 todo 개수를 조회 + 출력하는 함수 정의하기
function getTotalCount() {

    //비동기로 서버에(DB)에서 전체 Todo 개수를 조회하는
    //fetch() API 코드 작성하기
    //fetch : 가지고 오다
    //요청 안적으면 기본값 get 요청임
    fetch("/ajax/totalCount") //controller에 비동기 요청 수행 요구함 -> Promise 객체를 반환한다
    .then( response => { 
        // then의 매개변수 response : 비동기 요청에 대한 응답이 담긴 객체를 의미함

        console.log("response : ", response);
        //response.text() : 응답 데이터를 문자열/숫자 형태로 변환한 결과를 가지는 Promise객체로 반환한다
        return response.text(); 

    })
    //두번째 then의 매개변수 result : 첫번째 then에서 반환된 Promise 객체의 Result 값
    .then(result =>{
        //result : controller 메서드에서 반환된 진짜 값

        console.log("result : ", result);

        //#totalCount 요소의 내용을 result로 변경해주기
        totalCount.innerText = result;

    });
}




//completeCount 값 비동기 통신으로 얻어와서 화면 출력
function getcompleteCount(){
    //fetch : 비동기로 요청해서 결과 데이터를 가져올 수 있게만든 API

    //첫번째 then의 response
    // - 응답 결과, 요청 주소, 응답 데이터 등의 정보가 담김

    //response.text() : 응답 데이터를 text 형태로 변환 - 단일형태에서만 가능하다

    //두번째 then의 result : 첫번째 then에서 text로 변환된 응답 데이터

    fetch("/ajax/completeCount")
    .then(response=>{
        return response.text();
    }).then(result=>{
        completeCount.innerText=result;
    });


}


//새로고침 버튼이 클릭 되었을 때
reloadBtn.addEventListener("click", ()=>{
    getcompleteCount();//비동기로 전체 할 일 개수 조회
    getTotalCount();  //비동기로 완료된 할 일 개수 조회
});






////////// 할일 추가 버튼 클릳 시 동작

addBtn.addEventListener("click",()=>{

    //비동기로 할 일 추가하는 fetch() 코드 작성
    //- 요청 주소 : "/ajax/add"
    //- 데이터 전달 방식(method) : post


    //파라미터를 저장한 JS객체 만들기
    const param = {
        //K:V 로 쓰면 됨

        "todoTitle" : todoTitle.value,
        "todoContent" : todoContent.value
    };


    fetch("/ajax/add", {
        method : "POST", //옵션을 중괄호 안에다 작성함
        headers : {"Content-Type":"application/json"}, //요청 데이터의 형식으로 JSON(string)으로 지정해서 보내겠다
        body : JSON.stringify(param) //param이라는 js객체를 JSON으로 변환해줌
    })
    .then(resp => resp.text()) //반환된 값을 text로 변환
    .then(temp =>{  //매개변수 temp : 이름은 상관 없음
        //첫번째 then에서 반환된 값 중 변환된 text를 temp에 저장함
        if(temp>0){ //insert 성공
            alert("추가 성공!!!");

            //추가 성공한 제목, 내용 지우기
            todoTitle.value="";
            todoContent.value="";

            //할일이 추가됐으니까 전체 Todo 개수 다시 조회하기(비동기 방식)
            getTotalCount(); 

            //할 일 목록 다시 조회
            selectTodoList();

        }else{ //실패
            alert("추가 실패...");
        }
    });

});

//------------------------------------------
//비동기로 할 일 상세 조회하는 함수
const selectTodo = (url)=>{

    //매개변수 url == "ajax/detail?todoNo=10" 형태의 문자열
    //url을 매개변수로 넘겨주고 있어서 controller에서 requestparam사용 가능

    /*fetch(url)
    .then(resp => resp.text())
    .then(result => {
        const todo = JSON.parse(result);
    }); */


    //response.json() : 응답데이터가 JSON인 경우 자동으로 js 객체로 변환해준다
    // ==JSON.parse(JSON 데이터)
    fetch(url)
    .then(resp=> resp.json())
    .then(todo =>{ //result가 알아서 object로 파싱됨
        //매개변수 todo : 서버응답(JSON)이 Object로 변환된 값
        console.log(todo);

        //popup Layer에 조회된 값을 출력하기

        popupTodoNo.innerText       =todo.todoNo; //object에 key값으로 접근해서 value값 얻어온 것임
        popupTodoTitle.innerText    =todo.todoTitle;
        popupComplete.innerText     =todo.complete;
        popupRegDate.innerText      =todo.regDate;
        popupTodoContent.innerText  =todo.todoContent;

        //popuplayer 보이게 하기
        popupLayer.classList.remove("popup-hidden");

        //만약 updateLayer가 열려있으면 숨기기
        updateLayer.classList.add('popup-hidden');
    });



}

//상세조회 X버튼 누르면 닫히게
popupClose.addEventListener("click",()=>{
    popupLayer.classList.add("popup-hidden");
});

//-------------------------------------------
//비동기로 할 일 목록을 조회하는 함수

const selectTodoList = ()=>{
    fetch("/ajax/selectList")
    .then(resp => resp.text()) //응답 결과를 text로 변환
    .then(result => {
        console.log("result : ", result);
        console.log(typeof result); //string으로 나옴 : 객체가 아닌 문자열 형태

        //문자열 형태로 넘어온 JSON을 가공은 할 수 있지만 사용하기 힘듦
        //-> JSON 형태를 JS 객체타입으로 변환시키기 : JSON.parse(JSON데이터) 
        //JSON.parse(JSON데이터) : string(JSON데이터) -> object(JS object)

        //JSON.stringify(JS Object) : object -> string 형변환
        //-JS Object 타입을 string 형태의 JSON 데이터로 변환해줌

        const todoList = JSON.parse(result);

        console.log("todoList : ", todoList);

        //--------------------------

        //기존에 출력되어 있던 할 일 목록을 모두 삭제해주기
        tbody.innerHTML="";


        //#tbody에 tr/td 요소를 생성해서 내용 추가하기

        for(let todo of todoList){ //js의 향상된 for문
            //tr 태그 생성하기
            const tr = document.createElement("tr");

            const arr = ['todoNo', 'todoTitle', 'complete', 'regDate'];

            for(let key of arr){
                const td = document.createElement("td");
                
                //제목인 경우
                if(key === 'todoTitle'){
                    const a = document.createElement("a"); //a태그 생성하기
                    a.innerText = todo[key]; //key값 줘서 해당 위치의 value 값에 내용 대입하기
                    a.href = "/ajax/detail?todoNo="+todo.todoNo; //a태그의 주소값에 현재 todoNo넘기기
                    td.append(a);
                    tr.append(td);

                    //a태그 클릭 시 기본 이벤트(페이지 이동==동기요청) 막기
                    a.addEventListener("click", (e)=>{
                        e.preventDefault(); //기본 이벤트 막기

                        //할 일 상세 조회(비동기요청으로)

                        //e.target.href : 클릭한 a태그의 href 속성 값
                        selectTodo(e.target.href);

                    });
                    continue;
                }

                td.innerText = todo[key];
                tr.append(td);
            }
            //tbody의 자식으로 tr(한 행) 추가하기
            tbody.append(tr);
        }

    })
};

//-----------------------------------------------------------

// 삭제 버튼 클릭 시 
deleteBtn.addEventListener("click", ()=>{
    //취소 클릭 시 아무것도 안함
    if(!confirm("정말 삭제 하시겠습니까?")){return;} //취소 클릭 시 아무것도 안하도록

    //삭제할 할 일의 번호 얻어오기
    const todoNo = popupTodoNo.innerText; //popupTodoNo 내용 얻어오기

    //비동기 DELETE 방식 요청
    fetch("/ajax/delete", {
        method : "DELETE", //DELETE 방식 요청 -> @DeleteMapping으로 처리해야함
        
        //데이터 하나만을 전달해도 요청방식이 JSON형태임을 알려줘야 한다
        headers : {"Content-Type" : "application/json"},
        body : todoNo //todoNo값을 body에 담아서 전달함
                      //->@RequestBody로 꺼내야 함
    })
    .then(resp => resp.text())
    .then(result => {
        if(result>0) {  //삭제 성공했다면
            alert("삭제 되었습니다.");
            //상세조회 창 닫기
            popupLayer.classList.add("popup-hidden");

            //전체, 완료된 할 일 개수 다시 조회
            // + 할 일 목록 다시 조회
            getTotalCount();
            getcompleteCount();
            selectTodoList();

        }else{ //삭제 실패
            alert("삭제 실패..!");
        }
    })
});


//---------------------------------------------
//완료 여부 변경 버튼 클릭 시
changeComplete.addEventListener("click", ()=>{

    //변경할 할 일 번호, 완료 여부 (Y <->N)
    const todoNo = popupTodoNo.innerText;
    const complete = popupComplete.innerText==='Y'?'N':'Y';  //Y면 N으로 바꾸고 아니면 반대로 바꾸기

    //SQL 수행에 필요한 값을 JS 객체로 묶기
    const obj={"todoNo" :todoNo, "complete":complete};

    //비동기로 완료 여부를 변경하기
    fetch("/ajax/changeComplete",{
        method:"PUT",
        headers:{"Content-Type":"application/json"},
        body: JSON.stringify(obj) //obj라는 객체를 JSON형태로 변경하기(자바 사용 쉽게)
    })
    .then(resp => resp.text())
    .then(result => {

        if(result > 0){
            //update된 DB데이터를 다시 조회해서 화면에 출력하기
            //selectTodo();  -> 서버 부하가 큼

            //서버 부하를 줄이기 위해 상세 조회에서 Y/N만 바꾸기
            popupComplete.innerText=complete;

            //완료여부 개수도 바꾸기 -> 서버 부하 줄이기 위해 +-1씩만 바꾸도록 하기
            //getCompleteCount();
            const count = Number(completeCount.innerText); //js 숫자 number로 형변환해주기

            if(complete === 'Y') completeCount.innerText = count + 1;
            else                 completeCount.innerText = count - 1;

            //서버 부하 줄이기 가능하지만 코드가 조금 복잡하니까 걍 다시 조회하기
            selectTodoList();
        }else{
            alert("완료 여부 변경 실패!!");
        }
    });



});


//------------------------------------------------
//상세조회에서 수정 버튼 (#updateView) 클릭 시 
updateView.addEventListener("click", ()=>{
    //기존 팝업레이어를 숨기기
    popupLayer.classList.add('popup-hidden');

    //수정레이어 보이게
    updateLayer.classList.remove('popup-hidden');

    //수정 버튼 누른 상태에서 상세조회버튼도 또 누른 경우 둘 다 겹쳐서 뜨게 됨
    //상세조회 기능에서 끄도록 설정하기

    //수정레이어 보일 때 
    //팝업레이어에 작성된 제목, 내용을 얻어와 세팅하기
    updateTitle.value = popupTodoTitle.innerText;
    updateContent.value = popupTodoContent.innerHTML.replaceAll("<br>", "\n"); //value말고 innerText로 적용해도 됨
    //br태그를 줄넘김으로 바꾸도록
    //html에서는 줄바꿈이 <br>로 인식되는데, textarea로 넘어오며 \n로 적용해야 줄바꿈으로 인식된다.
    
    //수정 레이어 -> 수정 버튼에 data-todo-no 속성 추가하기
    updateBtn.setAttribute("data-todo-no", popupTodoNo.innerText);
});

//----------------------------------------------------------
//수정레이어에서 취소 버튼(#updateCancel)을 눌렀을 때
updateCancel.addEventListener("click", ()=>{
    //수정 레이어 숨기기
    updateLayer.classList.add("popup-hidden");

    //상세 팝업 레이어 보이기
    popupLayer.classList.remove("popup-hidden");
});



//----------------------------------------------------------
updateBtn.addEventListener("click", e=>{
    
    //서버로 전달해야 하는 값을 객체로 묶어둠
    const obj={
        "todoNo" : e.target.dataset.todoNo,
        "todoTitle" : updateTitle.value, 
        "todoContent" : updateContent.value
    };
    //비동기 요청
    fetch("/ajax/update", {
        method:"PUT",
        headers:{"Content-Type" : "application/json"},
        body : JSON.stringify(obj)
    })
    .then(resp => resp.text())
    .then(result => {
        if(result > 0){
            alert("수정 성공!!");

            //수정 레이어 숨기기
            updateLayer.classList.add('popup-hidden');

            //목록 다시 조회하기
            selectTodoList();

            popupTodoTitle.innerText = updateTitle.value;
            popupTodoContent.innerHTML =    //innerHtml : 줄바꿈 br 태그 인식하기 위해
                    updateContent.value.replaceAll("\n", "<br>"); 

            popupLayer.classList.remove("popup-hidden");

            //수정 레이어에 있는 남은 흔적 제거하기
            updateTitle.value="";
            updateContent.value="";
            updateBtn.removeAttribute("data-todo-no"); //속성 제거

        }else{
            alert("수정 실패!!");
        }
    });
});







//페이지 새로고침 시 불러오기
selectTodoList();
getcompleteCount(); 
getTotalCount();  //함수 호출 -> 페이지 로드 될 때도 불러오도록 놔두기
