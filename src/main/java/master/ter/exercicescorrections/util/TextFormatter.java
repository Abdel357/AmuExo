package master.ter.exercicescorrections.util;

public class TextFormatter {

    public static String formatText(String text) {
        // Remplacer les nouvelles lignes par des balises <br>
        text = text.replaceAll("\n", "<br>");

        // Ajouter d'autres transformations selon les besoins
        text = text.replaceAll("#### ", "<h4>").replaceAll(" ####", "</h4>");
        text = text.replaceAll("\\*\\*", "<strong>").replaceAll("\\*\\*", "</strong>");
        text = text.replaceAll(" - ", "<li>").replaceAll(";", "</li>");

        return text;
    }
}

