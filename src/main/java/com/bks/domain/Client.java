package com.bks.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column (nullable = false)
	private String name;

	@Column (nullable = false, name = "DATE_OF_BIRTH")
	@JsonFormat(pattern = "dd.MM.yyyy")
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private LocalDate birthdate;

	@Column (nullable = false)
	@Size (min = 8, max = 500, message = "Длина пароля вне диапазона [8 - 500]")
	private String password;
}