public class main {

    public static void main(String[] args) {

        generador gen = new generador();
        viaje viaje = new viaje(3, 8, 15, gen);

        hotel pisos = new hotel(15);
        elevador elevador1 = new elevador(pisos, 1, 15, gen, 10);

        elevador1.start();
        viaje.start();

    }

}
