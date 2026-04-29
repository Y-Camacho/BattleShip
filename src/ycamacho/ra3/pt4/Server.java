package ycamacho.ra3.pt4;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {

    private Grid grid;
    private Grid gridEnemy;

    public Server(Grid grid) {
        this.grid = grid;
        this.gridEnemy = new Grid(Grid.GRID_ENEMY);
    }

    public void start() throws Exception {

        ServerSocket server = new ServerSocket(5020);
        System.out.println("Esperando conexión...");

        Socket socket = server.accept();
        System.out.println("Cliente conectado");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("INIT:5,1,1,3");

        String response = in.readLine();

        if (!response.equals("OK")) {
            socket.close();
            return;
        }

        Scanner sc = new Scanner(System.in);

        while (true) {

            // Turno server
            System.out.print("\nDispara: ");
            String shot = sc.nextLine();

            out.println("SHOT:" + shot);

            String resultMsg = in.readLine();

            if (resultMsg == null) {
                break;
            }

            // Caso fin de partida
            if (resultMsg.equals("WIN")) {
                System.out.println("Has ganado la partida!");
                break;
            }

            // Caso normal
            if (!resultMsg.startsWith("RESULT:")) {
                System.out.println("Mensaje inesperado: " + resultMsg);
                break;
            }

            String result = resultMsg.split(":")[1];

            gridEnemy.markEnemyGrid(shot, result);

            grid.printGrid();
            gridEnemy.printGrid();

            System.out.println("Resultado: " + result);

            if (result.equals("WIN")) break;

            // Turno cliente
            String enemyShot = in.readLine();

            if (enemyShot.startsWith("SHOT:")) {

                String coord = enemyShot.split(":")[1];

                String res = grid.shoot(coord);
                
                grid.printGrid();

                System.out.println("Te disparan a " + coord + " → " + res);

                if (grid.allShipsSunk()) {
                    out.println("WIN");
                    break;
                } else {
                    out.println("RESULT:" + res);
                }
            }
        }

        socket.close();
        server.close();
    }

    private int parseResult(String result) {
        switch (result) {
            case "aigua": return 0;
            case "tocat": return 3;
            case "enfonsat": return 3;
            case "WIN":
                return 1;
            default:
                return 0;
        }
    }
}