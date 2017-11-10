/**
 * 
 */
package pm.trabalho;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author rsouza
 *
 */
public class Paint {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	public static Scanner importaGrade() throws IOException {
		
		try {
			FileInputStream fis = new FileInputStream("C:/Users/rsouza/git/trabalhoFinalPM/TrabalhoFinalPM/src/files_import/grade_curricular.html");
			
			Scanner s = new Scanner(fis);
			
			return s;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public static void exportaGradeColorida() {
		
		try {
			Import imp = new Import();
			
			String matricula = Import.retornaMatricula(); 
			PrintWriter saida = new PrintWriter(
					new File("C:/Users/rsouza/git/trabalhoFinalPM/TrabalhoFinalPM/src/files_export/grade_"+matricula+".html"));
			
			Scanner s = importaGrade();
			String linha = "";
			String situacao = "";
			String regex = "[#][A-Z]{3}[0][0-9]{3}";
			String disciplina;
			boolean trocaCor = false;
			
			
			
			while(s.hasNextLine()) {
				linha = s.nextLine();
				
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(linha);
				
				if(matcher.find()) {
					int pos = matcher.start();
					disciplina = linha.substring(pos+1, pos + 8);
					situacao = imp.situacaoDiscplina(disciplina);
					trocaCor = true;
				}
				
				if(trocaCor) {
					if(linha.contains("ctx.fillStyle")) {
						if (situacao.equals("VENCIDO"))
							linha = "	ctx.fillStyle = 'rgb(0, 255, 0)';" + "// ------>" + situacao;
						else if (situacao.equals("REPROVADO"))
							linha = "	ctx.fillStyle = 'rgb(255, 0, 0)';";
						//else 
							//linha = "	ctx.fillStyle = 'rgb(0, 0, 0)';";
						situacao = "";
						trocaCor = false;
					}
				}
				saida.print(linha+"\r\n");	
				System.out.println(linha);
				
			}
			saida.close();
			s.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			
		 exportaGradeColorida();
		
				

	}
	
	

}
