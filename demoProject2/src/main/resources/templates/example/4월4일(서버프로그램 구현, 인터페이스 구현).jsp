<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!--서버 프로그램 구현-->
<!DOCTYPE html>

<html>
<head>
    <meta charset="UTF-8">

    <title>로그인 결과</title>
</head>
<body>

로그인 정보 : ${member}

</body>

</html>


<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>테스트 프로젝트</title>
</head>
<body>
    <h1>로그인 테스트</h1>

    <form action="/login" method="post">

    ID : <input type="text" id="id" name="inputId">

    PW : <input type="password" id="pw" name="inputPw">

    <button>로그인</button>

    </form>

</body>
</html>





<!--인터페이스 구현-->
///////////////

[index.html]

<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>

</head>
<body>

    <h1>학생 조회</h1>

    <form action="/student/select" method="POST">

        이름 : <input type="text" name="name"> <br>
        나이 : <input type="number" name="age"> <br>
        주소 : <input type="text" name="addr"> <br>

        <button>제출하기</button>
    </form>

</body>
</html>

[student/select.html]

<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>

</head>
<body>

    <h1>조회 결과</h1>

    <h4 th:text="${stdName}+님"></h4>
    <h4 th:text="${stdAge}+세"></h4>
    <h4 th:text="${stdAddress}"></h4>
</body>
</html>