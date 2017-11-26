/**
 * 
 */
package pm.trabalho;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;



/**
 * @author rsouza
 *
 */
public class PaintSVG {
	
	static String velhaCor = "#ffffff";
	static String novaCor;
	public static String caminhoArquivo;


	/** 
	 * @param args
	 * @throws IOException 
	 */
	
	
	public PaintSVG() {
		caminhoArquivo = escolheArquivoSVG();
	}
	
	public PaintSVG(boolean teste) {
		
	}
	
	
	public String escolheArquivoSVG() {
		
		JFileChooser arquivo = new JFileChooser();
		
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("Arquivo svg", "svg");
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
	public String destinoArquivoSVG(){
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
	 * Remove a acentuação para remover problemas com exportação.
	 */
	public String removerAcentos(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
	
	/*
	 * Realiza o import do arquivo svg selecionado pelo usuário 
	 */
	public Scanner importaGrade() throws IOException {
		
		
		try {
			FileInputStream fis = new FileInputStream(caminhoArquivo);
			
			Scanner s = new Scanner(fis, "UTF-8");
			
			return s;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/*
	 * Exporta o arquivo svg no lugar selecionado pelo usuário, com a grade curricular editada de acordo com o histórico do aluno 
	 */
	public void exportaGradeColorida() {
		
		Import imp = Regras.imp;
		String destino = destinoArquivoSVG()+".svg"; 
		
		try {
			//Import imp = new Import();
						
			PrintWriter saida = new PrintWriter(
					new File(destino));
			
			Scanner s = importaGrade();
			
			String linha = "";
			String situacao = "";
			String regex = "[id\"=][A-Z]{3}[0][0-9]{3}";
			String disciplina = "";
			Boolean alteraCor = false;	
			
			while(s.hasNextLine()) {
				novaCor = "";
				linha = s.nextLine();
				linha = removerAcentos(linha);
				
				if(linha.indexOf("<path") != -1) {
					ArrayList<String> linhas = new ArrayList<String>();
					linhas.add(linha);
					while(linha.indexOf("/>") == -1) {
						linha = s.nextLine();
						linhas.add(linha);						
					}
				
					int i = 0;
					int numLinhaCor = -1;
					
					while(i<linhas.size()) {
						if (linhas.get(i).contains("fill:#ffffff")) {
							numLinhaCor = i;
						}
						//System.out.println(linhas.get(i));
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(linhas.get(i));
						if(matcher.find()) {
							int pos = matcher.start()+1;
							disciplina = linhas.get(i).substring(pos, pos+7);
							situacao = imp.situacaoDiscplina(disciplina);
							alteraCor = alteraCor(situacao);
							
							//System.out.println(disciplina + " - " + situacao + " -> "+alteraCor);
						}
						else if(linhas.get(i).indexOf("id=\"OPTATIVA_0")!= -1) {
							int pos = linhas.get(i).indexOf("id=\"OPTATIVA_0");
							disciplina = linhas.get(i).substring(pos+4, pos+15);
							situacao = imp.situacaoOptativa(disciplina);
							alteraCor = alteraCor(situacao);
							//System.out.println(disciplina + " - " + situacao + " -> "+alteraCor);
							
						}
						
						else if (linhas.get(i).indexOf("id=\"ELETIVA_0") != -1){
							int pos = linhas.get(i).indexOf("id=\"ELETIVA_0");
							disciplina = linhas.get(i).substring(pos + 4, pos + 14);
							situacao = imp.situacaoEletiva(disciplina);
							alteraCor = alteraCor(situacao);
							//System.out.println(disciplina + " - " + situacao + " -> "+alteraCor);
						}
						i++;
					}
					
					if(alteraCor) {
						//System.out.println("alterar a linha "+numLinhaCor);
						linhas.set(numLinhaCor, linhas.get(numLinhaCor).replace("fill:"+velhaCor, "fill:"+novaCor));
						alteraCor = false;
						//System.out.println("NOVA LINHA -> "+ linhas.get(numLinhaCor));
					}
					
					
					for(i=0;i<linhas.size();i++) {
						//i = i;
						saida.print(linhas.get(i)+"\r\n");
						//System.out.println(linhas.get(i));	
					}
				}
				
				saida.print(linha+"\r\n");
			}
			saida.close();
			s.close();
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Indica se é pra alterar a cor da disciplina e seta o valor para cor, de acordo com a situação do aluno na disciplina. 
	 */
	public boolean alteraCor(String situacao) {
		if(situacao.equals("VENCIDO")) {
			novaCor = "#00FF00"; //#FF0000
			return true;
			}
		else if(situacao.equals("REPROVADO")) {
			novaCor = "#FF0000";
			return true;
		}
		else
			novaCor = velhaCor;
		
		return false;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//paint.optativasParaPintar();

	}
	
	

}
