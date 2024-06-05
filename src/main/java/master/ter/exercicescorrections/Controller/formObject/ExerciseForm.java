package master.ter.exercicescorrections.Controller.formObject;

import lombok.Data;

@Data
public class ExerciseForm {
    private String title;
    private String category;
    private String tagsAsString;
    private String statement;
    private String answer;
}
