package com.example.cinema_back_end.security.controller;



import com.example.cinema_back_end.dtos.PinCodeDTO;
import com.example.cinema_back_end.entities.PinCode;
import com.example.cinema_back_end.entities.User;
import com.example.cinema_back_end.repositories.IPinCodeRepository;
import com.example.cinema_back_end.security.jwt.JwtResponse;
import com.example.cinema_back_end.security.jwt.JwtService;
import com.example.cinema_back_end.security.repo.IUserRepository;
import com.example.cinema_back_end.security.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

@CrossOrigin("*")
@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserService userService;
    @Autowired
    private IPinCodeRepository pinCodeRepository;
    @Autowired
    private IUserRepository userRepository;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<User> optionalUser = userService.findByUsername(user.getUsername());
            if (optionalUser.isPresent()) {
                User currentUser = optionalUser.get();
                if (currentUser.getActive()) {
                    String jwt = jwtService.generateTokenLogin(authentication);
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    return ResponseEntity.ok(new JwtResponse(jwt, currentUser.getId(), userDetails.getUsername(), currentUser.getFullName()));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tài khoản chưa được kích hoạt!");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sai email hoặc mật khẩu!");
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu.");
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if(userService.findByUsername(user.getUsername()).isPresent()){
                throw new Exception("Đã tồn tại người dùng, vui lòng chọn tên đăng nhập khác");
            }

            // Tạo mã PIN ngẫu nhiên
            String pinCode = generatePinCode();

            // Gửi mã PIN qua email
            sendPinCodeByEmail(user.getUsername(), pinCode);

            // Lưu mã PIN vào cơ sở dữ liệu
            PinCode newPinCode = new PinCode();
            newPinCode.setPin(pinCode);
            newPinCode.setContent("VertifyPin");
            newPinCode.setEmail(user.getUsername());
            newPinCode.setExpiredTime(LocalDateTime.now().plusMinutes(5));
            pinCodeRepository.save(newPinCode);

            String password = user.getPassword();
            user.setActive(false);
            userService.save(user);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(),password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtService.generateTokenLogin(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userService.findByUsername(user.getUsername()).get();
            return ResponseEntity.ok(new JwtResponse(jwt, currentUser.getId(), userDetails.getUsername(), currentUser.getFullName()));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    // Phương thức tạo mã PIN ngẫu nhiên
    private String generatePinCode() {
        Random random = new Random();
        int pin = 1000 + random.nextInt(9000); // Mã PIN gồm 4 chữ số
        return String.valueOf(pin);
    }

    // Phương thức gửi mã PIN qua email
    private void sendPinCodeByEmail(String emailAddress, String pinCode) throws MessagingException {
        // Cấu hình thông tin email
        String host = "smtp.gmail.com"; // Thay thế bằng SMTP server của bạn
        String username = "kctsocialnetwork@gmail.com"; // Thay thế bằng địa chỉ email của bạn
        String password = "btyckggpovbyperj"; // Thay thế bằng mật khẩu email của bạn

        // Tạo properties cho session email
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587"); // Cổng SMTP

        // Tạo session email
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Tạo message email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));
        message.setSubject("Xác thực tài khoản");
        message.setText("Mã PIN của bạn là: " + pinCode);

        // Gửi email
        Transport.send(message);

        System.out.println("Email gửi thành công!");
    }


    @PostMapping("/vertifyPin")
    public ResponseEntity<?> vertifyPin(@RequestBody PinCodeDTO request) {
        try {
            // Retrieve the PIN code from the request
            String pinCode = request.getPin();
            String username = request.getEmail();

            // Find the PIN code in the database
            Optional<PinCode> optionalPinCode = pinCodeRepository.findByEmailAndPinAndContent(username, pinCode, "VertifyPin");
            if (optionalPinCode.isPresent()) {
                PinCode pin = optionalPinCode.get();
                if (pin.getExpiredTime().isAfter(LocalDateTime.now())) {
                    Optional<User> optionalUser = userRepository.findByUsername(username);
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        user.setActive(true);
                        userRepository.save(user);
                        return ResponseEntity.ok("User activated successfully!");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found!");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PIN code has expired!");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid PIN code!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying PIN code: " + e.getMessage());
        }
    }

}
