package master.ter.exercicescorrections.Controller.formObject;

import lombok.Data;
import master.ter.exercicescorrections.model.AcademicYear;
import master.ter.exercicescorrections.model.Domain;

@Data
public class UeForm {
    private String title;
    private String subPartsAsString;
    private String tagsAsString;
    private Domain domain;
    private AcademicYear year;
}
