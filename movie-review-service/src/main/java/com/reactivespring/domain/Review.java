package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {
    @Id
    private String reviewId;
    @NotNull(message = "review.movieInfoId cannot be null.")
    private Long movieInfoId;
    private String comment;
    @Min(value = 0, message = "Rating cannot be negative.")
    @Max(value = 5, message = "review.rating should be in the range 0 to 5. 0 being worst and 5 being best.")
    private Double rating;
}
