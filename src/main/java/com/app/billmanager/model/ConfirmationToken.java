package com.app.billmanager.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ConfirmationToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tokenId;

    private String confirmationToken;
    private LocalDate createdAt;

    @NonNull
    @ManyToOne
    private User user;

    @PrePersist
    public void create(){
        createdAt = LocalDate.now();
        confirmationToken = UUID.randomUUID().toString();
    }

}
