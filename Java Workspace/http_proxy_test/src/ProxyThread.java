import java.net.* ;
import java.io.* ;
import java.lang.* ;
import java.util.* ;

class ProxyThread extends Thread 
{
	Socket incoming, outgoing;
	String host;
	int hostport;

	ProxyThread(Socket in, Socket out, String host, int hostport)
	{
		incoming = in;
		outgoing = out;
		this.host = host;
		this.hostport = hostport;
	}

	// Overwritten run() method of thread,
	// does the data transfers
	public void run()
	{
		byte[] buffer = new byte[60];
		int numberRead = 0;
		OutputStream toClient;
		InputStream fromClient;
		
		try{
			BufferedReader inReader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			BufferedWriter outWriter = new BufferedWriter(new OutputStreamWriter(outgoing.getOutputStream()));
			
			while(true)
			{
				String line = inReader.readLine();
				
				// If line is null (blank), pass a blank line and continue
				if (line == null) {
					outWriter.write("\n");
					outWriter.flush();
					continue;
				}
				
				// Alter the line here if needed
				if (line.startsWith("Host: "))
					line = "Host: " + host + ":" + hostport;
				
				outWriter.write(line);
				outWriter.write("\n");
				outWriter.flush();
			}
		}
		catch(IOException e) 
		{
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
		}
	}
}