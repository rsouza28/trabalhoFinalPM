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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;



/**
 * @author rsouza
 *
 */
public class Paint {
	
	public static String caminhoArquivo = escolheArquivoHTML();

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	/*
	 * Escolhe o arquivo HTML que será usado como base para a grade curricular
	 */
	
	public static String escolheArquivoHTML() {
		
		JFileChooser arquivo = new JFileChooser();
		
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("Arquivo html", "html");
		arquivo.setDialogTitle("Selecione a grade curricular");
		arquivo.setFileFilter(filtro);
        int returnVal = arquivo.showOpenDialog(null);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	return arquivo.getSelectedFile().getAbsolutePath();
        }
        else
        	System.exit(0);
		
		
		return null;
	}
	
	/*
	 * Define onde o usuário irá salvar o arquivo html já editado
	 */
	public static String destinoArquivoHTML(){
		//String matricula = Import.retornaMatricula(); 
		
		JFileChooser destino = new JFileChooser();
		int returnVal = destino.showSaveDialog(null);
		
		destino.setDialogTitle("Salva como...");
		
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return destino.getSelectedFile().getAbsolutePath();
		}
		else
			System.exit(0);
		
		
		return null;
	}
	
	/*
	 * Realiza o import do arquivo html selecionado pelo usuário 
	 */
	
	public Scanner importaGrade() throws IOException {
		
		if(caminhoArquivo.equals("NoFile"))
			System.exit(0);
		
		try {
			FileInputStream fis = new FileInputStream(caminhoArquivo);
			
			Scanner s = new Scanner(fis);
			
			return s;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/*
	 * Exporta o arquivo html no lugar selecionado pelo usuário, com a grade curricular editada de acordo com o histórico do aluno 
	 */
	public void exportaGradeColorida() {
		
		try {
			Import imp = new Import();
			
			
			String destino = destinoArquivoHTML();
			
			
			destino = destino + ".html";
			
			PrintWriter saida = new PrintWriter(
					new File(destino));
			
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
									
						situacao = "";
						trocaCor = false;
					}
				}
				saida.print(linha+"\r\n");	
				
			}
			saida.close();
			s.close();
			
			System.out.println("Exportado com sucesso em: "+destino);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			

	}
	
	

}
