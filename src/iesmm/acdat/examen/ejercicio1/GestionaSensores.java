package iesmm.acdat.examen.ejercicio1;

import com.google.gson.Gson;
import iesmm.acdat.examen.ejercicio1.model.Sensor;

import java.io.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class GestionaSensores {

    public static boolean generar(String fcsv) {
        boolean generado = false;

        // Se convierte la ruta un objeto archivo
        File csvFile = new File(fcsv);
        // se comprueba que existe y no es un directorio
        if (csvFile.exists() && !csvFile.isDirectory()) {
            // Se obtiene el archivo de desino con el mismo nombre
            File fileDir = csvFile.getParentFile();
            File destFile = new File(fileDir.getAbsoluteFile() + File.separator + csvFile.getName().replace("csv", "dat"));
            try {
                // Se crea un lector de fichero secuenciales
                FileReader fileReader = new FileReader(csvFile);
                BufferedReader reader = new BufferedReader(fileReader);

                // Se crea una lista de sensores donde guardaremos los sensores que vayamos leyendo
                List<Sensor> sensors = new LinkedList<>();

                // Se leen los sensores
                while (reader.ready()) {
                    Sensor sensor = parseSensorFromCSVLine(reader.readLine());

                    // Si el sensor se ha podido leer y no ha tirado error, no será nulo y se comprueba que la temperatura sea bajo cero
                    if (sensor != null && sensor.getTemperatura() < 0) {
                        sensors.add(sensor);
                    }
                }

                // Si se ha podido leer algún sensor, se ha podido generar y se escriben los sensores, si no, no se ha podido generar.
                if (sensors.size() > 0) {
                    escribeSensores(destFile, sensors);
                    generado = true;
                }

                // Se liberan los recursos
                reader.close();
                fileReader.close();
            } catch (IOException ex) {
                System.err.println("Ha ocurrido un error de lectura o escritura el el archivo " + fcsv);
                ex.printStackTrace();
            } catch (Exception ex) {
                System.err.println("Ha ocurrido un error desconocido leyendo el archivo " + fcsv + " error: " + ex.getMessage());
            }
        } else {
            System.err.println("El archivo " + fcsv + " no es un archivo valido.");
        }

        return generado;
    }

    /**
     * Metodo auxiliar para escribir sensores para mejorar la legibilidad del cógido
     * @param destFile
     * @param sensors
     */
    private static void escribeSensores(File destFile, List<Sensor> sensors) {
        try {
            // Se crea un escritor de objetos binario
            ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(destFile));

            // Se escribe la cantidad de sensores para mejorar la legibilidad.
            writer.writeInt(sensors.size());

            // Se escribe sensor por sensor
            for (Sensor sensor : sensors) {
                writer.writeObject(sensor);
            }

            // Se liberan recursos
            writer.close();
        } catch (IOException ex) {
            System.err.println("Error escribiendo sensores.");
        } catch (Exception ex) {
            System.err.println("Error desconocido escribiendo sensores: " + ex.getMessage());
        }
    }

    /**
     * Función auxiliar para mejorar la legibilidad del código que transforma un sensor de cadena de caracteres en CSV a un objeto Sensor
     * Esta controla los errores ocurridos en la lectura y devuelve nulo en caso de haber un error.
     * @param line La linea a convertir
     * @return La linea convertida en {@link Sensor sensor}
     */
    private static Sensor parseSensorFromCSVLine(String line) {
        // En caso de error se devolverá nulo.
        Sensor sensor = null;

        try {
            // Formato: TEMP: LAT, LONG
            // Se separa por ":" la linea
            String[] separadoPuntos = line.split(":");
            // Se comprueba el formato de la linea
            if (separadoPuntos.length == 2) {
                // se separa la localización y se obtienen los datos según el formato
                String[] localizacionSeparada = separadoPuntos[1].split(", ");
                int temperatura = Integer.parseInt(separadoPuntos[0]);
                float latitud = Float.parseFloat(localizacionSeparada[0]);
                float longitud = Float.parseFloat(localizacionSeparada[1]);

                // Se crea y se asigna el sensor a la variable
                sensor = new Sensor(temperatura, latitud, longitud);
            } else {
                System.err.println("La linea " + line + " del documento está mal formada por lo que se ignorará.");
            }
        } catch (NumberFormatException ex) {
            System.err.println("La linea " + line + " del documento está mal formada por lo que se ignorará.");
        } catch (Exception ex) {
            System.err.println("Hay un error desconocido leyendo la linea '" + line + "': " + ex.getMessage());
        }

        return sensor;
    }

    /**
     * Función auxiliar para evitar la repetición de código para obtener los sensores de un archivo .dat
     * @param file El archivo .dat de sensores
     * @return Una lista de sensores.
     */
    private static List<Sensor> obtenerSensores(File file) {
        // Crea una lista vacia de sensores, en caso de error se devuelve vacia.
        List<Sensor> sensors = new LinkedList<>();

        try {
            // Se crea un lector de archivo binario
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));

            // La primera inserción siempre es el contador de objetos
            int count = objectInputStream.readInt();

            // Se van leyendo los objetos y añadiendolos a la lista.
            while (count > 0) {
                Sensor sensor = (Sensor) objectInputStream.readObject();
                sensors.add(sensor);
                count--;
            }

            // Se liberan recursos
            objectInputStream.close();
        } catch (IOException e) {
            System.err.println("No se ha podido leer el archivo " + file.getName());
        } catch (Exception e) {
            System.err.println("Error leyendo " + file.getName() + " -> " + e.getMessage());
        }

        return sensors;
    }

    public static boolean ordenar(File f) {
        // Se crea una variable que por defecto es que no se ha generado, a no ser que se le indique lo contrario.
        boolean generado = false;

        // Se comprueba si existe el archivo y si no es un directorio
        if (f.exists() && !f.isDirectory()) {
            // Si es un archivo, se obtienen los sensores en la función obtenerSensores la cual nos da una Colección de sensores.
            // EL control de errores se hace allí por lo que devolverá un array vacio y colocará por pantalla los errores.
            List<Sensor> sensors = obtenerSensores(f);
            // Se comprueba si se han podido leer sensores
            if (sensors.size() > 0) {
                // Se ordenan usando los comparadores de colecciones, por defecto usando comparingInt se ordenan en orden creciente, pero en este caso, al necesitar
                // un orden decreciente, se le dará la vuelta con el metodo reversed.
                sensors.sort(Comparator.comparingInt(Sensor::getTemperatura).reversed());

                // Se obtiene el archivo de destino, en este caso estará en la carpeta "res".
                File destFile = new File("res", "ordenado.txt");
                try {
                    // Se crea un escritor de fichero secuencial
                    FileWriter fileWriter = new FileWriter(destFile);

                    // Se escribe la cantidad de medidas que se han podido tomar y se salta de linea.
                    fileWriter.write("Medidas tomadas: " + sensors.size() + "\n");

                    // Escribimos todos los objetos usando el toString de la clase.
                    for (Sensor sensor : sensors) {
                        fileWriter.write(sensor.toString() + "\n");
                    }

                    // Establecemos en la variable que se ha generado con exito.
                    generado = true;

                    // Liberamos los recursos
                    fileWriter.close();
                } catch (IOException ex) {
                    System.err.println("Hay un error escribiendo el archivo ordenado.txt");
                } catch (Exception ex) {
                    System.err.println("Error escribiendo ordenado.txt " + ex.getMessage());
                }
            } else {
                System.err.println("No se han podido encontrar sensores en el archivo .dat");
            }
        } else {
            System.err.println("El archivo " + f.getName() + " no existe o no se puede leer");
        }

        return generado;
    }

    public static void toJSON(File f) {
        // Se comprueba si existe el archivo y si no es un directorio
        if (f.exists() && !f.isDirectory()) {
            // Si es un archivo, se obtienen los sensores en la función obtenerSensores la cual nos da una Colección de sensores.
            // EL control de errores se hace allí por lo que devolverá un array vacio y colocará por pantalla los errores.
            List<Sensor> sensors = obtenerSensores(f);
            // Se comprueba si se han podido leer sensores
            if (sensors.size() > 0) {
                // Se ordenan usando los comparadores de colecciones, por defecto usando comparingInt se ordenan en orden creciente.
                sensors.sort(Comparator.comparingInt(Sensor::getTemperatura));

                // Se crea una instancia de GSON para convertir nuestros objetos a Json
                Gson gson = new Gson();

                // Se convierten todos los objetos sensor a json con GSON.
                System.out.println("Sensores en JSON: " + sensors.size());
                for (Sensor sensor : sensors) {
                    System.out.println(gson.toJson(sensor));
                }
            } else {
                System.err.println("No se han podido encontrar sensores en el archivo .dat");
            }
        } else {
            System.err.println("El archivo " + f.getName() + " no existe o no se puede leer");
        }
    }

}
