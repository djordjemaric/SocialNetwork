package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

}
