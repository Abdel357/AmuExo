package master.ter.exercicescorrections.Service;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailDomainValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailDomain {
    String message() default "Email doit se terminer par @etu.univ-amu.fr ou @univ-amu.fr";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
