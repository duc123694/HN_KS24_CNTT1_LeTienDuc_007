package org.example.hackathon_de07.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String address;
}
