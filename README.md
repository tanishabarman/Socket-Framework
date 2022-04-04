# Socket-Framework
Requirements :
```
1> Gradle (6.7.1)
```
Smaple server side code :
```
// import jar file from server\build\libs\* and common\build\libs\*

import com.thinking.machines.nframework.server.*;
import com.thinking.machines.nframework.server.annotations.*;
@Path("/banking")
public class Bank
{
@Path("/branchName")
public String getBranchName(String city) throws BankingException
{
System.out.println("method got called");
if(city.equals("JABALPUR"))
{
return "Sihora";
}
if(city.equals("BHOPAL"))
{
return "Raisen";
}
throw new BankingException( "Dont know");
}
public static void main(String gg[])
{
NFrameworkServer server=new NFrameworkServer();
server.registerClass(Bank.class);
server.start();
}
}
```
Sample client side code : 
```
// import jar file from common\build\libs\* and client\build\libs\*

import com.thinking.machines.nframework.client.*;
class BankUI
{
public static void main(String gg[])
{
NFrameworkClient client=new NFrameworkClient();
String branchName="";
try
{
branchName=(String)client.execute("/banking/branchName",gg[0]);
System.out.println(branchName);
}catch(Throwable t)
{
System.out.println(t.getMessage());
}
}
}
```
