package org.androidtown.dormitory.util;

/**
 * Created by AndroidApp on 2017-08-14.
 */

public class StringManipulation {

    public static String expandNIckname(String nickname ){
        return nickname.replace(".", " ");

    }
    public static String condenseNickname(String nickname){
        return nickname.replace(" ", ".");
    }
}
