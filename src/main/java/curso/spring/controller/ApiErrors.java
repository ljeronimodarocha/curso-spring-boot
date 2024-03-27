package curso.spring.controller;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ApiErrors {
    private List<String> errors;

    public ApiErrors(String error) {
        this.errors = Collections.singletonList(error);
    }
}
