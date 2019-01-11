package com.artivisi.kampus.latihan.controller;

import com.artivisi.kampus.latihan.dao.UserDao;
import com.artivisi.kampus.latihan.entity.User;
import com.artivisi.kampus.latihan.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired private UserDao userDao;
    @Autowired private EmailService emailService;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/forgotPassword")
    public String showForgotPassword(){
        return "forgot_password";
    }

    @PostMapping("/forgotPassword")
    public String prosesForgotPassword(@RequestParam String username,
                                       RedirectAttributes redirectAttributes) throws MessagingException, UnsupportedEncodingException {

        User user = userDao.findByUsername(username);
        if(user != null){

            String newPwd = UUID.randomUUID().toString().substring(0,8);

            MimeMessage message = emailService.getJavaMailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(username);
            helper.setFrom(emailService.username,"Kampus Onlen");

            String content = "Password anda telah berhasil direset, ini adalah password baru anda <b>"+newPwd+"</b>";

            helper.setSubject("Reset Password");
            helper.setText(content, true);
            emailService.getJavaMailSender().send(message);

            user.getUserPassword().setPassword(passwordEncoder.encode(newPwd));
            userDao.save(user);

            redirectAttributes.addFlashAttribute("sukses", "Email reset password telah dikirim");
        }else{
            redirectAttributes.addFlashAttribute("error","User tidak ditemukan");
        }

        return "redirect:/login";
    }

}
