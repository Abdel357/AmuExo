package master.ter.exercicescorrections.Controller.formObject;

import lombok.Data;
import master.ter.exercicescorrections.model.Question;

import java.util.List;

@Data
public class QuizzForm {
    private String title;
    private String category;
    private String tagsAsString;
    private List<Question> questions;
}