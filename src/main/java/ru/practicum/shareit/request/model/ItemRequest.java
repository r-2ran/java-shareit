package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    long id;
    @Column(name = "description")
    @NotBlank
    String description;
//    @ManyToOne
//    @JoinColumn(name = "requestor_id")
//    User requestor;
    //  неиспользуемый сейчас функционал, из-за него не запускаются тесты
    @Column(name = "created")
    LocalDateTime created;
}
