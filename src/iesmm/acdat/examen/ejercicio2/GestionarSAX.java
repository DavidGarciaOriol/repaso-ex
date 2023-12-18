package iesmm.acdat.examen.ejercicio2;

import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

public class GestionarSAX {

    private TransformadorPokedex handler;

    public String obtenerHtml() {
        return handler.getResult();
    }

    public int abrir_XML_SAX(File file) {
        try {
            SAXParserFactory factory = SAXParserFactory.newDefaultInstance();
            SAXParser parser = factory.newSAXParser();

            handler = new TransformadorPokedex();

            parser.parse(file, handler);
            return 0;
        } catch (SAXException e) {
            System.err.println(e.getMessage());
            return -1;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

}
