package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Validated
public class MovieInfo {
    private String movieInfoId;

    @NotBlank(message = "movieInfo.name must be present")
    private String name;

    @NotNull(message = "movieInfo.year must be present")
    @Positive(message = "movieInfo.year must be a positive value")
    private Integer year;

    @NotNull(message = "movieInfo.cast must be present")
    private List<@NotBlank(message = "movieInfo.cast must be present") String> cast;

    private LocalDate releaseDate;
}
