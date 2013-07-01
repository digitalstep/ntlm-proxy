ntlm-proxy
==========
A very simple proxy server, written in Java.

I created this little proxy in order to get [Maven][] working behind an annoying corporate firewall.
The solutions [cntlm][] and [NTLM APS][] outlined at
http://docs.codehaus.org/display/MAVENUSER/Configuring+Maven+behind+an+NTLM+proxy
didn't work for me. My Active Directory account got deactivated a few times when followed that guide.
Thus, I wanted a solution which doesn't require me to enter a password.

[Maven]: http://maven.apache.org/
[cntlm]: http://cntlm.sourceforge.net/
[NTLM APS]: http://ntlmaps.sourceforge.net/

Download binary from the wiki page.

Usage: java -jar ntlm-proxy-0.1.0.jar 1234

the 1234 is the port the proxy server ist listening on. Just change it to your like.

For use with Maven, just add the following proxy configuration to your settings.xml:

	<settings>
	  <proxies>
	   <proxy>
	      <active>true</active>
	      <protocol>http</protocol>
	      <host>127.0.0.1</host>
	      <port>1234</port>
	    </proxy>
	  </proxies>
	</settings>

Again, change the port number as you like, but it must ,of course, match the port number, your local proxy is running on.

Based on code an information found in
* https://bugs.eclipse.org/bugs/show_bug.cgi?id=180921
* http://java-ntlm-proxy.sourceforge.net/

