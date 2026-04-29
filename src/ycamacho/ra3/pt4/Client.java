package ycamacho.ra3.pt4;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private Grid grid;
    private Grid gridEnemy;

    public Client(Grid grid) {
        this.grid = grid;
        this.gridEnemy = new Grid(Grid.GRID_ENEMY);
    }

    public void start(String ip) throws Exception {

        Socket socket = new Socket(ip, 5020);
        System.out.println("Conectado al servidor");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String init = in.readLine();

        if (!init.equals("INIT:5,1,1,3")) {
            out.println("ERROR");
            socket.close();
            return;
        }

        out.println("OK");

        Scanner sc = new Scanner(System.in);

        while (true) {

            // Recibir disparo enemigo
            String msg = in.readLine();

            if (msg.startsWith("SHOT:")) {

                String coord = msg.split(":")[1];

                String res = grid.shoot(coord);

                grid.printGrid();
                
                System.out.println("\nTe disparan a " + coord + " → " + res);

                if (grid.allShipsSunk()) {
                    out.println("WIN");
                    break;
                } else {
                    out.println("RESULT:" + res);
                }
            }

            // Turno
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
        }

        socket.close();
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