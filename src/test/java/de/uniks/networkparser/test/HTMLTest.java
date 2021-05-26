package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.gui.Style;
import de.uniks.networkparser.gui.controls.GUILine;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class HTMLTest {

	@Test
	public void testHTML2(){
//		HTMLContainer htmlContainer = new HTMLContainer();
//		HTMLEntity entity = htmlContainer.fromUrl("http://www.google.de");


		HTMLEntity html = new HTMLEntity();
		StringBuilder sb = new StringBuilder();
		sb.append("<html itemscope=\"\" itemtype=\"http://schema.org/WebPage\" lang=\"de\"><head><meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\"><meta content=\"/images/branding/googleg/1x/googleg_standard_color_128dp.png\" itemprop=\"image\"><title>Google</title><script>(function(){window.google={kEI:'piSdWaifOIXbwQKE8b3ACw',kEXPI:'1352960,1353383,1354276,1354297,1354401,1354442,1354514,1354625,1354664,1354749,1355158,3700296,3700347,3700407,3700442,4029815,4031109,4040137,4043492,4045841,4048347,4063220,4072775,4076999,4078430,4081039,4081165,4093313,4094544,4095910,4097153,4097922,4097929,4097955,4097971,4098733,4098740,4098752,4102109,4102239,4103475,4103845,4103861,4104037,4104258,4105241,4106084,4107555,4109489,4109610,4110427,4110656,4110670,4111275,4113148,4113217,4113275,4114597,4115290,4116724,4116731,4116926,4116935,4117980,4118103,4118226,4118627,4118798,4119239,4119272,4120285,4120415,4120915,4121035,4121079,4121696,4121806,4122184,4123477,4123640,4123830,4123850,4123983,4124090,4124109,4124340,4125372,4125477,4126001,4126137,4126506,4126650,4126708,4126767,4126890,4127301,4127316,4127321,4127329,4127358,4127377,4127402,4127728,4127752,4127876,4128062,4128294,10200083,10201957,10202457',authuser:0,kscs:'c9c918f0_24',u:'c9c918f0'};google.kHL='de';})();(function(){google.lc=[];google.li=0;google.getEI=function(a){for(var b;a&&(!a.getAttribute||!(b=a.getAttribute(\"eid\")));)a=a.parentNode;return b||google.kEI};google.getLEI=function(a){for(var b=null;a&&(!a.getAttribute||!(b=a.getAttribute(\"leid\")));)a=a.parentNode;return b};google.https=function(){return\"https:\"==window.location.protocol};google.ml=function(){return null};google.wl=function(a,b){try{google.ml(Error(a),!1,b)}catch(c){}};google.time=function(){return(new Date).getTime()};google.log=function(a,b,c,d,g){if(a=google.logUrl(a,b,c,d,g)){b=new Image;var e=google.lc,f=google.li;e[f]=b;b.onerror=b.onload=b.onabort=function(){delete e[f]};google.vel&&google.vel.lu&&google.vel.lu(a);b.src=a;google.li=f+1}};google.logUrl=function(a,b,c,d,g){var e=\"\",f=google.ls||\"\";c||-1!=b.search(\"&ei=\")||(e=\"&ei=\"+google.getEI(d),-1==b.search(\"&lei=\")&&(d=google.getLEI(d))&&(e+=\"&lei=\"+d));d=\"\";!c&&google.cached&&-1==b.search(\"&cached=\")&&(d=\"&cached=\"+google.cached);a=c||\"/\"+(g||\"gen_204\")+\"?atyp=i&ct=\"+a+\"&cad=\"+b+e+f+\"&zx=\"+google.time()+d;/^http:/i.test(a)&&google.https()&&(google.ml(Error(\"a\"),!1,{src:a,glmm:1}),a=\"\");return a};google.y={};google.x=function(a,b){if(a)var c=a.id;else{do c=Math.random();while(google.y[c])}google.y[c]=[a,b];return!1};google.lq=[];google.load=function(a,b,c){google.lq.push([[a],b,c])};google.loadAll=function(a,b){google.lq.push([a,b])};}).call(this);google.f={};var a=window.location,b=a.href.indexOf(\"#\");if(0<=b){var c=a.href.substring(b+1);/(^|&)q=/.test(c)&&-1==c.indexOf(\"#\")&&a.replace(\"/search?\"+c.replace(/(^|&)fp=[^&]*/g,\"\")+\"&cad=h\")};</script><style>#gbar,#guser{font-size:13px;padding-top:1px !important;}#gbar{height:22px}#guser{padding-bottom:7px !important;text-align:right}.gbh,.gbd{border-top:1px solid #c9d7f1;font-size:1px}.gbh{height:0;position:absolute;top:24px;width:100%}@media all{.gb1{height:22px;margin-right:.5em;vertical-align:top}#gbar{float:left}}a.gb1,a.gb4{text-decoration:underline !important}a.gb1,a.gb4{color:#00c !important}.gbi .gb4{color:#dd8e27 !important}.gbf .gb4{color:#900 !important}\r\n" +
				"</style><style>body,td,a,p,.h{font-family:arial,sans-serif}body{margin:0;overflow-y:scroll}#gog{padding:3px 8px 0}td{line-height:.8em}.gac_m td{line-height:17px}form{margin-bottom:20px}.h{color:#36c}.q{color:#00c}.ts td{padding:0}.ts{border-collapse:collapse}em{font-weight:bold;font-style:normal}.lst{height:25px;width:496px}.gsfi,.lst{font:18px arial,sans-serif}.gsfs{font:17px arial,sans-serif}.ds{display:inline-box;display:inline-block;margin:3px 0 4px;margin-left:4px}input{font-family:inherit}a.gb1,a.gb2,a.gb3,a.gb4{color:#11c !important}body{background:#fff;color:black}a{color:#11c;text-decoration:none}a:hover,a:active{text-decoration:underline}.fl a{color:#36c}a:visited{color:#551a8b}a.gb1,a.gb4{text-decoration:underline}a.gb3:hover{text-decoration:none}#ghead a.gb2:hover{color:#fff !important}.sblc{padding-top:5px}.sblc a{display:block;margin:2px 0;margin-left:13px;font-size:11px}.lsbb{background:#eee;border:solid 1px;border-color:#ccc #999 #999 #ccc;height:30px}.lsbb{display:block}.ftl,#fll a{display:inline-block;margin:0 12px}.lsb{background:url(/images/nav_logo229.png) 0 -261px repeat-x;border:none;color:#000;cursor:pointer;height:30px;margin:0;outline:0;font:15px arial,sans-serif;vertical-align:top}.lsb:active{background:#ccc}.lst:focus{outline:none}</style><script></script><link href=\"/images/branding/product/ico/googleg_lodp.ico\" rel=\"shortcut icon\"></head><body bgcolor=\"#fff\"><script>(function(){var src='/images/nav_logo229.png';var iesg=false;document.body.onload = function(){window.n && window.n();if (document.images){new Image().src=src;}\r\n" +
				"if (!iesg){document.f&&document.f.q.focus();document.gbqf&&document.gbqf.q.focus();}\r\n" +
				"}\r\n" +
				"})();</script><div id=\"mngb\"> <div id=gbar><nobr><b class=gb1>Suche</b> <a class=gb1 href=\"http://www.google.de/imghp?hl=de&tab=wi\">Bilder</a> <a class=gb1 href=\"http://maps.google.de/maps?hl=de&tab=wl\">Maps</a> <a class=gb1 href=\"https://play.google.com/?hl=de&tab=w8\">Play</a> <a class=gb1 href=\"http://www.youtube.com/?gl=DE&tab=w1\">YouTube</a> <a class=gb1 href=\"http://news.google.de/nwshp?hl=de&tab=wn\">News</a> <a class=gb1 href=\"https://mail.google.com/mail/?tab=wm\">Gmail</a> <a class=gb1 href=\"https://drive.google.com/?tab=wo\">Drive</a> <a class=gb1 style=\"text-decoration:none\" href=\"https://www.google.de/intl/de/options/\"><u>Mehr</u> &raquo;</a></nobr></div><div id=guser width=100%><nobr><span id=gbn class=gbi></span><span id=gbf class=gbf></span><span id=gbe></span><a href=\"http://www.google.de/history/optout?hl=de\" class=gb4>Webprotokoll</a> | <a  href=\"/preferences?hl=de\" class=gb4>Einstellungen</a> | <a target=_top id=gb_70 href=\"https://accounts.google.com/ServiceLogin?hl=de&passive=true&continue=http://www.google.de/\" class=gb4>Anmelden</a></nobr></div><div class=gbh style=left:0></div><div class=gbh style=right:0></div> </div><center><br clear=\"all\" id=\"lgpd\"><div id=\"lga\"><div style=\"padding:28px 0 3px\"><div style=\"height:110px;width:276px;background:url(/images/branding/googlelogo/1x/googlelogo_white_background_color_272x92dp.png) no-repeat\" title=\"Google\" align=\"left\" id=\"hplogo\" onload=\"window.lol&&lol()\"><div style=\"color:#777;font-size:16px;font-weight:bold;position:relative;top:70px;left:218px\" nowrap=\"\">Deutschland</div></div></div><br></div><form action=\"/search\" name=\"f\"><table cellpadding=\"0\" cellspacing=\"0\"><tr valign=\"top\"><td width=\"25%\">&nbsp;</td><td align=\"center\" nowrap=\"\"><input name=\"ie\" value=\"ISO-8859-1\" type=\"hidden\"><input value=\"de\" name=\"hl\" type=\"hidden\"><input name=\"source\" type=\"hidden\" value=\"hp\"><input name=\"biw\" type=\"hidden\"><input name=\"bih\" type=\"hidden\"><div class=\"ds\" style=\"height:32px;margin:4px 0\"><input style=\"color:#000;margin:0;padding:5px 8px 0 6px;vertical-align:top\" autocomplete=\"off\" class=\"lst\" value=\"\" title=\"Google-Suche\" maxlength=\"2048\" name=\"q\" size=\"57\"></div><br style=\"line-height:0\"><span class=\"ds\"><span class=\"lsbb\"><input class=\"lsb\" value=\"Google-Suche\" name=\"btnG\" type=\"submit\"></span></span><span class=\"ds\"><span class=\"lsbb\"><input class=\"lsb\" value=\"Auf gut Gl?ck!\" name=\"btnI\" onclick=\"if(this.form.q.value)this.checked=1; else top.location='/doodles/'\" type=\"submit\"></span></span></td><td class=\"fl sblc\" align=\"left\" nowrap=\"\" width=\"25%\"><a href=\"/advanced_search?hl=de&amp;authuser=0\">Erweiterte Suche</a><a href=\"/language_tools?hl=de&amp;authuser=0\">Sprachoptionen</a></td></tr></table><input id=\"gbv\" name=\"gbv\" type=\"hidden\" value=\"1\"></form><div id=\"gac_scont\"></div><div style=\"font-size:83%;min-height:3.5em\"><br></div><span id=\"footer\"><div style=\"font-size:10pt\"><div style=\"margin:19px auto;text-align:center\" id=\"fll\"><a href=\"/intl/de/ads/\">Werben mit Google</a><a href=\"/services/\">Unternehmensangebote</a><a href=\"https://plus.google.com/117570067846637741468\" rel=\"publisher\">+Google</a><a href=\"/intl/de/about.html\">?ber Google</a><a href=\"http://www.google.de/setprefdomain?prefdom=US&amp;sig=__01AmxAHfbT1c_WkBof2X4wjFlX8%3D\" id=\"fehl\">Google.com</a></div></div><p style=\"color:#767676;font-size:8pt\">&copy; 2017 - <a href=\"/intl/de/policies/privacy/\">Datenschutzerkl?rung</a> - <a href=\"/intl/de/policies/terms/\">Nutzungsbedingungen</a></p></span></center><script>(function(){window.google.cdo={height:0,width:0};(function(){var a=window.innerWidth,b=window.innerHeight;if(!a||!b){var c=window.document,d=\"CSS1Compat\"==c.compatMode?c.documentElement:c.body;a=d.clientWidth;b=d.clientHeight}a&&b&&(a!=google.cdo.width||b!=google.cdo.height)&&google.log(\"\",\"\",\"/client_204?&atyp=i&biw=\"+a+\"&bih=\"+b+\"&ei=\"+google.kEI);}).call(this);})();</script><div id=\"xjsd\"></div><div id=\"xjsi\"><script>(function(){function c(b){window.setTimeout(function(){var a=document.createElement(\"script\");a.src=b;document.getElementById(\"xjsd\").appendChild(a)},0)}google.dljp=function(b,a){google.xjsu=b;c(a)};google.dlj=c;}).call(this);(function(){window.google.xjsrm=[];})();if(google.y)google.y.first=[];if(!google.xjs){window._=window._||{};window._DumpException=window._._DumpException=function(e){throw e};if(google.timers&&google.timers.load.t){google.tick('load', {gen204: 'xjsls', clearcut: 31});}google.dljp('/xjs/_/js/k\\x3dxjs.hp.en_US.HWKnNOqE224.O/m\\x3dsb_he,d/am\\x3dABg/rt\\x3dj/d\\x3d1/t\\x3dzcms/rs\\x3dACT90oFd7WTU33BO8_uKNZdqfket_iKTeg','/xjs/_/js/k\\x3dxjs.hp.en_US.HWKnNOqE224.O/m\\x3dsb_he,d/am\\x3dABg/rt\\x3dj/d\\x3d1/t\\x3dzcms/rs\\x3dACT90oFd7WTU33BO8_uKNZdqfket_iKTeg');google.xjs=1;}google.pmc={\"sb_he\":{\"agen\":false,\"cgen\":false,\"client\":\"heirloom-hp\",\"dh\":true,\"dhqt\":true,\"ds\":\"\",\"fl\":true,\"host\":\"google.de\",\"isbh\":28,\"jam\":0,\"jsonp\":true,\"lm\":true,\"msgs\":{\"cibl\":\"Suche l?schen\",\"dym\":\"Meintest du:\",\"lcky\":\"Auf gut Gl?ck!\",\"lml\":\"Weitere Informationen\",\"oskt\":\"Eingabetools\",\"psrc\":\"Diese Suchanfrage wurde aus deinem \\u003Ca href=\\\"/history\\\"\\u003EWebprotokoll\\u003C/a\\u003E entfernt.\",\"psrl\":\"Entfernen\",\"sbit\":\"Bildersuche\",\"srch\":\"Google-Suche\"},\"nds\":true,\"ovr\":{},\"pq\":\"\",\"refpd\":true,\"rfs\":[],\"sbpl\":24,\"sbpr\":24,\"scd\":10,\"sce\":5,\"stok\":\"PPRWhKcCN6HxiE7obFLVCjgN__8\"},\"d\":{},\"aWiv7g\":{},\"YFCs/g\":{}};google.y.first.push(function(){if(google.med){google.med('init');google.initHistory();google.med('history');}});if(google.j&&google.j.en&&google.j.xi){window.setTimeout(google.j.xi,0);}\r\n" +
				"</script></div></body></html>");


		html.withValue("<!doctype html>"+sb.toString());
//		Assert.assertEquals(sb.toString(), html.toString());

//		HTMLEntity answer = SocketUtil.getHTTP("http://www.google.de");
	}
	@Test
	public void testHTML(){
		String txt = "Stefan <Test>";
		String encode = EntityUtil.encode(txt);
		Assert.assertEquals("Stefan &lt;Test&gt;", encode);

		Assert.assertEquals(txt, EntityUtil.decode(encode));
	}
	@Test
	public void testSimpleHTMLFile(){
		HTMLEntity file=new HTMLEntity();
		file.withText("Hallo Welt");
		Style style = new Style().withBorder(GUIPosition.ALL, new GUILine().withColor("black").withWidth("1px"));
		style.toString();
		file.addStyle("Table", ".Table{border:1px solid black}");
		file.addStyle("Table", ".div{border:1px solid black}");
		file.withNewLine();
		file.withText("Second Line");
		Assert.assertNotNull(file.toString());
	}

	@Test
	public void testSimpleJSoup(){
		StringBuilder sb=new StringBuilder("<div id=\"mp-itn\">");
		sb.append("<ul>");
		sb.append("<li>");
		sb.append("<b>");
		sb.append("<a href=\"/wiki/2016_Kaikoura_earthquake\" title=\"2016 Kaikoura earthquake\">An earthquake</a>");
		sb.append("</b>");
		sb.append("</li>");
		sb.append("<li>Canadian singer, songwriter, and poet <b><a href=\"/wiki/Leonard_Cohen\" title=\"Leonard Cohen\">Leonard Cohen</a></b> <i>(pictured)</i> dies at the age of 82.</li>");
		sb.append("</ul>");
		sb.append("</div>");
		HTMLEntity entity = new HTMLEntity().with(sb.toString());
		XMLEntity list = entity.getElementsBy(EntityUtil.CLASS, "#mp-itn b a");
		Assert.assertEquals(2, list.sizeChildren());
		for(int i=0;i<list.sizeChildren();i++) {
			BaseItem child = list.getChild(i);
			if(i==0) {
				Assert.assertEquals("<a href=\"/wiki/2016_Kaikoura_earthquake\" title=\"2016 Kaikoura earthquake\">An earthquake</a>", child.toString());
			} else {
				Assert.assertEquals("<a href=\"/wiki/Leonard_Cohen\" title=\"Leonard Cohen\">Leonard Cohen</a>", child.toString());
			}
		}
	}

}
