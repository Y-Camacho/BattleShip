package ycamacho.ra3.pt4;

public class Grid {

	public final static int GRID_ALLY = 0;
	public final static int GRID_ENEMY = 1;
	
	static final int SIZE = 5;
	static final int SHIP_3 = 1;
	static final int SHIP_2 = 0;
	static final int SHIP_1 = 0;
	
	private int team;
	private int[][] grid;
	
	int ships3Placed = 0;
	int ships2Placed = 0;
	int ships1Placed = 0;
	
	
	public Grid (int team)  {
		
		this.team = team;
		this.grid = new int[SIZE][SIZE];
	}
	
	// Función para disparar a la cuadrícula
	public String shoot(String coord) {
	    int row = coord.charAt(1) - '1';
	    int col = coord.toUpperCase().charAt(0) - 'A';
	    
	    if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
	        return "error";
	    }

	    if (grid[row][col] == 0) {
	        grid[row][col] = 2; // agua
	        return "aigua";
	    }

	    if (grid[row][col] == 1) {
	        grid[row][col] = 3; // tocado
	        
	        if (isShipSunk(row, col)) {	
	            return "enfonsat";
	        } else {
	            return "tocat";
	        }
	    }

	    return "repetido"; // opcional
	}
	
	// Función para saber si todos los barcos están undidos
	public boolean allShipsSunk() {
		for ( int i = 0; i < SIZE; i ++) {
			for ( int j = 0; j < SIZE; j ++) {
				if ( grid[i][j] == 1 ) return false;
			}
		}
		
		return true;
	}
	
	// Función para verificar si un barco ya está totalmente undido
	private boolean isShipSunk(int row, int col) {
	    
	    // direcciones: arriba, abajo, izquierda, derecha
	    int[][] dirs = {
	        {-1, 0},
	        {1, 0},
	        {0, -1},
	        {0, 1}
	    };

	    for (int[] d : dirs) {
	        int r = row;
	        int c = col;

	        while (true) {
	            r += d[0];
	            c += d[1];

	            // fuera del tablero
	            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) break;

	            // si encontramos parte viva del barco → NO está hundido
	            if (grid[r][c] == 1) {
	                return false;
	            }

	            // si ya no es barco (agua o fallo) → dejamos de mirar en esa dirección
	            if (grid[r][c] != 3) {
	                break;
	            }
	        }
	    }

	    return true;
	}
	
	// Función para para graficar el estado del grid del enemigo según nuestras tiradas
	public void markEnemyGrid(String coord, String result) {

	    int row = coord.charAt(1) - '1';
	    int col = coord.toUpperCase().charAt(0) - 'A';

	    if (result.equals("aigua")) {
	        grid[row][col] = 2; // agua (X)
	    }

	    if (result.equals("tocat") || result.equals("enfonsat")) {
	        grid[row][col] = 3; // barco tocado
	    }
	}
	
	// Función para ubicar un barco en la cuardícula 
	public boolean placeShip(String input) {
		
		String[] coordenadas = input.split(":");
		
		if ( allShipsPlaced() ) {
			System.out.println("Ya están posicionados todos los barcos");
			return false;
		}
		
		if (coordenadas.length > 2) {
			System.out.println("Indica solo la coordenada inicial y la final del barco [" + input + "]");
			return false;
		}
			
		// Parsear cada coordenada
		int row1, col1;
		int row2, col2;

		// Primera coordenada
		String c1 = coordenadas[0].toUpperCase();

		col1 = c1.charAt(0) - 'A';   // A=0, B=1...
		row1 = Character.getNumericValue(c1.charAt(1)) - 1; // 1=0, etc.
		
		// Detectar si es barco de 1 o de varios
		if (coordenadas.length == 1) {
		    row2 = row1;
		    col2 = col1;
		} else {
		    String c2 = coordenadas[1].toUpperCase();
		    
		    col2 = c2.charAt(0) - 'A';
		    row2 = Character.getNumericValue(c2.charAt(1)) - 1;
		}
		
		// Validar que está dentro del tablero
		if (row1 < 0 || row1 >= 5 || col1 < 0 || col1 >= 5 ||
		    row2 < 0 || row2 >= 5 || col2 < 0 || col2 >= 5) {
		    System.out.println("Fuera del tablero");
		    return false;
		}
		
		// Validar que es horizontal o vertical
		if (row1 != row2 && col1 != col2) {
		    System.out.println("No puede ser diagonal");
		    return false;
		}
		
		// Recorrer las casillas del barco
		int rowStart = Math.min(row1, row2);
		int rowEnd   = Math.max(row1, row2);
		int colStart = Math.min(col1, col2);
		int colEnd   = Math.max(col1, col2);
		
		int shipSize;

		if (row1 == row2) {
		    shipSize = colEnd - colStart + 1;
		} else {
		    shipSize = rowEnd - rowStart + 1;
		}
		
		// Validar el tamaño del barco
		if (shipSize > 3 || shipSize < 1) {
		    System.out.println("Tamaño de barco inválido");
		    return false;
		}
		
		// Validar que no se pongan más del número permitido de un barco de cierto tamaño
		if ( ! validateShipCount(shipSize) ) {
			System.out.println("Ya has colocado todos los barcos de tamaño " + shipSize);
			return false;
		}
		
		for (int i = rowStart; i <= rowEnd; i++) {
		    for (int j = colStart; j <= colEnd; j++) {
		        
		        // comprobar si ya hay barco
		        if (grid[i][j] == 1) {
		            System.out.println("Ya hay un barco ahí");
		            return false;
		        }
		        
		        // comprobar alrededores (NO tocar otros barcos)
		        for (int x = i - 1; x <= i + 1; x++) {
		            for (int y = j - 1; y <= j + 1; y++) {
		                
		                if (x >= 0 && x < 5 && y >= 0 && y < 5) {
		                    if (grid[x][y] == 1) {
		                        System.out.println("No puede tocar otro barco");
		                        return false;
		                    }
		                }
		            }
		        }
		    }
		}
		
		// Colocar el barco
		for (int i = rowStart; i <= rowEnd; i++) {
		    for (int j = colStart; j <= colEnd; j++) {
		        grid[i][j] = 1;
		    }
		}
		
		// Sumar tipo de barco
		switch (shipSize) {
		    case 3: ships3Placed++; break;
		    case 2: ships2Placed++; break;
		    case 1: ships1Placed++; break;
		}
		
		// Mostrar el tablero actualizado
		printGrid();
		return true;
	}
	
	// Indica si ya están todos los barcos posicionador
	public boolean allShipsPlaced() {
		if( (ships3Placed + ships2Placed + ships1Placed) == (SHIP_1 + SHIP_2 + SHIP_3) )
			return true;
				
		return false;
	}
	
	// Valida si ya se ha llegado al límite de cada tipo de barco
	private boolean validateShipCount(int shipSize) {
		switch (shipSize) {
	    case 3:
	        if (ships3Placed >= SHIP_3) {
	            return false;
	        }
	        break;
	        
	    case 2:
	        if (ships2Placed >= SHIP_2) {
	            return false;
	        }
	        break;
	        
	    case 1:
	        if (ships1Placed >= SHIP_1) {
	            return false;
	        }
	        break;
		}
		
		return true;
	}
	
	// Imprime el grid, graficando respectivamente segun el número en cada casilla
	public void printGrid() {
	    // Encabezado de letras
		String strTeam = ( this.team == GRID_ALLY ) ? "  *= = = = ALLY = = = =*\n" : "  *= = = = ENEMY = = = =*\n"; 
	    String strGrid = "     A   B   C   D   E\n";
	    String separador = "    +---+---+---+---+---+\n";
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append(strTeam).append(strGrid).append(separador);

	    for (int i = 0; i < grid.length; i++) {
	        // Número de fila 
	        sb.append(String.format("%2d  |", i + 1)); 

	        for (int j = 0; j < grid[i].length; j++) {
	            String contenido;
	            switch (grid[i][j]) {
	                case 0: contenido = "   "; break; // Agua/Vacío
	                case 1: contenido = "[ ]"; break; // Barco
	                case 2: contenido = " X "; break; // Fallo (Agua tocada)
	                case 3: contenido = "[*]"; break; // Tocado/Hundido
	                default:
	                    throw new IllegalArgumentException("Valor inesperado: " + grid[i][j]);
	            }
	            sb.append(contenido).append("|");
	        }
	        sb.append("\n").append(separador);
	    }
	    
	    // Imprimir el resultado final
	    System.out.print(sb.toString());
	}
		
}
