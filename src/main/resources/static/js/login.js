$(".witch>li").click(function (e) { 
    $(this).css("border-bottom","2px solid #fff").siblings().css("border-bottom","none");
    var aid = $(this).attr("id");
    if( aid === "registerButton"){
        $(".content>.loginOrRegisterBox").css("height","400px");
        $("#login").css("display","none");
        $("#register").css("display","block");
    } else if( aid === "loginButton") {
        $(".content>.loginOrRegisterBox").css("height","340px");
        $("#login").css("display","block");
        $("#register").css("display","none");
    }
});
function checkLoginUserName(obj){
    var len = obj.length;
    var checkLoginUserNameMsg = document.getElementById("checkLoginUserNameMsg");
    if(len < 4 || len > 12){
        checkLoginUserNameMsg.innerText = "用户名格式错误";
    } else{
        checkLoginUserNameMsg.innerText = "";
    }
}
function checkLoginPassword(obj){
    var len = obj.length;
    var checkLoginUsernameMsg = document.getElementById("checkLoginPasswordMsg");
    if(len < 6 || len > 16){
        checkLoginUsernameMsg.innerText = "密码格式错误";
    } else{
        checkLoginUsernameMsg.innerText = "";
    }
}
function checkLogin(){
    var msg1 = document.getElementById("checkLoginUserNameMsg").innerText;
    var msg2 = document.getElementById("checkLoginPasswordMsg").innerText;
    if(msg1 !== "" || msg2 !== ""){
        confirm("登录账号或密码格式有误, 请重新输入!");
        return false;
    }
    var val1 = document.getElementById("loginUsername").value;
    var val2 = document.getElementById("loginPassword").value;
    if(val1 === "" || val2 === ""){
        confirm("登录信息不能为空!");
        return false;
    }
    return true;
}

function checkRegisterUsername(obj){
    var len = obj.length;
    var checkLoginUserNameMsg = document.getElementById("checkRegisterUsernameMsg");
    if(len < 4 || len > 12){
        checkLoginUserNameMsg.innerText = "用户名必须在4 - 12个字符内!";
    } else{
        checkLoginUserNameMsg.innerText = "";
    }
}
function checkRegisterPassword(obj){
    var len = obj.length;
    var checkLoginUsernameMsg = document.getElementById("checkRegisterPasswordMsg");
    if(len < 6 || len > 16){
        checkLoginUsernameMsg.innerText = "密码必须在6 - 16个字符内!";
    } else{
        checkLoginUsernameMsg.innerText = "";
    }
    var hasEnteredConfirmPassword = (document.getElementById("confirmPassword").value !== "");
    if(hasEnteredConfirmPassword){
        checkRegisterPasswordSame();
    }

}
function checkRegisterPasswordSame(){
    var checkPassword = document.getElementById("confirmPassword").value;
    var password = document.getElementById("registerPassword").value;
    var checkRegisterPasswordSameMsg = document.getElementById("checkRegisterPasswordSameMsg");
    if(checkPassword !== password){
        checkRegisterPasswordSameMsg.innerText = "两次输入的密码不一致!";
    } else{
        checkRegisterPasswordSameMsg.innerText = "";
    }
}
function checkRegister(){
    var msg1 = document.getElementById("checkRegisterUsernameMsg").innerText;
    var msg2 = document.getElementById("checkRegisterPasswordMsg").innerText;
    var msg3 = document.getElementById("checkRegisterPasswordSameMsg").innerText;
    if(msg1 !== "" || msg2 !== "" || msg3 !== ""){
        confirm("注册的账号信息格式有误, 请重新输入!");
        return false;
    }
    var val1 = document.getElementById("registerUsername").value;
    var val2 = document.getElementById("registerPassword").value;
    var val3 = document.getElementById("confirmPassword").value;
    if(val1 === "" || val2 === "" || val3 === ""){
        confirm("注册信息不能为空!");
        return false;
    }
    return true;
}

