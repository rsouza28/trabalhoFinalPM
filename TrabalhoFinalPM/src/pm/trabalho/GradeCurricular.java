/**
 * 
 */
package pm.trabalho;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author rsouza
 *
 */
public class GradeCurricular {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		

		Regras michel= new Regras("C:/Users/rsouza/Documents/historico michel.pdf");
		michel.verificaTodasAsRegras(michel);

		PaintSVG paintMichel = new PaintSVG();
		paintMichel.exportaGradeColorida();
		paintMichel.criaArquivoHTML(paintMichel.destinoArquivo, michel.verificaTodasAsRegras(michel));

		
		Regras rodrigo = new Regras();
		
		rodrigo.verificaTodasAsRegras(rodrigo);

		PaintSVG paintRodrigo = new PaintSVG();
		paintRodrigo.exportaGradeColorida();
		paintRodrigo.criaArquivoHTML(paintRodrigo.destinoArquivo, rodrigo.verificaTodasAsRegras(rodrigo));
		

	}
	
	
	

}
