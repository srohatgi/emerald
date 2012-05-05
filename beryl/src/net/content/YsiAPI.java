package net.content;

import java.util.Set;

import com.yousendit.dpi.YouSendItDPI;
import com.google.gson.Gson;

public class YsiAPI
{
  final static String API_KEY = "6kvyvgmc9bcqku3tmk726u4p";
  YouSendItDPI dpi;
  String authToken;
  
  private YsiAPI(String authToken)
  {
    dpi = new YouSendItDPI(YouSendItDPI.SandboxEndpoint,API_KEY,authToken);
    this.authToken = authToken;
  }
  
  public static YsiAPI instance(String authToken)
  {
    return new YsiAPI(authToken);
  }
  
  public static YsiAPI instance(String user, String passwd) throws Exception
  {
    YouSendItDPI dpi_temp = new YouSendItDPI(YouSendItDPI.SandboxEndpoint,API_KEY);
    return new YsiAPI(dpi_temp.login(user, passwd));
  }
  
  public Set<String> folders()
  {
    return null;
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