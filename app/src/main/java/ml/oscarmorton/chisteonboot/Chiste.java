package ml.oscarmorton.chisteonboot;

public class Chiste {

    private int id;
    private String texto;


    public Chiste() {
        this.id = 0;
        this.texto = "ERROR";
    }


    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
