package com.openclassrooms.paymybuddy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth)
    throws Exception {
    auth.jdbcAuthentication()
      .dataSource(dataSource)
//      The extracted user data must match a specfic table scheme with columns named
//      'username', 'password', and 'enabled', so it can be used with AuthenticationManagerBuilder.
//      That is why we use a specific SQL request
      .usersByUsernameQuery("select email as username,password,enabled "
        + "from users "
        + "where email = ?")
//      Same here, the authorities must match a specific table scheme with columns named
//      'username', 'authorities'.
      .authoritiesByUsernameQuery(
        "select email as username, name from users u "
        + "join user_authority ua on (ua.user_id=u.user_id) "
        + "join authorities a on (a.authority_id=ua.authority_id) where email = ?;"
      );
  }

  @Override
  public void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeRequests()
      .antMatchers("/admin*")
      .hasRole("ADMIN")
      .antMatchers("/principal")
      .hasRole("ADMIN")
      .antMatchers("/user*")
      .hasRole("USER")
      .anyRequest()
      .authenticated()
      .and()
      .formLogin();
  }
}
