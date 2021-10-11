    // 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
// 例子：   
// (new Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423   
// (new Date()).format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18   
Date.prototype.format = function(fmt) { // author: meizz
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

/**
 * 缩小times倍，再转换为元
 */
function decreaseAnd2Y(fen, times){
	
	fen = new Number(fen / times).toFixed(0);
	
	return changeF2Y(fen);
}

/**
 * 缩小10倍，再转换为元
 */
function decrease10And2Y(fen){
	
	return decreaseAnd2Y(fen, 10);
}

/**
 * 分转换成元
 */
function changeF2Y(fen){
     if (fen==="") return "-";
     if (parseFloat(fen)===0) return 0;;
     var y = '';
     if (parseFloat(fen)<0){
         var pre = "-";
         fen = Math.abs(fen);
     }else{
         var pre = "";
     }
     var fen = fen+"";
     if (!/^([+-]?)\d*\.?\d+$/.test(fen)) return "-";
     var length = fen.length;
     if(length>2){
         var beforePoint = fen.substring(0,length-2)
         afterPoint = fen.substring(length-2,length);
         y = beforePoint.concat(".").concat(afterPoint);
     }
     else if(length==2){
         y = "0.".concat(fen);    
     }
     else if(length==1){
          y = "0.0".concat(fen);
     }
     return pre+y;
 }

/**
 * AES加密，依赖aes.js
 * @param src
 * @param key
 */
function encryptAes(src, key){
	key = CryptoJS.enc.Utf8.parse(key);
	var encrypted = CryptoJS.AES.encrypt(src, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
	return encrypted.ciphertext.toString().toUpperCase();
}

/**
 * AES解密
 * @param word
 * @returns
 */
function decryptAes(word, key){
    var encryptedHexStr = CryptoJS.enc.Hex.parse(word);
    key = CryptoJS.enc.Utf8.parse(key);
    var srcs = CryptoJS.enc.Base64.stringify(encryptedHexStr);
    var decrypt = CryptoJS.AES.decrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
    var decryptedStr = decrypt.toString(CryptoJS.enc.Utf8); 
    return decryptedStr.toString();
}
/**
 * 打开文件下载菜单
 * @returns
 */
function openFileDonwloadPage(){
	//打开文件下载菜单
	parent.document.getElementById("open_file_download_btn").click();
}

/**
 * 设置按钮启用、禁用
 * @param btnClass 按钮的class属性
 * @param enabled true：启用，false：禁用
 * @returns
 */
function buttonSetting($, btnClass, enabled){
	if(enabled){
		$("." + btnClass).attr("disabled","disabled");
    	$("." + btnClass).attr("class", "layui-btn layui-btn-normal "+ btnClass +" layui-btn-disabled");
	}else{
		$("." + btnClass).removeAttr("disabled");
    	$("." + btnClass).attr("class", "layui-btn layui-btn-normal "+ btnClass +" ");
	}
}


/**
 * 设置按钮启用、禁用
 * @param btnClass 按钮的class属性
 * @param enabled true：启用，false：禁用
 * @returns
 */
function buttonSettingById($, btnId, enabled){
	if(enabled){
		$("#" + btnId).attr("disabled","disabled");
    	$("#" + btnId).attr("class", "layui-btn layui-btn-normal layui-btn-disabled");
	}else{
		$("#" + btnId).removeAttr("disabled");
    	$("#" + btnId).attr("class", "layui-btn layui-btn-normal");
	}
}