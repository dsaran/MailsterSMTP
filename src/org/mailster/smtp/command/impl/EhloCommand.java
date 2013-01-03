package org.mailster.smtp.command.impl;

import java.io.IOException;

import org.apache.mina.core.session.IoSession;
import org.mailster.smtp.SMTPServerConfig;
import org.mailster.smtp.auth.AuthenticationHandler;
import org.mailster.smtp.auth.impl.DummyAuthenticationHandler;
import org.mailster.smtp.command.AbstractBaseCommand;
import org.mailster.smtp.core.Session;
import org.mailster.smtp.core.mina.SMTPContext;

/**
 * The EHLO command implementation.
 * 
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 * @author De Oliveira Edouard &lt;doe_wanted@yahoo.fr&gt;
 */
public class EhloCommand extends AbstractBaseCommand
{
	public EhloCommand()
	{
		super("EHLO", "The EHLO command posts the client hostname info to the server.\n"+
					  "This extended version of the HELO command will return hints about\n"+
					  "the extended commands available on the local server"
				, "<hostname>\n hostname = your hostname");
	}
	
	public void execute(String commandString, IoSession ioSession, SMTPContext ctx) 
		throws IOException
	{
		String[] args = getArgs(commandString);
		if (args.length < 2)
		{
			sendResponse(ioSession, "501 Syntax: EHLO hostname");
			return;
		}

		//		postfix returns...
		//		250-server.host.name
		//		250-PIPELINING
		//		250-SIZE 10240000
		//		250-ETRN
		//		250 8BITMIME

		Session session = ctx.getSession();
		StringBuilder response = new StringBuilder();
		if (!session.getHasSeenHelo())
		{
			SMTPServerConfig cfg = ctx.getSMTPServerConfig();
			
			session.setHasSeenHelo(true);
			response.append("250-");
			response.append(cfg.getHostName());
			response.append("\r\n");
			response.append("250-8BITMIME\r\n");

			if (cfg.isTLSSupported() &&		
					getCommandHandler().containsCommand("STARTTLS"))
			{
				response.append("250-STARTTLS\r\n");
			}

			if (getCommandHandler().containsCommand(AuthCommand.VERB))
			{
				getEhloString(ctx.getAuthenticationHandler(), response);
			}
			
			response.append("250 Ok");
		}
		else
		{
			String remoteHost = args[1];
			response.append("503 ");
			response.append(remoteHost);
			response.append(" Duplicate EHLO");
		}
		sendResponse(ioSession, response.toString());
	}
	
	private void getEhloString(AuthenticationHandler handler, StringBuilder sb)
	{
		if (!(handler instanceof DummyAuthenticationHandler))
		{
            sb.append("250-").append(AuthCommand.VERB).append(' ');
            getTokenizedString(sb, handler.getAuthenticationMechanisms(), " ");
            sb.append("\r\n");
		}
	}
}