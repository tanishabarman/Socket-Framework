package com.nframework.common;
import com.google.gson.*;
public class JSONUtil
{
private JSONUtil(){}
public static String toJSON(java.io.Serializable serializable)
{
try
{
Gson gson=new Gson();
return gson.toJson(serializable);
}catch(Exception e)
{
return "{}";
}
}
//how the following mwthod will be used
//Buld b=JSONUtil.fromJSON(something,Bulb.class)
public static <T> T fromJSON(String jsonString,Class<T> c)
{
try
{
Gson gson=new Gson();
return gson.fromJson(jsonString,c);
}catch(Exception exception)
{
return null;
}
}
}
