package com.email.app.service;

import com.email.app.request.Messages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface EmailService {
	
	// send email to single person
	void sendEmail(String to, String subject, String message);
	
	// send email to multiple person
	void sendEmail(String[] to, String subject, String message);

	// send email with HTML
	void sendEmailWithHtml(String to, String subject, String htmlContent);
	
	// send email with file
	void sendEmailWithFile(String to, String subject, String message, File file);

	// send email with InputStream
	void sendEmailWithStream(String to, String subject, String message, InputStream inputStream);

	List<Messages> getInboxMessages() throws IOException;
}
