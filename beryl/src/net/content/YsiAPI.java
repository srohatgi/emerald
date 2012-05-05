package net.content;

import com.yousendit.dpi.YouSendItDPI;

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
}
