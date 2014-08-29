package de.uniks.networkparser.gui.brush;

/*
java-syntax-highlighter
Copyright (c) 2011 Chan Wai Shing

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import java.util.regex.Pattern;

/**
 * AppleScript brush
 * @author Chan Wai Shing (cws1989@gmail.com)
 */
public class BrushAppleScript extends Brush {

  public BrushAppleScript() {
    super();

    // AppleScript brush by David Chambers
    // http://davidchambersdesign.com/

    String keywords = "after before beginning continue copy each end every from return get global in local named of set some that the then times to where whose with without";
    String ordinals = "first second third fourth fifth sixth seventh eighth ninth tenth last front back middle";
    String specials = "activate add alias AppleScript ask attachment boolean class constant delete duplicate empty exists false id integer list make message modal modified new no paragraph pi properties quit real record remove rest result reveal reverse run running save string true word yes";

    addRule(new RegExpressions("(--|#).*$", Pattern.MULTILINE, "comments"));
    addRule(new RegExpressions("\\(\\*(?:[\\s\\S]*?\\(\\*[\\s\\S]*?\\*\\))*[\\s\\S]*?\\*\\)", Pattern.MULTILINE, "comments")); // support nested comments
    addRule(new RegExpressions("\"[\\s\\S]*?\"", Pattern.MULTILINE, "string"));
    addRule(new RegExpressions("(?:,|:|¬|'s\\b|\\(|\\)|\\{|\\}|«|\\b\\w*»)", "color1"));
    addRule(new RegExpressions("(-)?(\\d)+(\\.(\\d)?)?(E\\+(\\d)+)?", "color1")); // numbers
    addRule(new RegExpressions("(?:&(amp;|gt;|lt;)?|=|� |>|<|≥|>=|≤|<=|\\*|\\+|-|\\/|÷|\\^)", "color2"));
    addRule(new RegExpressions("\\b(?:and|as|div|mod|not|or|return(?!\\s&)(ing)?|equals|(is(n't| not)?)?equal( to)?|does(n't| not) equal|(is(n't| not)?)?(greater|less) than( or equal( to)?)?|(comes|does(n't| not) come) (after|before)|is(n't| not)?( in)? (back|front) of|is(n't| not)? behind|is(n't| not)?( (in|contained by))?|does(n't| not) contain|contain(s)?|(start|begin|end)(s)? with|((but|end))?(consider|ignor)ing|prop(erty)?|(a)?ref(erence)?( to)?|repeat (until|while|with)|((end|exit))?repeat|((else|end))?if|else|(end)?(script|tell|try)|(on)?error|(put)?into|(of)?(it|me)|its|my|with (timeout( of)?|transaction)|end (timeout|transaction))\\b", "keyword"));
    addRule(new RegExpressions("\\b\\d+(st|nd|rd|th)\\b", "keyword")); // ordinals
    addRule(new RegExpressions("\\b(?:about|above|against|around|at|below|beneath|beside|between|by|(apart|aside) from|(instead|out) of|into|on(to)?|over|since|thr(ough|u)|under)\\b", "color3"));
    addRule(new RegExpressions("\\b(?:adding folder items to|after receiving|choose( ((remote)?application|color|folder|from list|URL))?|clipboard info|set the clipboard to|(the)?clipboard|entire contents|display(ing| (alert|dialog|mode))?|document( (edited|file|nib name))?|file( (name|type))?|(info)?for|giving up after|(name)?extension|quoted form|return(ed)?|second(?! item)(s)?|list (disks|folder)|text item(s| delimiters)?|(Unicode)?text|(disk)?item(s)?|((current|list))?view|((container|key))?window|with (data|icon( (caution|note|stop))?|parameter(s)?|prompt|properties|seed|title)|case|diacriticals|hyphens|numeric strings|punctuation|white space|folder creation|application(s( folder)?| (processes|scripts position|support))?|((desktop)?(pictures)?|(documents|downloads|favorites|home|keychain|library|movies|music|public|scripts|sites|system|users|utilities|workflows))folder|desktop|Folder Action scripts|font(s| panel)?|help|internet plugins|modem scripts|(system)?preferences|printer descriptions|scripting (additions|components)|shared (documents|libraries)|startup (disk|items)|temporary items|trash|on server|in AppleTalk zone|((as|long|short))?user name|user (ID|locale)|(with)?password|in (bundle( with identifier)?|directory)|(close|open for) access|read|write( permission)?|(g|s)et eof|using( delimiters)?|starting at|default (answer|button|color|country code|entr(y|ies)|identifiers|items|name|location|script editor)|hidden( answer)?|open(ed| (location|untitled))?|error (handling|reporting)|(do( shell)?|load|run|store) script|administrator privileges|altering line endings|get volume settings|(alert|boot|input|mount|output|set) volume|output muted|(fax|random)?number|round(ing)?|up|down|toward zero|to nearest|as taught in school|system (attribute|info)|((AppleScript( Studio)?|system))?version|(home)?directory|(IPv4|primary Ethernet) address|CPU (type|speed)|physical memory|time (stamp|to GMT)|replacing|ASCII (character|number)|localized string|from table|offset|summarize|beep|delay|say|(empty|multiple) selections allowed|(of|preferred) type|invisibles|showing( package contents)?|editable URL|(File|FTP|News|Media|Web) [Ss]ervers|Telnet hosts|Directory services|Remote applications|waiting until completion|saving( (in|to))?|path (for|to( (((current|frontmost))?application|resource))?)|POSIX (file|path)|(background|RGB) color|(OK|cancel) button name|cancel button|button(s)?|cubic ((centi)?met(re|er)s|yards|feet|inches)|square ((kilo)?met(re|er)s|miles|yards|feet)|(centi|kilo)?met(re|er)s|miles|yards|feet|inches|lit(re|er)s|gallons|quarts|(kilo)?grams|ounces|pounds|degrees (Celsius|Fahrenheit|Kelvin)|print( (dialog|settings))?|clos(e(able)?|ing)|(de)?miniaturized|miniaturizable|zoom(ed|able)|attribute run|action (method|property|title)|phone|email|((start|end)ing|home) page|((birth|creation|current|custom|modification))?date|((((phonetic)?(first|last|middle))|computer|host|maiden|related) |nick)?name|aim|icq|jabber|msn|yahoo|address(es)?|save addressbook|should enable action|city|country( code)?|formatte(r|d address)|(palette)?label|state|street|zip|AIM [Hh]andle(s)?|my card|select(ion| all)?|unsaved|(alpha)?value|entr(y|ies)|group|(ICQ|Jabber|MSN) handle|person|people|company|department|icon image|job title|note|organization|suffix|vcard|url|copies|collating|pages (across|down)|request print time|target( printer)?|((GUI Scripting|Script menu))?enabled|show Computer scripts|(de)?activated|awake from nib|became (key|main)|call method|of (class|object)|center|clicked toolbar item|closed|for document|exposed|(can)?hide|idle|keyboard (down|up)|event( (number|type))?|launch(ed)?|load (image|movie|nib|sound)|owner|log|mouse (down|dragged|entered|exited|moved|up)|move|column|localization|resource|script|register|drag (info|types)|resigned (active|key|main)|resiz(e(d)?|able)|right mouse (down|dragged|up)|scroll wheel|(at)?index|should (close|open( untitled)?|quit( after last window closed)?|zoom)|((proposed|screen))?bounds|show(n)?|behind|in front of|size (mode|to fit)|update(d| toolbar item)?|was (hidden|miniaturized)|will (become active|close|finish launching|hide|miniaturize|move|open|quit|(resign)?active|((maximum|minimum|proposed))?size|show|zoom)|bundle|data source|movie|pasteboard|sound|tool(bar| tip)|(color|open|save) panel|coordinate system|frontmost|main( (bundle|menu|window))?|((services|(excluded from)?windows))?menu|((executable|frameworks|resource|scripts|shared (frameworks|support)))?path|(selected item)?identifier|data|content(s| view)?|character(s)?|click count|(command|control|option|shift) key down|context|delta (x|y|z)|key( code)?|location|pressure|unmodified characters|types|(first)?responder|playing|(allowed|selectable) identifiers|allows customization|(auto saves)?configuration|visible|image( name)?|menu form representation|tag|user(-|)defaults|associated file name|(auto|needs) display|current field editor|floating|has (resize indicator|shadow)|hides when deactivated|level|minimized (image|title)|opaque|position|release when closed|sheet|title(d)?)\\b", "color3"));
    addRule(new RegExpressions(getKeywords(specials), Pattern.MULTILINE, "color3"));
    addRule(new RegExpressions(getKeywords(keywords), Pattern.MULTILINE, "keyword"));
    addRule(new RegExpressions(getKeywords(ordinals), Pattern.MULTILINE, "keyword"));

    setCommonFileExtensionList("applescript", "scpt");
  }
}
