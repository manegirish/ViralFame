package com.technoindians.views;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;

public class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {

public int findTokenStart(CharSequence text, int cursor) {
int i = cursor;

while (i > 0 && text.charAt(i - 1) != ' ') {
    i--;
}
while (i < cursor && text.charAt(i) == ' ') {
    i++;
}
    Log.e("findTokenEnd()","text -> "+text+" start -> "+i);
return i;
}

public int findTokenEnd(CharSequence text, int cursor) {
int i = cursor;
int len = text.length();

while (i < len) {
    if (text.charAt(i) == ' ') {
        return i;
    } else {
        i++;
    }
}
    Log.e("findTokenEnd()","text -> "+text+" len -> "+len);
return len;
}

public CharSequence terminateToken(CharSequence text) {
int i = text.length();

while (i > 0 && text.charAt(i - 1) == ' ') {
    i--;
}

if (i > 0 && text.charAt(i - 1) == ' ') {
    Log.e("terminateToken()1","text -> "+text);
    return text;
} else {
    if (text instanceof Spanned) {
        SpannableString sp = new SpannableString(text + " ");
        TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                Object.class, sp, 0);
        Log.e("terminateToken()2","text -> "+sp);
        return sp;
    } else {
        Log.e("terminateToken()3","text -> "+text);
        return text + " ";
    }
    }
    }
}

