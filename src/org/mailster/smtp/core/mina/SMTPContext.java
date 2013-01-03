package org.mailster.smtp.core.mina;

import java.io.InputStream;
import java.net.SocketAddress;

import org.apache.mina.core.session.IoSession;
import org.mailster.smtp.SMTPServerConfig;
import org.mailster.smtp.api.handler.AbstractDeliveryHandler;
import org.mailster.smtp.api.handler.DeliveryContext;
import org.mailster.smtp.auth.AuthenticationHandler;
import org.mailster.smtp.auth.Credential;
import org.mailster.smtp.core.DeliveryHandlerFactory;
import org.mailster.smtp.core.Session;

/**
 * The context of a SMTP session.
 * 
 * @author De Oliveira Edouard &lt;doe_wanted@yahoo.fr&gt;
 */
public class SMTPContext implements DeliveryContext
{
	private SMTPServerConfig cfg;
	
	private Session session;	
	private SocketAddress remoteAddress;
	private Credential credential;
	
	private InputStream inputStream;

	private AbstractDeliveryHandler deliveryHandler;
	private AuthenticationHandler authenticationHandler;

	public SMTPContext(SMTPServerConfig cfg, DeliveryHandlerFactory factory, 
			IoSession ioSession)
	{
		this.cfg = cfg;
		this.remoteAddress = ioSession.getRemoteAddress();
		this.session = new Session();
		
		this.deliveryHandler = factory.create(this);
		this.authenticationHandler = deliveryHandler.getAuthenticationHandler();
	}

	public AbstractDeliveryHandler getDeliveryHandler() 
	{
		return deliveryHandler;
	}

	public AuthenticationHandler getAuthenticationHandler() 
	{
		return authenticationHandler;
	}

	public InputStream getInputStream() 
	{
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) 
	{
		this.inputStream = inputStream;
	}

	public Session getSession()
	{
		return session;
	}

	public SocketAddress getRemoteAddress()
	{
		return remoteAddress;
	}

	public SMTPServerConfig getSMTPServerConfig()
	{
		return cfg;
	}

	public Credential getCredential() 
	{
		return credential;
	}

	public void setCredential(Credential credential) 
	{
		this.credential = credential;
	}
}
