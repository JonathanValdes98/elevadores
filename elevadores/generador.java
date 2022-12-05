import java.util.ArrayList;

public class generador {

    ArrayList<Object> viajes;

    int origen;
    int destino;
    int cantidad;
    int direccion;
    int estado;

    public generador() {
        this.viajes = new ArrayList<Object>();
    }

    public generador(int origen, int destino, int cantidad, int direccion, int estado) {
        this.origen = origen;
        this.destino = destino;
        this.cantidad = cantidad;
        this.direccion = direccion;
        this.estado = estado;
    }

    public synchronized void generarViaje(
            int origenPiso,
            int destinoPiso,
            int cantidadPersonas,
            int direccionAscensor,
            int estadoAscensor) {
        this.viajes.add(new generador(origenPiso, destinoPiso, cantidadPersonas, direccionAscensor, estadoAscensor));
    }

    public int getOrigen() {
        return this.origen;
    }

    public int getDestino() {
        return this.destino;
    }

    public int getCantidad() {
        return this.cantidad;
    }

}