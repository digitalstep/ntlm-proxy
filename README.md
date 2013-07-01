ntlm-proxy
==========
A very simple proxy server, written in Java.

I created this little proxy in order to get Maven working behind an annoying corporate firewall.
The usual suspects [cntlm][] and [ntlmaps][] didn't work for me. My Active Directory account got deactivated a few
times when trying to use these. I wanted a solution which doesn't require me to enter a password.

Download binary from the wiki page.

Usage: java -jar ntlm-proxy-0.1.0.jar 1234

the 1234 is the port the proxy server ist listening on. Just change it to your like.

Based on code an information found in
* https://bugs.eclipse.org/bugs/show_bug.cgi?id=180921
* http://java-ntlm-proxy.sourceforge.net/

[cntlm]: http://cntlm.sourceforge.net/
[ntlmaps]: http://ntlmaps.sourceforge.net/
