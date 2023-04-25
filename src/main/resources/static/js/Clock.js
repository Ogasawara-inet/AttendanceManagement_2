/**
 * 
 */

// 要素を取得
var attClock = document.getElementById("clock");

// ページを開いたとき、繰り返し処理を開始
window.onload = function(){
	
	// 繰り返し処理の1回目
	clock();
	// その後は1秒ごとに処理を行う
	Passage = setInterval("clock()", 1000);	
	
	
	
	// ヘッダーの処理
	// （Header.jsのonloadが上書きされるためここに記述）
	header();
	
}



// ページを閉じたとき、繰り返し処理を終了
window.onunload = function(){
	clearInterval(Passage);
}



// 繰り返し処理
function clock(){
	
	// 現在時刻を取得
	var date = new Date();
	
	// 時間をテキストに
	var hh = set2fig(date.getHours());
	var mm = set2fig(date.getMinutes());
	var ss = set2fig(date.getSeconds());
	var msg = hh + ":" + mm + ":" + ss;
	
	attClock.innerHTML = msg;
}



// 2桁にフォーマットする
function set2fig(num){
	var num2
	if(num < 10) {
		num2 = "0" + num;
	} else {
		num2 = num;
	}
	return num2;
}


