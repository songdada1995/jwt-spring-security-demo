package org.zerhusen.security;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zerhusen.dao.primary.UserMapper;
import org.zerhusen.model.primary.User;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserModelDetailsService implements UserDetailsService {

   private final Logger log = LoggerFactory.getLogger(UserModelDetailsService.class);

   private final UserMapper userMapper;

   @Autowired
   public UserModelDetailsService(UserMapper userMapper) {
      this.userMapper = userMapper;
   }

   @Override
   @Transactional
   public UserDetails loadUserByUsername(final String login) {
      log.debug("Authenticating user '{}'", login);

      if (new EmailValidator().isValid(login, null)) {
         User user = userMapper.findOneWithAuthoritiesByEmailIgnoreCase(login);
         return createSpringSecurityUser(login, user);
      }

      String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
      User user = userMapper.findOneWithAuthoritiesByUsername(lowercaseLogin);
      return createSpringSecurityUser(lowercaseLogin, user);
   }

   private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
      if (!user.getEnabled()) {
         throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
      }
      List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
         .map(authority -> new SimpleGrantedAuthority(authority.getName()))
         .collect(Collectors.toList());
      return new org.springframework.security.core.userdetails.User(user.getUsername(),
         user.getPassword(),
         grantedAuthorities);
   }
}
