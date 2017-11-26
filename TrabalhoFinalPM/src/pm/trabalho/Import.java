/**
 * 
 */
package pm.trabalho;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
	
	static String caminhoArquivo;
	
	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/*
	 * Construtor onde o usu�rio escolhe o arquivo atrav�s do JFileChooser
	 */
	public Import() {
		 caminhoArquivo = importaArquivoPDF();
	}
	
	/*
	 * Construtor onde o arquivo � passado como par�metro
	 */
	public Import(String caminho) {
		caminhoArquivo = caminho; 
	}
	
	/*
	 * Usa o JFileChooser para importar o arquivo PDF
	 */
	public String importaArquivoPDF() {
		
		JFileChooser arquivo = new JFileChooser();
		
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("Arquivo pdf", "pdf");
		arquivo.setDialogTitle("Selecione o historico de um aluno");
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
	 * Abre a conex�o com arquivo PDF e retorna o arquivo lido.
	 */
	public static Scanner importaPDF() {
		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		
		try {
			File arquivo = new File(
					caminhoArquivo);
			
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
	
	/*
	 * L� o arquivo importado e procura onde est� a matr�cula Ao encontrar, retorna.
	 */
	public String retornaMatricula() {
		String matricula;
		String linha;
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		
		while(s.hasNext()) {
			linha = s.next().toUpperCase();
			
			if(linha.indexOf("MATR�CULA") != -1) {
				matricula = linha.substring(0,11);
				return matricula;
			}
		}
		
		return "Matricula nao encontrada";
	}
	
	/*
	 * Retorna o ano de ingresso, usando a matricula como par�metro
	 */
	public int retornaAnoIngresso(String matricula) {
		int ano;
		ano = Integer.parseInt(matricula.substring(0,4));
		
		return ano;
	}
	
	/*
	 * Retorna o semestre de ingresso, usando a matricula como par�metro
	 */
	
	public int retornaSemestreIngresso(String matricula) {
		int semestre;
		semestre = Integer.parseInt(matricula.substring(4,5));
		
		return semestre;
	}
	
	/*
	 * Calcula o prazo m�ximo para a integeliza��o do aluno.
	 * Se entrou em 2013 ou antes, o prazo � de 14 meses.
	 * Se entrou a parti de 2014 o prazo � de 12 meses.
	 */
	public int retornaPrazoMaximoIntegralizacao(int ano) {
		int prazo;
		if (ano <= 2013)
			prazo = 14;
		else
			prazo = 12;
		
		return prazo;
	}
	
	/*
	 * Varre o arquivo pdf importado procurando pelos c�digos das disciplinas e a situa��o do aluno na mesma
	 * Retorna um hash com todo o hist�rico do aluno
	 */
	public Map<Integer, String[]> retornaHistoricoAluno() {
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		
		String linha = "";
		ArrayList<String> codigos = new ArrayList<String>();
		ArrayList<String> situacoes = new ArrayList<String>();
		
		while (s.hasNext()) {
			linha = s.next().toUpperCase();
			
			// Express�o regular que define os c�digos da disciplina no arquivo
			String regex = "[A-Z]{3}[0][0-9]{3}";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(linha);
			
			// Condi��o para verificar se a linha examinada possui o c�digo da disciplina. Existindo, adiciona em um ArrayList
			if(matcher.find()) {
				int pos = matcher.start();
				String codigo = linha.substring(pos, pos + 7); 
				codigos.add(codigo);
			}
					
			// Condi��o para verificar a situa��o do aluno em determinada disciplina. Quando encontrar, adiciona em um ArrayList
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
					situacao = "N�O REALIZADO";
				
				situacoes.add(situacao);

			}
			
		}
		//Transformar os dois ArrayList em um HashMap
		Map<Integer, String[]> hist = new HashMap<Integer, String[]>();
		
		for(int i = 0; i < codigos.size(); i++) {
			hist.put(i + 1, new String[] {codigos.get(i), situacoes.get(i)});
		}
		
		return hist;
	}
	
	/*
	 * Monta o hist�rico somente com a situa��o mais recente do aluno em cada disciplina 
	 */
	public Map<String, String> ultimaSituacaoDisciplinas(){
		
		Map<String, String> ultimaSituacao = new HashMap<String, String>();
		Map<Integer, String[]> hist = retornaHistoricoAluno();
		
		// Looping usado para verificar se a disciplina foi cursada mais de uma vez, pegando ent�o a �ltima vez que foi realizada.
		for(Map.Entry<Integer, String[]> entry : hist.entrySet()) {
			String[] valor = entry.getValue();
			if(!valor[1].equals("CURSANDO")) {
				if (ultimaSituacao.containsKey(valor[0]))
					ultimaSituacao.replace(valor[0], valor[1]);
				else 
					ultimaSituacao.put(valor[0], valor[1]);
			}
		}
		return ultimaSituacao;
	}
	
	/*
	 * Verifica a situa��o do aluno na disciplina passada por par�metro. Se n�o existir, a disciplina n�o foi realizada.
	 */	
	public String situacaoDiscplina(String disciplina) {
		// Hardcode para currigir erro do arquivo SVG.
		if(disciplina.equals("TIN0010"))
			disciplina = "TIN0110";
		
		if (ultimaSituacaoDisciplinas().containsKey(disciplina))
			return ultimaSituacaoDisciplinas().get(disciplina);
		
		return "NAO REALIZADO";
	}
	
	/*
	 * L� o arquivo PDF importado e retorna o CR em cada per�odo.
	 */
	public Map<Integer, Double> retornaCrPeriodos() {
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		
		String linha;
		int periodo = 0; 
		double nota;
		
		Map<Integer, Double> notas = new HashMap<Integer, Double>();
		// Varre as linhas at� achar onde est�o o Coeficiente de Rendimento de cada per�odo cursado. 
		// Quando encontra, adiciona no HashMap notas, onde Key � o per�odo e Value � a nota.
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
	
	/*
	 * L� o arquivo importado e retorna o per�odo atual do aluno
	 */
	public int retornaPeriodoAtual() {
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		String linha;
		int periodo = 0;
		// Varre o arquivo e quando encontra o per�odo atual, retorna o valor.
		while(s.hasNext()) {
			linha = s.next().toUpperCase();
			
			if(linha.indexOf("PER�ODOATUAL:") != - 1) {
				int pos = linha.indexOf("�");
				periodo = Integer.parseInt(linha.substring(13, pos));
				
				return periodo;
			}
		}
		return 0; 
	}
	
	/*
	 * L� o arquivo importado e retorna o CR geral do aluno
	 */
	public double retornaCrGeral() {
		
		Scanner s = importaPDF();
		s.useDelimiter("\r\n");
		String linha;
		double cr = 0;
		
		// Varre o arquivo at� encontrar o Coeficiente de Rendimento Geral. Quando encontra, retorna o valor.
		while(s.hasNext()) {
			linha = s.next().toUpperCase();
			
			if(linha.indexOf("COEFICIENTEDERENDIMENTOGERAL:") != - 1) {
				cr = Double.parseDouble(linha.substring(29, 35).replace(",", "."));
				
				return cr;
			}
				
		}
		
		return 0; 
	}
	
	/*
	 * Define quais s�o as disciplinas optativas
	 */
	public ArrayList<String> defineOptativas() {
		// Adiciona todas as disciplinas consideradas optativas, encontradas no site BSI Uniriotec. 
		ArrayList<String> optativas = new ArrayList<String>();
		optativas.add("TIN0135");
		optativas.add("TIN0144");
		optativas.add("TIN0150");
		optativas.add("TIN0146");
		optativas.add("TIN0149");
		optativas.add("TIN0138");
		optativas.add("TIN0158");
		optativas.add("TIN0143");
		optativas.add("TIN0147");
		optativas.add("TIN0136");
		optativas.add("TIN0160");
		optativas.add("TIN0128");
		optativas.add("TIN0172");
		optativas.add("TIN0142");
		optativas.add("TIN0159");
		optativas.add("TIN0148");
		optativas.add("TIN0145");
		optativas.add("TIN0137");
		optativas.add("TIN0162");
		optativas.add("TIN0163");
		optativas.add("TIN0161");
		optativas.add("TIN0166");
		optativas.add("TIN0141");
		optativas.add("TIN0164");
		optativas.add("TIN0165");
		
		return optativas;
	}
	
	/*
	 * Verifica se a disciplina passada como par�metro � optatativa
	 */
	public boolean verificaOptativa(String opt) {
		
		ArrayList<String> optativas = defineOptativas();
		
		if (optativas.contains(opt))
			return true;
		
		return false;
	}
	
	/*
	 *  Monta o historico do aluno das disciplinas optativas
	 */
	public Map<String, String> ultimaSituacaoOptativa() {
		int totalOptativas = 8;
		Map<String, String> disc = ultimaSituacaoDisciplinas();
		Map<String, String> opt = new HashMap<String, String>(); 
		
		int i = 1;
		// Verifica primeiro as optativas que foram realizadas e vencidas, uma vez que aprova��o tem prioridade sobre reprova��o.
		// Encontrando, joga em um HashMap, que � limitado ao tamanho de 8, que � o total de optativas da grade curricular 
		for(Map.Entry<String, String> entry : disc.entrySet()) {
			String optativa = entry.getKey();
			String situacao = entry.getValue();
			if(verificaOptativa(optativa) && situacao == "VENCIDO") {
				if(opt.size() < totalOptativas) {
					opt.put("OPTATIVA_0"+i,situacao);
					i++;
				}
			}
		}		
		// Verifica agora as optativas que foram realizadas e houve reprova��o.
		// Encontrando, joga em um HashMap, que � limitado ao tamanho de 8, que � o total de optativas da grade curricular
		for(Map.Entry<String, String> entry : disc.entrySet()) {
			String optativa = entry.getKey();
			String situacao = entry.getValue();
			if(verificaOptativa(optativa) && situacao != "VENCIDO") {
				if(opt.size() < totalOptativas) {
					opt.put("OPTATIVA_0"+i,situacao);
					//System.out.println("ADICIONAR OPTATIVA_0"+i+ "("+ optativa + "). Com situacao: "+situacao);
					i++;
				}
			}
		}
	
		return opt;
	}
	
	/*
	 * Consulta a situa��o da disciplina optativa passada por par�metro
	 */
	
	public String situacaoOptativa(String optativa) {
		if(!verificaOptativa(optativa) && !optativa.startsWith("OPT"))
			return "N�O � ELETIVA";
		
		Map<String, String> opt = ultimaSituacaoOptativa();
		
		if(!opt.containsKey(optativa))
			return "N�O REALIZADO";
		
		return opt.get(optativa);
	}
	
	/*
	 * Verifica se a disciplina passada por par�metro � eletiva
	 */
	
	public boolean verificaEletiva(String disciplina) {
		
		// Disciplinas que come�am com o prefixo "TIN" s�o obrigat�rias
		if(disciplina.startsWith("TIN"))
			return false;
		
		// Disciplinas obrigat�rias de BSI, da �rea matem�tica e de letras.
		switch(disciplina) {
			case "HTD0058": return false;
			case "TME0015": return false;
			case "TME0112": return false;
			case "TME0113": return false;
			case "TME0114": return false;
			case "TME0115": return false;
			case "TME0101": return false;
		}
		
		return true;
	}
	
	/*
	 * Monta o hist�rico das disciplinas eletivas
	 */
	public Map<String, String> ultimaSituacaoEletiva(){
		Map<String, String> disc = ultimaSituacaoDisciplinas();
		Map<String, String> elet = new HashMap<String, String>();
		int i = 1;
		
		// Verifica primeiro as eletivas que foram realizadas e vencidas, uma vez que aprova��o tem prioridade sobre reprova��o.
		// Encontrando, joga em um HashMap, que � limitado ao tamanho de 4, que � o total de eletivas da grade curricular 
		for(Map.Entry<String, String> entry : disc.entrySet()) {
			String eletiva = entry.getKey();
			String situacao = entry.getValue();
			if(verificaEletiva(eletiva) && situacao == "VENCIDO") {
				if(elet.size() < 4) {
					elet.put("ELETIVA_0"+i,situacao);
					i++;
				}
			}
		}
		
		// Verifica primeiro as eletivas que foram realizadas e houve reprova��o.
		// Encontrando, joga em um HashMap, que � limitado ao tamanho de 4, que � o total de eletivas da grade curricular 
		for(Map.Entry<String, String> entry : disc.entrySet()) {
			String eletiva = entry.getKey();
			String situacao = entry.getValue();
			if(verificaEletiva(eletiva) && situacao != "VENCIDO") {
				if(elet.size() < 4) {
					elet.put("ELETIVA_0"+i,situacao);
					//System.out.println("ADICIONAR OPTATIVA_0"+i+ "("+ optativa + "). Com situacao: "+situacao);
					i++;
				}
			}
		}
		
		return elet;
	}
	
	/*
	 * Consulta a situa��o da disciplina eletiva
	 */
	public String situacaoEletiva(String eletiva) {
		if(!verificaEletiva(eletiva) && !eletiva.startsWith("ELE"))
			return "N�O � ELETIVA";
		
		Map<String, String> elet = ultimaSituacaoEletiva();
		
		if(!elet.containsKey(eletiva))
			return "N�O REALIZADO";
		
		return elet.get(eletiva);
	}

}
