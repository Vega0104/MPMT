package tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBcrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String adminPwd = "Admin123!";
        String demoPwd = "Demo123!"; // utilisé pour Alice et Bob

        System.out.println("ADMIN (" + adminPwd + "): " + encoder.encode(adminPwd));
        System.out.println("ALICE (" + demoPwd + "): " + encoder.encode(demoPwd));
        System.out.println("BOB   (" + demoPwd + "): " + encoder.encode(demoPwd));
    }
}
