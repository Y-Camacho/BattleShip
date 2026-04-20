package ycamacho.ra3.pt4;

public class Grid {

	public final static int GRID_ALLY = 0;
	public final static int GRID_ENEMY = 1;
	
	private int team;
	private int[][] grid;
	
	public Grid (int team)  {
		
		this.team = team;
		this.grid = new int[5][5];
		printGrid(grid);
		
	}
	
	public void printGrid(int[][] grid) {
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
