public class main {

    public static void main(String[] args) {
        /*
         * se debe validar que el numero de viajes generados sea igual al maximo de viajes del elevador para que finalice el programa
         * si se generan errores reiniciar el vs code funciona
         */
        generador gen = new generador(); // clase que almacena los viajes
        viaje viaje = new viaje(10, 8, 15, gen);// hilo que genera los datos de cada viaje

        hotel pisos = new hotel(15);
        elevador el = new elevador(pisos, gen, 1, 15, 10);

        el.start();
        viaje.start();

    }

}
