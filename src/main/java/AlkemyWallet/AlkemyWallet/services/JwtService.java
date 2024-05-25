package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.security.config.JwtConfig;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@AllArgsConstructor
public class JwtService {

    private JwtConfig jwtConfig;

    public String createToken(Long userId, int minutesValid) {
        var algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        return JWT.create()
                .withIssuer(jwtConfig.getIssuer())
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + minutesValid * 60 * 1000))
                .sign(algorithm);
    }


    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        var algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        var verifier = JWT.require(algorithm)
                .withIssuer(jwtConfig.getIssuer())
                .build();
        return verifier.verify(token);
    }


}
