package com.example.idene.whatsapp.helper;

import android.util.Base64;

//COMPLETED

public class Base64Custom {

    public static String encodeBase64(String text){
        //codificarBase64-encodeBase64
        return Base64.encodeToString(text.getBytes(),Base64.DEFAULT).replaceAll("\\n|\\r","");//replaceAll substitui caracteres invalidos-- replaceAll replaces invalid characters
    }

    public static String decodeBase64(String textCoded){
        //decodificarBase64--decodeBase64
        return  new String(Base64.decode(textCoded,Base64.DEFAULT));
    }

}
