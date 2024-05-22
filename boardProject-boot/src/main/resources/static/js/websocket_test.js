/*웹소켓 테스트*/

/*
1. SockJS 라이브러리 추가
   common.html에서 추가해서 사용 가능함

2. SockJS 객체 생성하기

3. 생성된 SockJS 객체를 이용해 메시지 전달

4. 서버로부터 현재 클라이언트에게 웹소켓을 이용한 메시지가 전달된 경우


*/

//2. 객체 생성
const testSock = new SockJS("/testSock"); //websockeConfig에 작성한 addHandler의 요청주소와 같아야함
// - 객체 생성 시 자동으로
//  ws://localhost(또는 ip)/testSock  으로 연결요청을 보냄

//3. 메시지 전달
const sendMessageFn = (name, str) =>{

    //JSON을 이용해서 데이터를 text 형태로 전달
    const obj = {
        "name" : name
        ,"str" : str
    };

    //연결된 웹소켓 핸들러로 JSON을 전달하기
    testSock.send(JSON.stringify(obj));
}

//4. 메시지 전달된 경우
testSock.addEventListener("message", e => {
    //e.data : 서버로부터 전달받은 메시지
    const msg = JSON.parse(e.data); // json -> js object로 변환됨
    console.log(`${msg.name} : ${msg.str}`);
    //  홍길동 : hi 로 나옴
});
