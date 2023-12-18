package iesmm.acdat.examen.ejercicio2;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;
import java.util.List;

public class TransformadorPokedex extends DefaultHandler {

    private String result;
    private String lastTag;

    private boolean saveStats;

    private String name;
    private List<String> habilidades;
    private List<String> score;
    private int xp;
    private String rateType;

    public TransformadorPokedex() {
        result = "";
        lastTag = "";
    }

    public String getResult() {
        return result;
    }

    @Override
    public void startDocument() throws SAXException {
        this.result += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
        this.result += "<html lang=\"es\">\n";
        this.result += "\t<head>\n\t\t<title>Pokedex</title>\n\t</head>\n";
        this.result += "\t<body>\n";
        this.result += "\t\t<table border=\"1\" style=\"table-layout: fixed;\">\n";
        // Cabezera de la tabla
        this.result += "\t\t\t<tr style=\"color: white; background-color: black;\">\n";
        this.result += "\t\t\t\t<th>Nombre</th>\n";
        this.result += "\t\t\t\t<th>Habilidades</th>\n";
        this.result += "\t\t\t\t<th>Score</th>\n";
        this.result += "\t\t\t\t<th>Experiencia</th>\n";
        this.result += "\t\t\t</tr>\n";
    }

    @Override
    public void endDocument() throws SAXException {
        this.result += "\t\t</table>\n";
        this.result += "\t</body>\n";
        this.result += "</html>";
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.lastTag = qName.toLowerCase();
        switch (lastTag) {
            // Se indica que todo lo que hay dentro de este objeto se deberá guardar
            case "basestats": {
                this.saveStats = true;
                break;
            }
            // Se limpian los datos de los pokemon almacenados para empezar un nuevo pokemon
            case "pokemon": {
                this.score = new LinkedList<>();
                this.habilidades = new LinkedList<>();
                this.name = "";
                this.xp = 0;
                this.rateType = "";
                this.saveStats = false;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName.toLowerCase()) {
            case "basestats": {
                this.saveStats = false;
                break;
            }
            // Cuando cierra se comprueba que tenga lo pedido y necesario y se escribe al resultado
            case "pokemon": {
                if (rateType.equalsIgnoreCase("MEDIUM SLOW") || rateType.equalsIgnoreCase("MEDIUM FAST") && xp >= 50 && habilidades.size() >= 2) {
                    this.result += "<tr>";

                    // Nombre:
                    this.result += "<td>" + name + "</td>";

                    // Habilidades:
                    this.result += "<td><ul>";
                    for (String habilidad : habilidades) {
                        this.result += "<li>" + habilidad + "</li>";
                    }
                    this.result += "</ul></td>";

                    // Score:
                    // Cabecera
                    this.result += "<td>\n";
                    this.result += "<table border=\"1\">\n";
                    this.result += "<tr>\n";
                    this.result += "<td>HP</td>\n";
                    this.result += "<td>ATK</td>\n";
                    this.result += "<td>DEF</td>\n";
                    this.result += "<td>SPD</td>\n";
                    this.result += "<td>SATK</td>\n";
                    this.result += "<td>SDEF</td>\n";
                    this.result += "</tr><tr>";

                    // Scores
                    for (String score : score) {
                        this.result += "<td>" + score + "</td>";
                    }
                    // Final
                    this.result += "</tr></table></td>";

                    // Experiencia:
                    this.result += "<td>" + xp + "</td>";

                    this.result += "</tr>";
                }
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String characters = new String(ch, start, length);
        if (!characters.trim().isEmpty()) {
            switch (lastTag) {
                case "species": {
                    this.name = characters;
                    break;
                }
                case "dream":
                case "ability": {
                    this.habilidades.add(characters);
                    break;
                }
                case "ratetype": {
                    this.rateType = characters;
                    break;
                }
                case "experience": {
                    this.xp = Integer.parseInt(characters);
                    break;
                }
                default: {
                    // Si se ha colocado la variable, se añaden todos los caracteres a score
                    if (saveStats) {
                        this.score.add(characters);
                    }
                }
            }
        }
    }
}
