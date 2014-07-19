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
 * CSS brush.
 * @author Chan Wai Shing (cws1989@gmail.com)
 */
public class BrushCss extends Brush {

  public BrushCss() {
    String keywords = "ascent azimuth background-attachment background-color background-image background-position "
            + "background-repeat background baseline bbox border-collapse border-color border-spacing border-style border-top "
            + "border-right border-bottom border-left border-top-color border-right-color border-bottom-color border-left-color "
            + "border-top-style border-right-style border-bottom-style border-left-style border-top-width border-right-width "
            + "border-bottom-width border-left-width border-width border bottom cap-height caption-side centerline clear clip color "
            + "content counter-increment counter-reset cue-after cue-before cue cursor definition-src descent direction display "
            + "elevation empty-cells float font-size-adjust font-family font-size font-stretch font-style font-variant font-weight font "
            + "height left letter-spacing line-height list-style-image list-style-position list-style-type list-style margin-top "
            + "margin-right margin-bottom margin-left margin marker-offset marks mathline max-height max-width min-height min-width orphans "
            + "outline-color outline-style outline-width outline overflow padding-top padding-right padding-bottom padding-left padding page "
            + "page-break-after page-break-before page-break-inside pause pause-after pause-before pitch pitch-range play-during position "
            + "quotes right richness size slope src speak-header speak-numeral speak-punctuation speak speech-rate stemh stemv stress "
            + "table-layout text-align top text-decoration text-indent text-shadow text-transform unicode-bidi unicode-range units-per-em "
            + "vertical-align visibility voice-family volume white-space widows width widths word-spacing x-height z-index";
    String values = "above absolute all always aqua armenian attr aural auto avoid baseline behind below bidi-override black blink block blue bold bolder "
            + "both bottom braille capitalize caption center center-left center-right circle close-quote code collapse compact condensed "
            + "continuous counter counters crop cross crosshair cursive dashed decimal decimal-leading-zero default digits disc dotted double "
            + "embed embossed e-resize expanded extra-condensed extra-expanded fantasy far-left far-right fast faster fixed format fuchsia "
            + "gray green groove handheld hebrew help hidden hide high higher icon inline-table inline inset inside invert italic "
            + "justify landscape large larger left-side left leftwards level lighter lime line-through list-item local loud lower-alpha "
            + "lowercase lower-greek lower-latin lower-roman lower low ltr marker maroon medium message-box middle mix move narrower "
            + "navy ne-resize no-close-quote none no-open-quote no-repeat normal nowrap n-resize nw-resize oblique olive once open-quote outset "
            + "outside overline pointer portrait pre print projection purple red relative repeat repeat-x repeat-y rgb ridge right right-side "
            + "rightwards rtl run-in screen scroll semi-condensed semi-expanded separate se-resize show silent silver slower slow "
            + "small small-caps small-caption smaller soft solid speech spell-out square s-resize static status-bar sub super sw-resize "
            + "table-caption table-cell table-column table-column-group table-footer-group table-header-group table-row table-row-group teal "
            + "text-bottom text-top thick thin top transparent tty tv ultra-condensed ultra-expanded underline upper-alpha uppercase upper-latin "
            + "upper-roman url visible wait white wider w-resize x-fast x-high x-large x-loud x-low x-slow x-small x-soft xx-large xx-small yellow";
    String fonts = "[mM]onospace [tT]ahoma [vV]erdana [aA]rial [hH]elvetica [sS]ans-serif [sS]erif [cC]ourier mono sans serif";

    addRule(new RegExpressions(RegExpressions.multiLineCComments, "comments")); // multiline comments
    addRule(new RegExpressions(RegExpressions.doubleQuotedString, "string")); // double quoted strings
    addRule(new RegExpressions(RegExpressions.singleQuotedString, "string")); // single quoted strings
    addRule(new RegExpressions("\\#[a-fA-F0-9]{3,6}", "value")); // html colors
    addRule(new RegExpressions("(-?\\d+)(\\.\\d+)?(px|em|pt|\\:|\\%|)", "value")); // sizes
    addRule(new RegExpressions("!important", "color3")); // !important
    addRule(new RegExpressions(getKeywordsCSS(keywords), Pattern.MULTILINE, "keyword")); // keywords
    addRule(new RegExpressions(getValuesCSS(values), "value")); // values
    addRule(new RegExpressions(getKeywords(fonts), "color1")); // fonts
    addRule(new HTMLRegExRule("(?:&lt;|<)\\s*style.*?(?:&gt;|>)", "(?:&lt;|<)\\/\\s*style\\s*(?:&gt;|>)"));
    setCommonFileExtensionList("css");
  }

  protected static String getKeywordsCSS(String str) {
    return "\\b([a-z_]|)" + str.replaceAll("\\s", "(?=:)\\\\b|\\\\b([a-z_\\\\*]|\\\\*|)") + "(?=:)\\b";
  }

  protected static String getValuesCSS(String str) {
    return "\\b" + str.replaceAll("\\s", "(?!-)(?!:)\\\\b|\\\\b()") + ":\\b";
  }
}
