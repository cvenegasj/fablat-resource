package lat.fab.app.resource.util;

import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl implements EmailService {

//	@Autowired
//    public JavaMailSender emailSender;

    public void sendHTMLMessage(String to, String subject, String text) {
//        try {
//        	MimeMessage message = emailSender.createMimeMessage();
//            // pass 'true' to the constructor to create a multipart message
//            MimeMessageHelper helper = new MimeMessageHelper(message, true); // boolean flag indicates html content type
//
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text, true);
//
//            emailSender.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
    }

}
