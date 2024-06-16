/*
 * Created 2018-6-25 15:31:20
 */
package cn.com.yting.kxy.web.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Azige
 */
@Entity
@Data
@Table(name = "account")
public class Account implements Serializable, UserDetails {

    public static final Collection<GrantedAuthority> AUTHORITIES = Collections.singleton(new SimpleGrantedAuthority("USER"));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "display_name", nullable = false)
    private String displayName;
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "locked", nullable = false)
    private boolean locked;
    @Column(name = "white_listed", nullable = false)
    private boolean whiteListed;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AccountPasscode> passcodes = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AUTHORITIES;
    }

    @Override
    public String getPassword() {
        return passcodes.stream()
                .filter(it -> it.getPasscodeType().equals(AccountPasscodeType.PASSWORD))
                .findAny()
                .map(AccountPasscode::getPasscode)
                .orElse("No password");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public AccountInfo toAccountInfo() {
        return new AccountInfo(
                getId(),
                username,
                displayName,
                getPasscodes().stream()
                        .map(AccountPasscode::getPasscodeType)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return "{ \"exception\": \"error convert to json\" }";
        }
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }
}
