import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtGenerator {
    
    private static final String SECRET_KEY = "defaultSecretKey";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    
    public static String generateToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    
    public static void main(String[] args) {
        // Generate a test token
        String token = generateToken("1", "test@example.com", "USER");
        System.out.println("Generated JWT Token:");
        System.out.println(token);
        System.out.println("\nUse this token in your requests with:");
        System.out.println("Authorization: Bearer " + token);
    }
}
