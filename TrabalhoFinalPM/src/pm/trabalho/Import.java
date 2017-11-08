/**
 * 
 */
package pm.trabalho;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
 
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;


/**
 * @author rsouza
 *
 */
public class Import {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 
	 * Abre a conexão com arquivo e retorna o arquivo lido.
	 */
	public static Scanner importaPDF() {
		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		File arquivo = new File(
				"C:/users/rsouza/Documents/historico.pdf");
	
		try {
			PDFParser parser = new PDFParser(new FileInputStream(arquivo));
			parser.parse();
			cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);
			pdfStripper.setStartPage(1);
			pdfStripper.setEndPage(pdfStripper.getEndPage());
			String parsedText = pdfStripper.getText(pdDoc);
			
			parsedText = parsedText.replaceAll(" ","");
			pdDoc.close();
			Scanner s = new Scanner(parsedText);
			
			return s;
			
			} catch (IOException e) {
				e.printStackTrace();
			}
	
		return null;
	}
	
	public static void arquivoSaida() {
		
		try {
			
			PrintWriter saida = new PrintWriter(
					new File("C:/users/rsouza/Documents/saida_teste1.txt"));
			
			Scanner s = importaPDF();
			s.useDelimiter("\r\n");
			
			
			String linha = "";
			
			while(s.hasNext()) {
				linha = s.next().toUpperCase();
				saida.print(linha);
				saida.print("\r\n");
			}
			
			saida.close();
			s.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Lê o arquivo importado e procura onde está a matrícula Ao encontrar, retorna.
	 */
	
	public static String retornaMatricula() {
		String matricula;
		String linha;
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		
		while(s.hasNext()) {
			linha = s.next().toUpperCase();
			
			if(linha.indexOf("MATRÍCULA") != -1) {
				matricula = linha.substring(0,11);
				return matricula;
			}
				
		}
		
		return "Matricula nao encontrada";
	}
	
	/**
	 * Retorna o ano de ingresso, usando a matricula como parâmetro
	 */
	public static int retornaAnoIngresso(String matricula) {
		int ano;
		ano = Integer.parseInt(matricula.substring(0,4));
		
		return ano;
	}
	/**
	 * Retorna o semestre de ingresso, usando a matricula como parâmetro
	 * @param matricula
	 * @return
	 */
	
	public static int retornaSemestreIngresso(String matricula) {
		int semestre;
		semestre = Integer.parseInt(matricula.substring(4,5));
		
		return semestre;
	}
	
	/**
	 * Calcula o prazo máximo para a integelização do aluno.
	 * Se entrou em 2013 ou antes, o prazo é de 14 meses.
	 * Se entrou a parti de 2014 o prazo é de 12 meses.
	 * @param ano
	 * @return
	 */
	public static int retornaPrazoMaximoIntegralizacao(int ano) {
		int prazo;
		if (ano <= 2013)
			prazo = 14;
		else
			prazo = 12;
		
		return prazo;
	}
	
	/**
	 * Varre o arquivo pdf importado procurando pelos códigos das disciplinas e a situação do aluno na mesma
	 * Retorna um hash com todo o histórico do aluno
	 * @return
	 */
	public Map<Integer, String[]> retornaHistoricoAluno() {
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		
		String linha = "";
		ArrayList<String> codigos = new ArrayList<String>();
		ArrayList<String> situacoes = new ArrayList<String>();
		
		while (s.hasNext()) {
			linha = s.next().toUpperCase();
			
			String regex = "[A-Z]{3}[0][0-9]{3}";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(linha);
			
			if(matcher.find()) {
				int pos = matcher.start();
				String codigo = linha.substring(pos, pos + 7); 
				codigos.add(codigo);
			}
					
			
			if (linha.indexOf("APV-") != -1 || linha.indexOf("REF-") != -1 || linha.indexOf("TRA-") != -1 || linha.indexOf("ASC-") != -1 || linha.indexOf("REP-") != -1)
			{
				String situacao = "";
				if (linha.indexOf("APV-") != -1)
					situacao = "VENCIDO";
				else if (linha.indexOf("REP-") != -1 || linha.indexOf("REF-") != -1 )
					situacao = "REPROVADO";
				else if (linha.indexOf("TRA-") != -1)
					situacao = "TRANCADO";
				else if (linha.indexOf("ASC-") != -1)
					situacao = "CURSANDO";
				else
					situacao = "NÃO REALIZADO";
				
				situacoes.add(situacao);

			}
			
		}
		
		Map<Integer, String[]> hist = new HashMap<Integer, String[]>();
		
		for(int i = 0; i < codigos.size(); i++) {
			hist.put(i + 1, new String[] {codigos.get(i), situacoes.get(i)});
		}
		
		
		return hist;
		
	}
	
	/**
	 * Lê o arquivo PDF importado e retorna o CR em cada período.
	 * @return
	 */
	
	public static Map<Integer, Double> retornaCrPeriodos() {
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		
		String linha;
		int periodo = 0; 
		double nota;
		
		Map<Integer, Double> notas = new HashMap<Integer, Double>();
		
		while(s.hasNext()) {
			linha = s.next().toUpperCase();
			
			if(linha.indexOf("COEFICIENTEDERENDIMENTO:") != - 1){
				periodo = periodo + 1;
				nota = Double.parseDouble(linha.substring(0, 6).replace(",", "."));
				notas.put(periodo, nota);
				
			}
			
			
		}
		
		return notas;
		
	}
	
	/**
	 * Lê o arquivo importado e retorna o período atual do aluno
	 * @return
	 */
	public static int retornaPeriodoAtual() {
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		String linha;
		int periodo = 0;
		
		while(s.hasNext()) {
			linha = s.next().toUpperCase();
			
			if(linha.indexOf("PERÍODOATUAL:") != - 1) {
				periodo = Integer.parseInt(linha.substring(13, 15));
				
				return periodo;
			}
				
		}
		
		return 0; 
	}
	
	/**
	 * Lê o arquivo importado e retorna o CR geral do aluno
	 * @return
	 */
	public static double retornaCrGeral() {
		
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		String linha;
		double cr = 0;
		
		while(s.hasNext()) {
			linha = s.next().toUpperCase();
			
			if(linha.indexOf("COEFICIENTEDERENDIMENTOGERAL:") != - 1) {
				cr = Double.parseDouble(linha.substring(29, 35).replace(",", "."));
				
				return cr;
			}
				
		}
		
		return 0; 
	}


}
