public class elevador extends Thread {
    private int PISO_ACTUAL;
    private int CAPACIDAD_MAXIMA;
    public int CONTADOR_VIAJES;
    private int CONTADOR_PASAJEROS;
    private int SENTIDO_ELEVADOR;
    public int MAX_VIAJES;
    public generador gen;
    public hotel pisos;

    public elevador(hotel pisos, int pisoIni, int capacidad, generador gen, int maxViajes) {
        this.gen = gen;
        this.PISO_ACTUAL = pisoIni;
        this.CAPACIDAD_MAXIMA = capacidad;
        this.pisos = pisos;
        this.CONTADOR_PASAJEROS = 0;
        this.MAX_VIAJES = maxViajes;
        this.SENTIDO_ELEVADOR = 0;
    }

    /*
     * ReferenciasDeEstados
     * // direccion
     * 0 = detenido
     * 1 = subiendo
     * -1 = bajando
     * // estado
     * 0 = completado
     * 1 = en curso
     * 2 = en espera
     */

    public void run() {
        pisos.getPisos();

        try {
            while (true) {
                if (getDireccion() == 0) {
                    Thread.sleep(600);
                    System.out.println("No hay viajes pendientes, el elevador se encuentra en 1");
                    for (int pisoActual = obtenerPisoActual(); pisoActual >= 0; pisoActual--) {
                        cambiarSentido();
                        if (getDireccion() != 0) {
                            break;
                        }
                        Thread.sleep(800);
                        avanza(pisoActual);

                    }
                } else if (getDireccion() == 1) {
                    Thread.sleep(600);
                    System.out.println("--------------------------");
                    System.out.println("El elevador va hacia arriba");
                    System.out.println("--------------------------");
                    for (int pisoActual = obtenerPisoActual(); pisoActual < this.pisos.getPisos(); pisoActual++) {
                        cambiarSentido();
                        if (getDireccion() != 1) {
                            break;
                        }

                        Thread.sleep(600);
                        avanza(pisoActual);
                        verGenerador();
                    }
                } else if (getDireccion() == -1) {
                    Thread.sleep(600);
                    System.out.println("--------------------------");
                    System.out.println("El elevador va hacia abajo");
                    System.out.println("--------------------------");
                    for (int pisoActual = obtenerPisoActual(); pisoActual >= 0; pisoActual--) {
                        cambiarSentido();
                        if (getDireccion() != -1) {
                            break;
                        }

                        Thread.sleep(600);
                        avanza(pisoActual);
                        verGenerador();
                    }
                }

            }
        } catch (

        Exception e) {

        }
    }

    public boolean viajesPendientes(int sentido) {
        boolean pendientes = false;
        switch (sentido) {
            case 1:
                for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual++) {
                    generador viaje = (generador) gen.viajes.get(viajeActual);
                    if ((viaje.destino >= obtenerPisoActual() && viaje.estado == 1)
                            || (viaje.origen >= obtenerPisoActual() && viaje.estado == 2)) {
                        pendientes = true;
                        break;
                    }
                }
                break;
            case -1:
                for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual--) {
                    System.out.println(gen.viajes.size());
                    generador viaje = (generador) gen.viajes.get(viajeActual);
                    if ((viaje.origen <= obtenerPisoActual() && viaje.estado == 2)
                            || (viaje.destino <= obtenerPisoActual() && viaje.estado == 1)) {
                        pendientes = true;
                        break;
                    }
                }
                break;
            default:
                break;
        }
        return pendientes;

    }

    public int getDireccion() {
        return this.SENTIDO_ELEVADOR;
    }

    public int obtenerPisoActual() {
        return this.PISO_ACTUAL;
    }

    public void cambiarSentido() {
        if (obtenerPisoActual() == 0 && viajesPendientes(1)) {
            setSentido(1);
        } else if (getDireccion() == 1 && !viajesPendientes(1)) {
            setSentido(-1);
        } else if (getDireccion() == -1 && !viajesPendientes(-1)) {
            if (viajesPendientes(1)) {
                setSentido(1);
            } else if (viajesPendientes(-1)) {
                setSentido(-1);
            }
        }
    }

    public void setSentido(int nuevoSentido) {
        this.SENTIDO_ELEVADOR = nuevoSentido;
    }

    public void avanza(int pisoMov) {
        setPisoActual(pisoMov);
        if (obtenerPisoActual() > 0) {
            System.out.println("El elevador esta en el piso:  " + getPisoActual());
        }

    }

    public void setPisoActual(int currentFloor) {
        this.PISO_ACTUAL = currentFloor;
    }

    public int getPisoActual() {
        return this.PISO_ACTUAL;
    }

    public void verGenerador() {
        for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual++) {
            generador viaje = (generador) gen.viajes.get(viajeActual);

            if (pisoDestino(viaje)) {
                setPasajeros(-viaje.cantidad);
                System.out.println(viaje.cantidad + " personas llegaron a su destino. Personas restantes: "
                        + getPasajeros() + " al nivel: " + getDireccion());
                viaje.estado = 0;

                // gen.viajes.remove(viajeActual); // no se si esto es necesario pero elimina el
                // viaje una vez terminado
            }
        }
        for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual++) {
            generador viaje = (generador) gen.viajes.get(viajeActual);

            if (pisoOrigen(viaje)) {

                if (validarEspacio(viaje) && (getDireccion() == viaje.direccion || getPasajeros() == 0)) {
                    if (viaje.estado == 2) {
                        setSentido(viaje.direccion);
                    }
                    setPasajeros(viaje.cantidad);
                    System.out.println(viaje.cantidad + " entraron al elevador. pasajeros: "
                            + getPasajeros() + " Direction: " + getDireccion());
                    viaje.estado = 1;
                }
            }

        }

    }

    public boolean pisoDestino(generador viaje) {
        if (getPisoActual() == viaje.destino && viaje.estado == 1) {// estado 1 = en curso
            return true;
        }
        return false;
    }

    public boolean validarEspacio(generador viaje) {
        if (getPasajeros() + viaje.cantidad <= this.CAPACIDAD_MAXIMA) {
            return true;
        }
        return false;
    }

    public int getPasajeros() {
        return this.CONTADOR_PASAJEROS;
    }

    public void setPasajeros(int pasajeros) {
        this.CONTADOR_PASAJEROS += pasajeros;
    }

    public boolean pisoOrigen(generador viaje) {
        if (obtenerPisoActual() == viaje.origen && viaje.estado == 2) {// estado 2 = pendiente
            return true;
        }
        return false;
    }

}
