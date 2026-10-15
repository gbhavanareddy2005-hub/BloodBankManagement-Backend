package BloodBankManagement.controller;

import BloodBankManagement.entity.User;
import BloodBankManagement.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
    origins = "http://localhost:8080",
    allowCredentials = "true"
)
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {

        user.setRole("USER");

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        User savedUser = userRepository.save(user);

        // Do not send password hash to frontend
        savedUser.setPassword(null);

        return savedUser;
    }

    // =========================
    // GET ALL USERS
    // =========================

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .peek(user -> user.setPassword(null))
                .toList();
    }

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public User loginUser(
            @RequestBody User loginUser,
            HttpServletRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginUser.getEmail(),
                                loginUser.getPassword()
                        )
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);

        session.setAttribute(
                HttpSessionSecurityContextRepository
                        .SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        User user =
                userRepository
                        .findByEmail(loginUser.getEmail())
                        .orElseThrow();

        user.setPassword(null);

        return user;
    }
}