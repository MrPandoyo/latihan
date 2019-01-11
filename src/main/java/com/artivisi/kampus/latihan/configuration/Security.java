package com.artivisi.kampus.latihan.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {

//    koneksi ke database
    @Autowired
    private DataSource dataSource;

//    password encoder (BCrypt)
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String SQL_ROLE
            = "select u.username as username, u.user_type as authority "
            + "from user u "
            + "where u.username = ?";

    private static final String SQL_LOGIN
            = "select u.username as ue,p.password as passwosernamrd, u.aktif as active "
            + "from user u "
            + "inner join user_password p on p.user_id = u.id "
            + "where username = ?";

//    global method untuk password encoder agar otomatis menggunakan BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

//    metode untuk menyimpan session
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

//    metode untuk mengecheck userdetail password menggunakan passwordencoder(bcrypt)
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        // kdbcdaoimpl = menampung hasil query
        JdbcDaoImpl userDetails = new JdbcDaoImpl();
        userDetails.setDataSource(dataSource);

        // usersByUsernameQuery (memerlukan 3 field, yaitu :
        // username, password, active) untuk menampung user
        // username, password & active
        userDetails.setUsersByUsernameQuery(SQL_LOGIN);

        // authoritiesByUsernameQuery (memerlukan 2 field, yaitu :
        // username, authority) untuk menampung role yang dimiliki user
        userDetails.setAuthoritiesByUsernameQuery(SQL_ROLE);
        return userDetails;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //disable cross site request forgery
        http.csrf().disable();

        http
                .authorizeRequests()
                .antMatchers("/bower_components/**").permitAll()
                .antMatchers("/dist/**").permitAll()
                .antMatchers("/plugins/**").permitAll()
                .antMatchers("/forgotPassword/**").permitAll()
                .antMatchers("/image/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
//                kalau true, maka redirect akan aktif
                .defaultSuccessUrl("/", false)
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }
}
