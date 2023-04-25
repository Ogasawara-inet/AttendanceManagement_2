/**
 * 
 */



// ページを読み込んだ時の処理
// Clock.jsでも呼び出すため処理内容は分ける
window.onload = function(){
	header();
}



// ヘッダーの処理
function header(){
	
	var adminHome = document.getElementById("adminHome");
	
	
	
	
//	
//	/*
//	 * 表示するメニューの設定
//	 */
//	
//	// 各メニューの取得
//	var empMenu = document.getElementById("empMenu");
//	var adminMenu = document.getElementById("adminMenu");
//	
//	// ホーム画面（index.html）ではどちらも非表示に
//	if(location.pathname == "/"){
//		empMenu.style.display = "none";
//		adminMenu.style.display = "none";
//		
//	// 管理者画面では管理者メニューを表示
//	} else if(location.pathname.indexOf("/admin") == 0) {
//		empMenu.style.display = "none";
//		adminMenu.style.display = "block";
//		
//	// 上記以外
//	} else {
//		
//		// 自身の登録情報変更ではアカウントの権限に従う
//		if(location.pathname == "/revice" 
//				&& document.getElementById("auth").value.indexOf("ADMIN") != -1){
//			empMenu.style.display = "none";
//			adminMenu.style.display = "block";
//		
//		// それ以外では従業員メニューを表示
//		} else {
//			empMenu.style.display = "block";
//			adminMenu.style.display = "none";
//			
//		}
//		
//	}
	
}


