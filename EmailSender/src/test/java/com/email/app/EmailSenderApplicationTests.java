package com.email.app;

import com.email.app.request.Messages;
import com.email.app.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.List;

@SpringBootTest
class EmailSenderApplicationTests {

	@Autowired
	private EmailService emailService;

	@Test
	void contextLoads() {
		System.out.println("Sending Email...");
		emailService.sendEmail("nehadeojuly2000@gmail.com", "Email from Spring Boot App", "This is the sample email..");
	}

	@Test
	void sendEmailWithHtml() {
		System.out.println("Sending Email...");
		String html = "" + "<h1 style=color:red; border: 1px solid red;> Welcome to Email Service with HTML Content.</h1>" + "";
		emailService.sendEmailWithHtml("nehadeojuly2000@gmail.com", "Email from Spring Boot App", html);
	}

	@Test
	void sendEmailWithFile() {
		System.out.println("Sending Email...");
		emailService.sendEmailWithFile("nehadeojuly2000@gmail.com",
				"Email from Spring Boot App",
				"Email with file.",
				new File("D:\\workspace\\EmailSender\\src\\main\\resources\\customFiles\\photo1.jpeg"));
	}

	@Test
	void sendEmailWithStream() {
		System.out.println("Sending Email...");
		File file = new File("D:\\workspace\\EmailSender\\src\\main\\resources\\customFiles\\photo1.jpeg");

        try {
			InputStream  is = new FileInputStream(file);
			emailService.sendEmailWithStream("nehadeojuly2000@gmail.com", "Input Stream", "Mail with Input Stream", is);

		} catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

	}

	@Test
	void getInbox() throws IOException {
		List<Messages> inboxMessages = emailService.getInboxMessages();
		inboxMessages.forEach(item->{
			System.out.println(item.getSubject());
			System.out.println(item.getContent());
			System.out.println(item.getFiles());
			System.out.println("-------------------------------------------------");
		});

	}

}
