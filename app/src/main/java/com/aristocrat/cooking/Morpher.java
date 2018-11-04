package com.aristocrat.cooking;

/**
 * Created by Nursultan on 21.09.2015.
 */
import android.content.Context;
import android.os.Environment;
import android.util.Log;


import javax.xml.parsers.*;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Morpher {

    private ArrayList<String> variants;
    private String name;

    public Morpher(String phrase) throws
            IOException,
            SAXException,
            ParserConfigurationException
    {
        name=phrase;
        String surl = "http://www.morpher.ru/WebService.asmx/GetXml?s=";

        String finalurl =surl + URLEncoder.encode(phrase, "UTF-8");
        if(finalurl.contains("+")){
            finalurl=finalurl.replace("+","%20");
        }
        Log.e("padezh",finalurl);

        URL url = new URL(finalurl);
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        String body = IOUtils.toString(in, encoding);
        System.out.println(body);

        variants=parseResult(body);
        for(String item : variants){
            Log.e("variants",item);
        }
    }

    public Ingredient getIngredient(){
        Ingredient ingredient=new Ingredient(name,variants);
        return ingredient;
    }


    /**
     * с DOM и SAX была какая то бабуйня с кодировкой и эксепшны без ссылок на мой код и без внятного объяснения.
     * пришлось парсить вручную. ногами не бить.
     * @param xml
     * @return
     */
    private ArrayList<String> parseResult(String xml){
        ArrayList<String> variants=new ArrayList<>();
        String[] nodes= {"<Р>","<Д>","<В>","<Т>","<П>"};
        String[] escapes= {"</Р>","</Д>","</В>","</Т>","</П>"};
        for(int z=0;z<nodes.length;z++){
            String tempString=xml;
            for (int i = 0; i < 2; i++) { //пробегаем два раза потому что есть и множественное число этих падежей
                int cutStartIndex=tempString.indexOf(nodes[z]);
                tempString=tempString.substring(cutStartIndex+3);
                int cutEndIndex=tempString.indexOf(escapes[z]);
                if(cutEndIndex!=-1) {
                    variants.add(tempString.substring(0, cutEndIndex));
                    tempString=tempString.substring(cutEndIndex);
                }
            }
        }
        return variants;
    }


    public static void writeToFile(String filename,String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void writeToFileExternal(String dirname,String filename,String data,Context context) {
        try {
            File sdCard= Environment.getExternalStorageDirectory();
            File dir=new File(sdCard.getAbsolutePath()+"/"+dirname);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file=new File(dir,filename);
            FileOutputStream f = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(f);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}