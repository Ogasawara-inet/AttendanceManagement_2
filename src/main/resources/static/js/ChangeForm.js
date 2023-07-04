/**
 * 
 */

// チェックボックス
var nameReverseOrder = document.querySelector("#nameReverseOrder");
var addMiddleName = document.querySelector("#addMiddleName");

// 表示順を逆にするフォーム
var nameForm = document.getElementById("nameForm");
var nameKanaForm = document.getElementById("nameKanaForm");

// 表示順を格納するinput
var nameReverseOrderInput = document.getElementById("nameReverseOrderInput");

// ミドルネームのフォーム
var middleNameLabel = document.getElementById("middleNameLabel");
var middleName = document.getElementById("middleName");
var middleNameKanaLabel = document.getElementById("middleNameKanaLabel");
var middleNameKana = document.getElementById("middleNameKana");



// 読み込み時の処理
window.onload = function(){
	
	// 姓名の順序を反映
	if(nameReverseOrderInput.value == "checked"){
		nameReverseOrder.checked = true;
	}
	
	// ミドルネームがあればミドルネームを表示
	if(middleName.value != null && middleName.value != ""){
		addMiddleName.checked = true;
	}
	
	// 変更を反映
	middleNameFunc();
	
}




function middleNameFunc(){
	
	// ミドルネームの入力欄を表示
	
	if(addMiddleName.checked){
		
		middleNameLabel.style.display = "block";
		middleName.style.display = "block";
		middleName.style.disabled = false;
		middleNameKanaLabel.style.display = "block";
		middleNameKana.style.display = "block";
		middleNameKana.style.disabled = false;
		
	}else{
		
		middleNameLabel.style.display = "none";
		middleName.style.display = "none";
		middleName.style.disabled = true;
		middleNameKanaLabel.style.display = "none";
		middleNameKana.style.display = "none";
		middleNameKana.style.disabled = true;
		
	}
	
	directionFunc();
	
}

function directionFunc(){
	
	// 姓名の順を逆にするかどうか
	// ミドルネームがあるときは縦並びにする
	
	if(nameReverseOrder.checked){
		if(addMiddleName.checked){
			nameForm.style.flexDirection = "column-reverse";
			nameKanaForm.style.flexDirection = "column-reverse";
		}else{
			nameForm.style.flexDirection = "row-reverse";
			nameKanaForm.style.flexDirection = "row-reverse";
		}
	}else{
		if(addMiddleName.checked){
			nameForm.style.flexDirection = "column";
			nameKanaForm.style.flexDirection = "column";
		}else{
			nameForm.style.flexDirection = "row";
			nameKanaForm.style.flexDirection = "row";
		}
	}
	
	
}

 