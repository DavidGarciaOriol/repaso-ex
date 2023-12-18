package iesmm.acdat.examen.ejercicio1;

import java.io.File;
import java.util.Scanner;

public class SensoresApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Se pide el archivo por consola
        System.out.print("Por favor introducza la ruta de su archivo de sensores:");
        String filePath = scanner.nextLine();

        // Los errores se gestionan dentro del metodo por lo cual no se gestionan aqui
        if (GestionaSensores.generar(filePath)) {
            // Se obtiene el archivo de destino
            File csvFile = new File(filePath);
            File fileDir = csvFile.getParentFile();
            File destFile = new File(fileDir.getAbsoluteFile() + File.separator + csvFile.getName().replace("csv", "dat"));

            // Se prueba el metodo
            if (GestionaSensores.ordenar(destFile)) {
                System.out.println("Se ha podido ordenar el archivo " + filePath);
            } else {
                System.err.println("No se ha podido ordenar el archivo.");
            }

            // Se prueba el metodo
            System.out.println("El archivo en JSON es:");
            GestionaSensores.toJSON(destFile);
        } else {
            System.err.println("No se ha podido leer el archivo introducido.");
        }
    }

}
