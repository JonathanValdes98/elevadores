public class viaje extends Thread {

	public int generados;
	public int cantidad;
	private int maxPersonas;
	public int maxPisos;
	public generador gen;

	public viaje(int cantidad, int maxPersonas, int maxPisos, generador gen) {
		this.cantidad = cantidad;
		this.maxPersonas = maxPersonas;
		this.maxPisos = maxPisos;
		this.gen = gen;
		this.generados = 1;
	}

	public void run() {
		try {
			while (getGenerados() <= getCantidad()) {
				sleep((long) (Math.random() * 9000 + 1000));
				int origen = (int) (Math.random() * (1 - (getMaxPisos() + 1)) + getMaxPisos() + 1);
				int destino = (int) (Math.random() * (1 - (getMaxPisos() + 1)) + getMaxPisos() + 1);
				int cantidad = (int) (Math.random() * (1 - getMaxPersonas()) + getMaxPersonas());
				while (origen == destino) {
					destino = (int) (Math.random() * 10);
				}
				int direccion;
				if (origen < destino) {
					direccion = 1;
				} else {
					direccion = -1;
				}
				gen.generarViaje(origen, destino, cantidad, direccion, 2);

				synchronized (gen) {
					gen.notify();
				}
				System.out.println(
						"Viaje de " + cantidad + " personas desde el piso " + origen + " hasta el piso " + destino);
				generados++;
			}
		} catch (

		InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getMaxPersonas() {
		return maxPersonas;
	}

	public int getMaxPisos() {
		return this.maxPisos;
	}

	private int getGenerados() {
		return this.generados;
	}

	private int getCantidad() {
		return this.cantidad;
	}

	public void pedirViaje() {

	}

}
