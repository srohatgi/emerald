package net.content;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yousendit.dpi.YouSendItDPI;
import com.yousendit.dpi.exceptions.DPIException;
import com.google.gson.Gson;

public class YsiAPI
{
  final static Log log = LogFactory.getLog(YsiAPI.class);
  final static String API_KEY = "6kvyvgmc9bcqku3tmk726u4p";
  final static String endpoint = YouSendItDPI.SandboxEndpoint;
  YouSendItDPI dpi;
  String authToken;
  
  private YsiAPI(String authToken)
  {
    dpi = new YouSendItDPI(endpoint,API_KEY,authToken);
    this.authToken = authToken;
  }
  
  public static YsiAPI instance(String authToken)
  {
    return new YsiAPI(authToken);
  }
  
  public static YsiAPI instance(String user, String passwd) throws Exception
  {
    YouSendItDPI dpi_temp = new YouSendItDPI(endpoint,API_KEY);
    String authToken = dpi_temp.login(user, passwd); 
    log.info("user="+user+" authToken="+authToken+" dpi_temp.AuthToken="+dpi_temp.getAuthToken());
    return new YsiAPI(authToken);
  }
  
  public String binFolder()
  {
    try
    {
      String rootfolders_json = remoteOperation("/dpi/v1/folder/0","GET",new HashMap<String,String>());
      Gson g = new Gson();
      YsiWorkspace w = g.fromJson(rootfolders_json, YsiWorkspace.class);
      for (YsiFolder f: w.folders.folder)
      {
        if ( f.name.equals("__bin__") )
        {
          return f.id;
        }
      }
      throw new RuntimeException("please setup __bin__ folder");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException("unable to get __bin__ folder");
    }
  }
  
  public String addFolder(String name, String parentId)
  {
    try
    {
      HashMap<String,String> params = new HashMap<String,String>();
      params.put("name", name);
      params.put("parentId", parentId);
      String result_json = remoteOperation("/dpi/v1/folder","POST",params);
      Gson g = new Gson();
      YsiWorkspace w = g.fromJson(result_json, YsiWorkspace.class);
      if ( w.errorStatus != null && w.errorStatus.code !=0 )
        throw new RuntimeException("Unable to create new folder: "+w.errorStatus.message);
      return w.id;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException("unable to add folder:"+name);
    }
  }

  private void doPost(OutputStream outputStream, HashMap<String, String> parameters)
  {
    if (parameters == null)
      return;

    try
    {
      DataOutputStream printout = new DataOutputStream(outputStream);
      int i = 0;
      StringBuffer content = new StringBuffer();
      for (String parameter : parameters.keySet())
      {
        if (i > 0)
          content.append("&");
        content.append(parameter).append("=").append(URLEncoder.encode(parameters.get(parameter), "UTF-8"));
        i++;
      }
      printout.writeBytes(content.toString());
      System.out.println("posting:"+content.toString());
      printout.flush();
      printout.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private static String addEncodedParameters(String restAPI, HashMap<String, String> parameters) throws Exception
  {
    if (parameters == null)
      return restAPI;

    boolean firstParameter = true;

    StringBuffer strbuf = new StringBuffer(restAPI);

    for (String parameter : parameters.keySet())
    {
      if (firstParameter)
      {
        strbuf.append("?");
        firstParameter = false;
      }
      else
        strbuf.append("&");

      strbuf.append(parameter).append("=").append(URLEncoder.encode(parameters.get(parameter), "UTF-8"));
    }
    System.out.println("appending:"+strbuf.toString());
    return strbuf.toString();
  }

  private String getText(InputStream in)
  {
    System.out.println("inside getText()");
    String response = null;
    try
    {
      BufferedReader responseReader = new BufferedReader(new InputStreamReader(in));
      StringWriter responseBuffer = new StringWriter();
      PrintWriter stdout = new PrintWriter(responseBuffer);
      for (String line; (line = responseReader.readLine()) != null;)
        stdout.println(line);
      responseReader.close();
      response = responseBuffer.toString();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.err.println("returning getText() response:"+response);
    }
    return response!=null?response.trim():null; // Remove trailing newline.
  }
  
  private String remoteOperation(String resource, String httpVerb, HashMap<String, String> parameters) throws DPIException
  {
    String response = null;
    HttpURLConnection conn = null;

    try
    {
      if (httpVerb.equals("GET"))
      {
        resource = addEncodedParameters(resource, parameters);
      }

      URL url = new URL(endpoint + resource);
      System.out.println(httpVerb+" to [" + url + "]");
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("X-Api-Key", API_KEY);
      log.info("authToken="+authToken);
      if (authToken != null)
        conn.setRequestProperty("X-Auth-Token", authToken);

      if (httpVerb.equals("GET"))
      { 
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");  
        conn.connect();
      }
      else if (httpVerb.equals("POST"))
      { 
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "application/json");
        doPost(conn.getOutputStream(), parameters);
      }
      else if (httpVerb.equals("DELETE"))
      { 
        conn.setDoOutput(true);
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        conn.connect();
      }

      response = getText(conn.getInputStream());
      System.out.println("response code:"+conn.getResponseCode()+" body:"+response);

      if (conn.getResponseCode()/100 != 2)
        throw new Exception("http response code:"+conn.getResponseCode()); // Throw exception which transfers control to the exception handler.
    }
    catch (Exception e)
    {
      try
      {
        if (conn == null)
          throw new DPIException(400, "Bad Request", e.getMessage());
        else
        {
          // GetErrorStream must be used to get the response when an HTTP response code that's not 200 is received.
          // Look at this Java bug for more detail: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4513568
          response = getText(conn.getErrorStream());
          throw new DPIException(conn.getResponseCode(), conn.getResponseMessage(), response);
        }
      }
      catch (Exception secondException)
      {
        throw new DPIException(400, "Bad Request", secondException.getMessage());
      }
    }

    return response;
  }

  public static void main(String[] args)
  {
    String json = "{\n" + 
    		"    \"name\": \"hello\",\n" + 
    		"    \"id\": null,\n" + 
    		"    \"type\": null,\n" + 
    		"    \"size\": null,\n" + 
    		"    \"revision\": null,\n" + 
    		"    \"parentid\": null,\n" + 
    		"    \"ownedByStorage\": null,\n" + 
    		"    \"currentUsage\": null,\n" + 
    		"    \"storageQuota\": null,\n" + 
    		"    \"createdOn\": null,\n" + 
    		"    \"folders\": null,\n" + 
    		"    \"files\": null,\n" + 
    		"    \"fileCount\": null,\n" + 
    		"    \"folderCount\": null,\n" + 
    		"    \"ownedByUser\": null,\n" + 
    		"    \"readable\": null,\n" + 
    		"    \"updatedOn\": null,\n" + 
    		"    \"writeable\": null,\n" + 
    		"    \"status\": null,\n" + 
    		"    \"errorStatus\": {\n" + 
    		"        \"message\": \"Not Found\",\n" + 
    		"        \"code\": 500\n" + 
    		"    }\n" + 
    		"  }";
    String json2 = "{\n" + 
    		"    \"name\": null,\n" + 
    		"    \"id\": null,\n" + 
    		"    \"type\": null,\n" + 
    		"    \"size\": null,\n" + 
    		"    \"revision\": \"1335711670660\",\n" + 
    		"    \"parentid\": null,\n" + 
    		"    \"ownedByStorage\": null,\n" + 
    		"    \"currentUsage\": \"15835932\",\n" + 
    		"    \"storageQuota\": \"2147483648\",\n" + 
    		"    \"createdOn\": null,\n" + 
    		"    \"folders\": {\n" + 
    		"        \"folder\": [\n" + 
    		"            {\n" + 
    		"                \"name\": \"pics\",\n" + 
    		"                \"id\": \"4739117\",\n" + 
    		"                \"type\": \"shared\",\n" + 
    		"                \"size\": \"0\",\n" + 
    		"                \"revision\": \"1324836432818\",\n" + 
    		"                \"parentid\": \"0\",\n" + 
    		"                \"ownedByStorage\": null,\n" + 
    		"                \"currentUsage\": null,\n" + 
    		"                \"storageQuota\": null,\n" + 
    		"                \"createdOn\": \"2011-10-28T22:03:18-07:00\",\n" + 
    		"                \"folders\": null,\n" + 
    		"                \"files\": null,\n" + 
    		"                \"fileCount\": null,\n" + 
    		"                \"folderCount\": null,\n" + 
    		"                \"ownedByUser\": \"120589182\",\n" + 
    		"                \"readable\": \"true\",\n" + 
    		"                \"updatedOn\": \"2011-12-25T10:07:12-08:00\",\n" + 
    		"                \"writeable\": \"true\",\n" + 
    		"                \"status\": null,\n" + 
    		"                \"errorStatus\": null\n" + 
    		"            },\n" + 
    		"            {\n" + 
    		"                \"name\": \"__bin__\",\n" + 
    		"                \"id\": \"12985790\",\n" + 
    		"                \"type\": \"private\",\n" + 
    		"                \"size\": \"482158\",\n" + 
    		"                \"revision\": \"1335299848398\",\n" + 
    		"                \"parentid\": \"0\",\n" + 
    		"                \"ownedByStorage\": null,\n" + 
    		"                \"currentUsage\": null,\n" + 
    		"                \"storageQuota\": null,\n" + 
    		"                \"createdOn\": \"2012-04-24T13:05:05-07:00\",\n" + 
    		"                \"folders\": null,\n" + 
    		"                \"files\": null,\n" + 
    		"                \"fileCount\": null,\n" + 
    		"                \"folderCount\": null,\n" + 
    		"                \"ownedByUser\": \"138390272\",\n" + 
    		"                \"readable\": \"true\",\n" + 
    		"                \"updatedOn\": \"2012-04-24T13:37:28-07:00\",\n" + 
    		"                \"writeable\": \"true\",\n" + 
    		"                \"status\": null,\n" + 
    		"                \"errorStatus\": null\n" + 
    		"            },\n" + 
    		"            {\n" + 
    		"                \"name\": \"Saved\",\n" + 
    		"                \"id\": \"13223907\",\n" + 
    		"                \"type\": \"private\",\n" + 
    		"                \"size\": \"0\",\n" + 
    		"                \"revision\": \"1335711670660\",\n" + 
    		"                \"parentid\": \"0\",\n" + 
    		"                \"ownedByStorage\": null,\n" + 
    		"                \"currentUsage\": null,\n" + 
    		"                \"storageQuota\": null,\n" + 
    		"                \"createdOn\": \"2012-04-29T08:01:10-07:00\",\n" + 
    		"                \"folders\": null,\n" + 
    		"                \"files\": null,\n" + 
    		"                \"fileCount\": null,\n" + 
    		"                \"folderCount\": null,\n" + 
    		"                \"ownedByUser\": \"138390272\",\n" + 
    		"                \"readable\": \"true\",\n" + 
    		"                \"updatedOn\": \"2012-04-29T08:01:10-07:00\",\n" + 
    		"                \"writeable\": \"true\",\n" + 
    		"                \"status\": null,\n" + 
    		"                \"errorStatus\": null\n" + 
    		"            }\n" + 
    		"        ]\n" + 
    		"    },\n" + 
    		"    \"files\": {\n" + 
    		"        \"file\": []\n" + 
    		"    },\n" + 
    		"    \"fileCount\": \"0\",\n" + 
    		"    \"folderCount\": \"3\",\n" + 
    		"    \"ownedByUser\": null,\n" + 
    		"    \"readable\": null,\n" + 
    		"    \"updatedOn\": null,\n" + 
    		"    \"writeable\": null,\n" + 
    		"    \"status\": null,\n" + 
    		"    \"errorStatus\": null\n" + 
    		"}";
    Gson gson = new Gson();
    YsiWorkspace w = gson.fromJson(json, YsiWorkspace.class);
    System.out.println("name:"+w.name);
    
    YsiWorkspace w2 = gson.fromJson(json2, YsiWorkspace.class);
    System.out.println("folder name:"+w2.folders.folder[0].name);
  }
}

/*
 * 
 * {
    "name": null,
    "id": null,
    "type": null,
    "size": null,
    "revision": null,
    "parentid": null,
    "ownedByStorage": null,
    "currentUsage": null,
    "storageQuota": null,
    "createdOn": null,
    "folders": null,
    "files": null,
    "fileCount": null,
    "folderCount": null,
    "ownedByUser": null,
    "readable": null,
    "updatedOn": null,
    "writeable": null,
    "status": null,
    "errorStatus": {
        "message": "Not Found",
        "code": 500
    }
 * }
 */
class YsiWorkspace 
{
  String name, id, type, size, revision, ownedByStorage, currentUsage, storageQuota,createdOn;
  String fileCount, folderCount, ownedByUser, readable, writeable, status;
  YsiFolders folders;
  YsiFiles files;
  YsiError errorStatus;
}

class YsiFolders
{
  YsiFolder[] folder;
  
}

class YsiFiles
{
  YsiFile[] file;
}

/*
 * {
                "name": "pics",
                "id": "4739117",
                "type": "shared",
                "size": "0",
                "revision": "1324836432818",
                "parentid": "0",
                "ownedByStorage": null,
                "currentUsage": null,
                "storageQuota": null,
                "createdOn": "2011-10-28T22:03:18-07:00",
                "folders": null,
                "files": null,
                "fileCount": null,
                "folderCount": null,
                "ownedByUser": "120589182",
                "readable": "true",
                "updatedOn": "2011-12-25T10:07:12-08:00",
                "writeable": "true",
                "status": null,
                "errorStatus": null
            }
 */
class YsiFolder
{
  String name, id, type, size, revision, parentid, ownedByStorage, currentUsage, storageQuota, createdOn;
  YsiFolders folders;
  YsiFiles files;
  int fileCount, folderCount;
  String ownedByUser, readable, updatedOn, writeable, status;
  YsiError errorStatus;
}

/*
 * {
                "name": "Photo 02-13-12-15-35-00.jpg",
                "id": "38013698",
                "size": "482158",
                "revision": "1",
                "fileRevisions": null,
                "fileid": null,
                "parentid": "12985790",
                "ownedByStorage": null,
                "createdOn": "2012-04-24T20:37:28",
                "downloadUrl": null,
                "clickableDownloadUrl": null,
                "status": null,
                "errorStatus": null
            }
 */
class YsiFile
{
  String name, id, size, revision, fileRevisions, fileid, parentid, ownedByStorage;
  String createdOn, downloadUrl, clickableDownloadUrl, status;
  YsiError errorStatus;
}

class YsiError
{
  String message;
  int code;
}