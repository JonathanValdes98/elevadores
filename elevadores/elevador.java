public class elevador extends Thread {
    private int PISO_ACTUAL;
    private int CAPACIDAD_MAXIMA;
    public int CONTADOR_VIAJES;
    private int CONTADOR_PASAJEROS;
    private int SENTIDO_ELEVADOR;
    public int MAX_VIAJES;
    public generador gen;
    public hotel pisos;

    public elevador(hotel pisos, generador gen, int pisoIni, int capacidad, int maxViajes) {
        this.gen = gen;
        this.PISO_ACTUAL = pisoIni;
        this.CONTADOR_VIAJES = 0;
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

    @Override
    public void run() {
        try {
            while (CONTADOR_VIAJES < MAX_VIAJES) {
                if (getSentido() == 0) {
                    if(getPisoActual() == 0 && !viajesPendientes()){
                        System.out.println("No hay viajes pendientes, el elevador se encuentra en el piso 1");
                        synchronized (gen) {
                            gen.wait();
                        }
                    }
                    for (int pisoActual = getPisoActual(); pisoActual >= 0; pisoActual--) {
                        cambiarSentido();
                        if (getSentido() != 0) {
                            break;
                        }
                        sleep(300);
                        avanza(pisoActual);
                    }
                } else if (getSentido() == 1) {
                    for (int pisoActual = getPisoActual(); pisoActual < this.pisos.getPisos(); pisoActual++) {
                        cambiarSentido();
                        if (getSentido() != 1) {
                            break;
                        }
                        sleep(300);
                        avanza(pisoActual);
                        verGenerador();
                    }
                } else if (getSentido() == -1) {
                    for (int pisoActual = getPisoActual(); pisoActual >= 0; pisoActual--) {
                        cambiarSentido();
                        if (getSentido() != -1) {
                            break;
                        }
                        sleep(300);
                        avanza(pisoActual);
                        verGenerador();
                    }
                }

            }
            System.out.println("El elevador ha completado su trabajo de "+this.MAX_VIAJES+" viajes");
        } catch (InterruptedException e) {
            System.out.println("Error en el hilo del elevador");
            System.out.println(e);
        }
    }

    public int getSentido() {
        return this.SENTIDO_ELEVADOR;
    }
    public void setSentido(int nuevoSentido) {
        this.SENTIDO_ELEVADOR = nuevoSentido;
    }

    public int getPisoActual() {
        return this.PISO_ACTUAL;
    }

    public void setPisoActual(int currentFloor) {
        this.PISO_ACTUAL = currentFloor;
    }


    public void setViajes() {
        this.CONTADOR_VIAJES+= 1;
    }

    public int getPasajeros() {
        return this.CONTADOR_PASAJEROS;
    }

    public void setPasajeros(int pasajeros) {
        this.CONTADOR_PASAJEROS += pasajeros;
    }

    public void avanza(int pisoMov) {
        setPisoActual(pisoMov);
        if (getPisoActual() > 0) {
            System.out.println("El elevador esta en el piso:  " + getPisoActual());
        }

    }

    public boolean validarEspacio(generador viaje) {
        if (getPasajeros() + viaje.cantidad <= this.CAPACIDAD_MAXIMA) {
            return true;
        }
        return false;
    }

    public boolean pisoOrigen(generador viaje) {
        if (getPisoActual() == viaje.origen && viaje.estado == 2) {// estado 2 = pendiente
            return true;
        }
        return false;
    }

    public boolean pisoDestino(generador viaje) {
        if (getPisoActual() == viaje.destino && viaje.estado == 1) {// estado 1 = en curso
            return true;
        }
        return false;
    }

    public int validarViajes() {
		int pendingRequests = 0;
		for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual++) {
			generador viaje = (generador) gen.viajes.get(viajeActual);
			if (viaje.estado == 2) {
				pendingRequests++;
			}
		}
		return pendingRequests;
	}

    public void cambiarSentido() {
        if (getPisoActual() == 0 && viajesPendientes(1)) {
            setSentido(1);

        } else if (getSentido() == 1 && !viajesPendientes(1)) {
            setSentido(-1);

        } else if (getSentido() == -1 && !viajesPendientes(-1)) {
            setSentido(0);
        }else if(getSentido() == 0){
            if (viajesPendientes(1)) {
                setSentido(1);
            } else if (viajesPendientes(-1)) {
                setSentido(-1);
            }
        }
    }

    public boolean viajesPendientes() {
        /*
         * comprobamos si existe algun viaje pendiente sin distinguir el destino/sentido
         */
        for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual++) {
            generador viaje = (generador)gen.viajes.get(viajeActual);
            if (viaje.estado == 2 || viaje.estado == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean viajesPendientes(int sentido) {
        boolean pendientes = false;
        switch (sentido) {
            case 1:
            /*
             * Se comprueba en la lista de viajes si existe alguno pendiente o en progreso con direccion hacia arriba
             * o tambien representada con el sentido==1
             */
            for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual++) {
                    generador viaje = (generador) gen.viajes.get(viajeActual);
                    if ((viaje.destino >= getPisoActual() && viaje.estado == 1)
                            || (viaje.origen >= getPisoActual() && viaje.estado == 2)) {
                        pendientes = true;
                        break;
                    }
                }
                break;
                /*
                 * Se comprueba en la lista de viajes si existe alguno pendiente o en progreso con direccion hacia abajo o tambien representada con el sentido==-1
                 */
                case -1:
                for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual++) {
                    generador viaje = (generador) gen.viajes.get(viajeActual);
                    if ((viaje.origen <= getPisoActual() && viaje.estado == 2)
                            || (viaje.destino <= getPisoActual() && viaje.estado == 1)) {
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

    public void verGenerador() {
        for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual++) {
            generador viaje = (generador) gen.viajes.get(viajeActual);

            if (pisoDestino(viaje)) {
                setPasajeros(-viaje.cantidad);
                System.out.println(viaje.cantidad + " personas llegaron a su destino. Personas restantes: "
                        + getPasajeros() + " al nivel: " + getSentido());
                viaje.estado = 0;
                setViajes();
                

                // gen.viajes.remove(viajeActual); // no se si esto es necesario pero elimina el
                // viaje una vez terminado
            }
        }
        for (int viajeActual = 0; viajeActual < gen.viajes.size(); viajeActual++) {
            generador viaje = (generador) gen.viajes.get(viajeActual);

            if (pisoOrigen(viaje)) {
                if(!validarEspacio(viaje)) {
                    System.out.println("No hay espacio para subir a las personas");
                    break;
                }
                if (validarEspacio(viaje) && (getSentido() == viaje.direccion || validarViajes() == 1)) {
                    if (validarViajes() == 2) {
                        setSentido(viaje.direccion);
                    }
                    setPasajeros(viaje.cantidad);
                    System.out.println(viaje.cantidad + " entraron al elevador. pasajeros: "
                            + getPasajeros() + " Con destino "+viaje.destino+" y direction: " + (getSentido() == 1? "Arriba" : "Abajo"));
                    viaje.estado = 1;
                }
            }

        }

    }


}
