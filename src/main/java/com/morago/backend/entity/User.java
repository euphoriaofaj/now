package com.morago.backend.entity;

import com.morago.backend.config.jpa.BigDecimalScale2Converter;
import com.morago.backend.listener.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditable implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phoneNumber", length = 20, unique = true)
    @NotBlank
    private String username;

    @Column(name = "password")
    @NotBlank
    private String password;

    @Column(name = "first_name", length = 200)
    private String firstName;

    @Column(name = "last_name", length = 200)
    private String lastName;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    @Convert(converter = BigDecimalScale2Converter.class)
    @DecimalMin("0.00")
    @Digits(integer = 17, fraction = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "reserved", nullable = false, precision = 19, scale = 2)
    @Convert(converter = BigDecimalScale2Converter.class)
    @DecimalMin("0.00")
    @Digits(integer = 17, fraction = 2)
    private BigDecimal reserved = BigDecimal.ZERO;

    @Version
    private Long version;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "on_boarding_status")
    private Byte onBoardingStatus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
    )
    private Set<Role> roles= new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private TranslatorProfile translatorProfile;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
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
        return isActive;
    }

    public void setUserProfile(UserProfile p) {
        this.userProfile = p;
        if (p != null) p.setUser(this);
    }

    public void setTranslatorProfile(TranslatorProfile p) {
        this.translatorProfile = p;
        if (p != null) p.setUser(this);
    }

    public void setBalance(BigDecimal b) {
        this.balance = b == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : b.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAvailable() {
        return balance.subtract(reserved).setScale(2, RoundingMode.HALF_UP);
    }

    public void setReserved(BigDecimal r) {
        this.reserved = (r == null ? BigDecimal.ZERO : r).setScale(2, RoundingMode.HALF_UP);
    }

}
