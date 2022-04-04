package com.nframework.server;
import java.net.*;
import java.nio.charset.*;
import com.nframework.common.*;
import java.lang.reflect.*;
import java.io.*;
class RequestProcessor extends Thread
{
private NFrameworkServer server;
private Socket socket;
RequestProcessor(NFrameworkServer server,Socket socket)
{
this.server=server;
this.socket=socket;
start();
}
public void run()
{
try
{
InputStream is=socket.getInputStream();
OutputStream os=socket.getOutputStream();
int bytesToReceive=1024;
byte tmp[]=new byte[1024];
byte header[]=new byte[1024];
int bytesReadCount;
int i,j,k;
j=0;
i=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
header[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
int requestLength=0;
j=1023;
i=1;
while(j>=0)
{
requestLength=requestLength+(header[j]*i);
i=i*10;
j--;
}
byte ack[]=new byte[1];
ack[0]=1;
os.write(ack,0,1);
os.flush();
byte request[]=new byte[requestLength];
bytesToReceive=requestLength;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
request[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
String requestJSONString=new String(request,StandardCharsets.UTF_8);
Request requestObject=JSONUtil.fromJSON(requestJSONString,Request.class);
//request object contains service path and arguments
//we want referance of tcp service that contains class ref and method ref
String servicePath=requestObject.getServicePath();
TCPService tcpService=this.server.getTCPService(servicePath);
Response responseObject=new Response();
if(tcpService==null)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("Invalid path : "+servicePath));
} 
else
{
Class c=tcpService.c;
Method method=tcpService.method;
try
{
Object serviceObject=c.newInstance();
Object result=method.invoke(serviceObject,requestObject.getArguments());   
responseObject.setSuccess(true);
responseObject.setResult(result);
responseObject.setException(null);
}catch(InstantiationException instantiationException)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("Unable to create object to service class associated with path :"+servicePath));
}
catch(IllegalAccessException illegalAccessException)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("Unable to create object to service class associated with path :"+servicePath));
}
catch(InvocationTargetException invocationTargetException)
{
Throwable t=invocationTargetException.getCause();
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(t);
}
}
String responseJSONString=JSONUtil.toJSON(responseObject);
byte objectBytes[]=responseJSONString.getBytes(StandardCharsets.UTF_8);
int responseLength=objectBytes.length;
int x;
i=1023;
x=responseLength;
header=new byte[1024];
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i--;
}
os.write(header,0,1024);  //from which index ,how many
os.flush();
System.out.println("Response header sent : "+responseLength);
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("Acknowledgement received");
int bytesToSend=responseLength;
j=0;
int chunkSize=1024;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j=j+chunkSize;
}
System.out.println("response sent");
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("ack received");
socket.close();
}catch(IOException e)
{
System.out.println(e);
}
}
}