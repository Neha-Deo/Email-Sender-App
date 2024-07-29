package com.email.app.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.email.app.request.Messages;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.email.app.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	@Value("${mail.store.protocol}")
	private String protocol;

	@Value("${mail.imaps.host}")
	private String host;

	@Value("${mail.imaps.port}")
	private String port;

	@Value("${spring.mail.username}")
	private String username;

	@Value("${spring.mail.password}")
	private String password;

	private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	private final JavaMailSender mailSender;

	@Autowired
	public EmailServiceImpl(JavaMailSender mailSender) {
		super();
		this.mailSender = mailSender;
	}

	@Override
	public void sendEmail(String to, String subject, String message) {

		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(to);
		simpleMailMessage.setFrom("neha.deo1207@gmail.com");
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(message);
		mailSender.send(simpleMailMessage);
		logger.info("Email sent successfully to Single user.");
	}

	@Override
	public void sendEmail(String[] to, String subject, String message) {

		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(to);
		simpleMailMessage.setFrom("neha.deo1207@gmail.com");
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(message);
		mailSender.send(simpleMailMessage);
		logger.info("Email sent successfully to Multiple user.");
	}

	@Override
	public void sendEmailWithHtml(String to, String subject, String htmlContent) {

		MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setFrom("neha.deo1207@gmail.com");
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(htmlContent, true);
			mailSender.send(mimeMessage);
			logger.info("Email sent successfully with HTML.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
	}

	@Override
	public void sendEmailWithFile(String to, String subject, String message, File file) {

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setFrom("neha.deo1207@gmail.com");
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(message);
			FileSystemResource fileSystemResource = new FileSystemResource(file);
			mimeMessageHelper.addAttachment(fileSystemResource.getFilename(), file);
			mailSender.send(mimeMessage);
			logger.info("Email sent successfully with Attachment.");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendEmailWithStream(String to, String subject, String message, InputStream inputStream) {

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setFrom("neha.deo1207@gmail.com");
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(message);

			File file = new File("src/main/resources/customFiles/test.png");
			Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			FileSystemResource fileSystemResource = new FileSystemResource(file);
			mimeMessageHelper.addAttachment(fileSystemResource.getFilename(), file);

			mailSender.send(mimeMessage);
			logger.info("Email sent successfully with Attachment using InputStream.");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

	@Override
	public List<Messages> getInboxMessages() throws IOException {

		Properties configs = new Properties();

		configs.setProperty("mail.store.protocol", protocol);
		configs.setProperty("mail.imaps.host", host);
		configs.setProperty("mail.imaps.port", port);

		Session session = Session.getDefaultInstance(configs);

        try {
			Store store = session.getStore();

			store.connect(username, password);

			Folder inbox = store.getFolder("INBOX");

			inbox.open(Folder.READ_ONLY);

			Message[] messages = inbox.getMessages();

			List<Messages> ls = new ArrayList<>();

			for(Message msg : messages) {
				String content = getContentFromEmailMessage(msg);
				List<String> attachments = getFilesFromEmailMessage(msg);
				ls.add(Messages.builder().subject(msg.getSubject()).content(content).files(attachments).build());
			}
			return ls;

        } catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		} catch (MessagingException e) {
            throw new RuntimeException(e);
        }
	}

	private String getContentFromEmailMessage(Message msg) throws MessagingException, IOException {
		if (msg.isMimeType("text/plain") || msg.isMimeType("text/html")) {
			return (String) msg.getContent();
		} else if (msg.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) msg.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/plain"))
					return (String) bodyPart.getContent();
			}
		}
        return null;
    }

	private List<String> getFilesFromEmailMessage(Message msg) throws MessagingException, IOException {
		List<String> files = new ArrayList<>();
		if (msg.isMimeType("multipart/*")) {
			Multipart content = (Multipart) msg.getContent();
			for (int i=0; i< content.getCount(); i++) {
				BodyPart bodyPart = content.getBodyPart(i);
				if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
					InputStream inputStream = bodyPart.getInputStream();
					File file = new File("src/main/resources/customFiles/"+bodyPart.getFileName());
					Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
					files.add(file.getAbsolutePath());
				}
			}
		}
        return files;
    }
}


















