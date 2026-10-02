import java.util.*;

public class SistemaVuelos {

    // Clase que representa un vuelo (Arista con precio)
    static class Vuelo {
        String destino;
        int precio;

        public Vuelo(String destino, int precio) {
            this.destino = destino;
            this.precio = precio;
        }
    }

    // Clase para procesar las rutas en el algoritmo de Dijkstra
    static class RutaProgreso implements Comparable<RutaProgreso> {
        String ciudadActual;
        int costoAcumulado;
        List<String> ciudadesRecorridas;

        public RutaProgreso(String ciudadActual, int costoAcumulado, List<String> historial) {
            this.ciudadActual = ciudadActual;
            this.costoAcumulado = costoAcumulado;
            this.ciudadesRecorridas = new ArrayList<>(historial);
            this.ciudadesRecorridas.add(ciudadActual);
        }

        @Override
        public int compareTo(RutaProgreso otra) {
            return Integer.compare(this.costoAcumulado, otra.costoAcumulado);
        }
    }

    // Grafo representado mediante una Lista de Adyacencia
    private Map<String, List<Vuelo>> grafoVuelos = new HashMap<>();

    public void agregarConexion(String origen, String destino, int precio) {
        grafoVuelos.putIfAbsent(origen, new ArrayList<>());
        grafoVuelos.putIfAbsent(destino, new ArrayList<>());
        grafoVuelos.get(origen).add(new Vuelo(destino, precio));
    }

    // REPORTERÍA: Permite visualizar la estructura completa del Grafo
    public void mostrarReporteria() {
        System.out.println("\n=============================================");
        System.out.println("    REPORTERÍA DE LA ESTRUCTURA DE DATOS");
        System.out.println("=============================================");
        for (Map.Entry<String, List<Vuelo>> entrada : grafoVuelos.entrySet()) {
            String origen = entrada.getKey();
            List<Vuelo> vuelos = entrada.getValue();
            
            System.out.println("\n[Ciudad de Origen]: " + origen.toUpperCase());
            if (vuelos.isEmpty()) {
                System.out.println("  └──> (Sin vuelos de salida registrados)");
            } else {
                for (Vuelo v : vuelos) {
                    System.out.printf("  └──> Destino: %-12s | Precio: $%d USD\n", v.destino, v.precio);
                }
            }
        }
        System.out.println("=============================================\n");
    }

    // ALGORITMO DE OPTIMIZACIÓN (Dijkstra)
    public RutaProgreso buscarRutaMasBarata(String origen, String destino) {
        if (!grafoVuelos.containsKey(origen) || !grafoVuelos.containsKey(destino)) {
            return null;
        }

        PriorityQueue<RutaProgreso> colaPrioridad = new PriorityQueue<>();
        Set<String> visitados = new HashSet<>();

        // Agregar punto de partida
        colaPrioridad.add(new RutaProgreso(origen, 0, new ArrayList<>()));

        while (!colaPrioridad.isEmpty()) {
            RutaProgreso actual = colaPrioridad.poll();

            if (actual.ciudadActual.equalsIgnoreCase(destino)) {
                return actual; // Se encontró el camino más económico
            }

            if (visitados.contains(actual.ciudadActual)) {
                continue;
            }
            visitados.add(actual.ciudadActual);

            for (Vuelo v : grafoVuelos.getOrDefault(actual.ciudadActual, new ArrayList<>())) {
                if (!visitados.contains(v.destino)) {
                    int nuevoCosto = actual.costoAcumulado + v.precio;
                    colaPrioridad.add(new RutaProgreso(v.destino, nuevoCosto, actual.ciudadesRecorridas));
                }
            }
        }
        return null; // No hay conexión
    }

    // MENÚ INTERACTIVO
    public static void main(String[] args) {
        SistemaVuelos sistema = new SistemaVuelos();
        Scanner entrada = new Scanner(System.in);

        // Simulando la Base de Datos Ficticia requerida por la guía
        sistema.agregarConexion("Quito", "Guayaquil", 55);
        sistema.agregarConexion("Quito", "Cuenca", 80);
        sistema.agregarConexion("Guayaquil", "Cuenca", 35);
        sistema.agregarConexion("Guayaquil", "Baltra", 125);
        sistema.agregarConexion("Cuenca", "Manta", 42);
        sistema.agregarConexion("Manta", "Baltra", 60);
        sistema.agregarConexion("Quito", "Manta", 95);

        while (true) {
            System.out.println("--- GESTIÓN DE VUELOS ECONÓMICOS ---");
            System.out.println("1. Visualizar Reportería (Grafo)");
            System.out.println("2. Cotizar Itinerario Más Barato");
            System.out.println("3. Salir del Programa");
            System.out.print("Seleccione una opción: ");
            String opc = entrada.nextLine().trim();

            if (opc.equals("1")) {
                sistema.mostrarReporteria();
            } else if (opc.equals("2")) {
                System.out.println("\nCiudades: Quito, Guayaquil, Cuenca, Manta, Baltra");
                System.out.print("Ingrese Origen: ");
                String orig = entrada.nextLine().trim();
                System.out.print("Ingrese Destino: ");
                String dest = entrada.nextLine().trim();

                long inicioTiempo = System.nanoTime();
                RutaProgreso resultado = sistema.buscarRutaMasBarata(orig, dest);
                long finTiempo = System.nanoTime();

                if (resultado != null) {
                    System.out.println("\n---------------------------------------------");
                    System.out.println("       ¡RUTA OPTIMIZADA ENCONTRADA!");
                    System.out.println("---------------------------------------------");
                    System.out.println("Camino recomendado: " + String.join(" -> ", resultado.ciudadesRecorridas));
                    System.out.println("Costo total: $" + resultado.costoAcumulado + " USD");
                    System.out.printf("Tiempo de cálculo: %.6f milisegundos\n", (finTiempo - inicioTiempo) / 1000000.0);
                    System.out.println("---------------------------------------------");
                } else {
                    System.out.println("\n[Error]: No hay vuelos disponibles para esa ruta.");
                }
            } else if (opc.equals("3")) {
                System.out.println("\nCerrando el sistema...");
                break;
            } else {
                System.out.println("\nOpción no válida. Intente de nuevo.\n");
            }
        }
        entrada.close();
    }
}
