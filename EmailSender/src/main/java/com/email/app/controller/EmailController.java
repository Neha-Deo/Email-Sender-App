package com.email.app.controller;

import com.email.app.request.EmailRequest;
import com.email.app.response.CustomResponse;
import com.email.app.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/email")
public class EmailController {

    private EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest) {

        emailService.sendEmailWithHtml(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessage());
        return ResponseEntity.ok(
                CustomResponse.builder().message("Email Sent Successfully..!").httpStatus(HttpStatus.OK).success(true).build()
        );
    }

    @PostMapping("/sendEmailWithFile")
    public ResponseEntity<?> sendEmailWithFile(@RequestPart EmailRequest emailRequest, @RequestPart MultipartFile file) throws IOException {

        emailService.sendEmailWithStream(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessage(), file.getInputStream());
        return ResponseEntity.ok(
                CustomResponse.builder().message("Email Sent Successfully..!").httpStatus(HttpStatus.OK).success(true).build()
        );
    }
}
