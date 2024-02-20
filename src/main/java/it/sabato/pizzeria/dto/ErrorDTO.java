package it.sabato.pizzeria.dto;

import lombok.Data;

/**
 * The type Error dto.
 * @author Gianluca Sabato
 */
@Data
public class ErrorDTO {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
}
