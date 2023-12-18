package iesmm.acdat.examen.ejercicio2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PokedexApp {
    public static void main(String[] args) {
        GestionarSAX parser = new GestionarSAX();

        File carpeta = new File("res");
        File originalFile = new File(carpeta, "pokedex.xml");
        File destinationFile = new File(carpeta, "pokedex.html");

        if (!destinationFile.exists()) {
            if (parser.abrir_XML_SAX(originalFile) == 0) {
                try {
                    FileWriter writer = new FileWriter(destinationFile);
                    writer.write(parser.obtenerHtml());
                    writer.close();
                } catch (IOException ex) {
                    System.err.println("Error al crear el fichero");
                } catch (Exception ex) {
                    System.err.println("Error: " + ex.getMessage());
                }
            }
        } else {
            System.err.println("El fichero HTML ya existe");
        }
    }
}
