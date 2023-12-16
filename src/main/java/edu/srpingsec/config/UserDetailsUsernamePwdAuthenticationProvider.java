package edu.srpingsec.config;

import edu.srpingsec.dao.AuthorityEntity;
import edu.srpingsec.dao.UserEntity;
import edu.srpingsec.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class UserDetailsUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    //Authentication logic
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = authentication.getCredentials().toString();
        List<UserEntity> user = userRepository.findByEmail(userName);
        if(user.size() > 0){
            if(passwordEncoder.matches(password,user.get(0).getPassword())) {
                return new UsernamePasswordAuthenticationToken(userName,password,getGrantedAuthorities(user.get(0).getAuthorities()));
            } else{
                throw new BadCredentialsException("Invalid password!");
            }
        }else {
            throw new BadCredentialsException("No user registered with this details!");
        }
    }
    private List<GrantedAuthority> getGrantedAuthorities(Set<AuthorityEntity> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (AuthorityEntity authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication); //Type of the authentication
    }
}
