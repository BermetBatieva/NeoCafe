package com.example.backend.entity;
import com.example.backend.Enums.ERole;
import com.example.backend.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="first_name")
    private String firstName;

    @Column(name ="last_name")
    private String lastName;

    @Column(name ="b_date")
    private Date bDate;

    @Column(name ="phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private ERole role;

    private long bonus;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "is_completed")
    private boolean isCompleted;

    @OneToOne
    private Branches branches;

    @OneToOne
    private TimeTable time;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long rating;

    @Column(name = "firebase_token")
    private String firebaseToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }



    @Override
    public String getPassword() {
        return getActivationCode();
    }

    @Override
    public String getUsername() {
        return getPhoneNumber();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}