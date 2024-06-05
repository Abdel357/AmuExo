package master.ter.exercicescorrections.Service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailDomainValidator implements ConstraintValidator<EmailDomain, String> {

    private static final String[] ALLOWED_DOMAINS = {"@etu.univ-amu.fr", "@univ-amu.fr"};

    @Override
    public void initialize(EmailDomain emailDomain) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return false; // ou true selon vos besoins
        }
        for (String domain : ALLOWED_DOMAINS) {
            if (email.endsWith(domain)) {
                return true;
            }
        }
        return false;
    }
}
