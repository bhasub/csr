package com.emc.ideaforce.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Date;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
public class PasswordResetRequest {

    private static final int EXPIRATION = 1000 * 60 * 10;

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = EAGER)
    @JoinColumn(nullable = false, name = "email")
    private User user;

    private Date expiryDate;

    public PasswordResetRequest(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = new Date(new Date().getTime() + EXPIRATION);
    }

    public boolean isExpired() {
        return new Date().getTime() >= expiryDate.getTime();
    }
}
