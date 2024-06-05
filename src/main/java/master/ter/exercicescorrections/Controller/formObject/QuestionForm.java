package master.ter.exercicescorrections.Controller.formObject;

import lombok.Data;

import java.util.List;

@Data
public class QuestionForm {
    private String statement;
    private List<AnswerForm> answers;
}
