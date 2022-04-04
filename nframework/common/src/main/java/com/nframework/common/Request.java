package com.nframework.common;
public class Request implements java.io.Serializable
{
private String servicePath;
private Object[] arguments;
public void setServicePath(String servicePath)
{
this.servicePath=servicePath;
}
public String getServicePath()
{
return this.servicePath;
}
public void setArguments(Object ...arguments)  //variable no. of arguments can be paased implicitly array bn jaega
{
this.arguments=arguments;
}
public Object[] getArguments()
{
return this.arguments;
}
}
