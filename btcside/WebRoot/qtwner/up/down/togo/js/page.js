function sure(qr, url) {
	if (qr != null) {
		if (confirm(qr)) {
			window.location.href = url;
		}
	} else {
		window.location.href = url;
	}

}
function postform(url) {
	document.select.action = url;
	document.select.submit();
}

function partitionClick(partition) {
	var pathname = window.location.pathname;
	window.location.href = pathname + "?partition=" + partition;
}

function senfe(o,a,b,c,d){
 var t=document.getElementById(o).getElementsByTagName("tr");
 for(var i=0;i<t.length;i++){
  t[i].style.backgroundColor=(t[i].sectionRowIndex%2==0)?a:b;
  /*t[i].onclick=function(){
   if(this.x!="1"){
    this.x="1";
    this.style.backgroundColor=d;
   }else{
    this.x="0";
    this.style.backgroundColor=(this.sectionRowIndex%2==0)?a:b;
   }
  }*/
  t[i].onmouseover=function(){
   if(this.x!="1")this.style.backgroundColor=c;
  }
  t[i].onmouseout=function(){
   if(this.x!="1")this.style.backgroundColor=(this.sectionRowIndex%2==0)?a:b;
  }
 }
}