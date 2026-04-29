package ycamacho.ra3.pt4;

import java.util.Scanner;

public class Game {
    
    private Grid grid;
    
    public Game() {
        grid = new Grid(Grid.GRID_ALLY);
    }
    
    public void setupBoard() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println(" *** Comencemos con la estrategia *** ");

        while (!grid.allShipsPlaced()) {
            System.out.println("Introduce coordenadas:");
            String input = sc.nextLine();

            if (!grid.placeShip(input)) {
                System.out.println("Error, prueba otra vez");
            }
        }
    }
    
    public void startGame() throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Escuchar (Servidor)");
        System.out.println("2. Conectar (Cliente)");

        String option = sc.nextLine();

        if (option.equals("1")) {
            Server server = new Server(grid);
            server.start();
        } else {
            System.out.print("IP: ");
            String ip = sc.nextLine();

            Client client = new Client(grid);
            client.start(ip);
        }
    }
}
