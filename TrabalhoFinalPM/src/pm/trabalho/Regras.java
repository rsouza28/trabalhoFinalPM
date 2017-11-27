/**
 * 
 */
package pm.trabalho;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author rsouza
 *
 */
public class Regras {


	static Import imp;
	public String matricula;
	public static int prazo;
	public static int periodoIntegralizacao;
	public static int periodoAtual;
	public double cr;
	public static double notaMinimaIntegralizacao = 5.0;
	public static int totalDisciplinasCurso = 51;
	public static int qtdPeriodosRegular = 8;

	/*
	 * Construtor de que passa o histórico como parametro na declaração do método
	 */
	public Regras(String historico) {
		imp = new Import(historico);
		matricula = imp.retornaMatricula();
		prazo = imp.retornaPrazoMaximoIntegralizacao(imp.retornaAnoIngresso(matricula));
		periodoIntegralizacao = retornaPeriodoIntegralizacao(prazo);
		periodoAtual = imp.retornaPeriodoAtual();
		cr = imp.retornaCrGeral();
	}
	
	/*
	 * Construtor que passa o histórico escolhendo o arquivo através de um JFileChooser
	 */
	public Regras() {
		imp = new Import();
		matricula = imp.retornaMatricula();
		prazo = imp.retornaPrazoMaximoIntegralizacao(imp.retornaAnoIngresso(matricula));
		periodoIntegralizacao = retornaPeriodoIntegralizacao(prazo);
		periodoAtual = imp.retornaPeriodoAtual();
		cr = imp.retornaCrGeral();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Define a partir de que periodo o aluno precisa entregar o plano de integralizacão 
	 * Para 14 periodos, o aluno deve entregar o plano no periodo 12
	 * Para 12 periodos, o aluno deve entregar o plano no periodo 8
	 */
	public int retornaPeriodoIntegralizacao(int prazo) {
				
		switch (prazo){
		case 12: return 7;
		case 14: return 12;
		default: return 0;
		}
	}

	/*
	* Calcula quantas disciplinas o aluno já venceu 
	* Para isso, verifica se a disciplina possui o a situação "VENCIDO" e retorna o total
	*/  

	public int calculaDisciplinasVencidas() {
		int total = 0;

		Map<Integer, String[]> hist = imp.retornaHistoricoAluno();
		String[] disciplina;
		for(Map.Entry<Integer, String[]> entry : hist.entrySet()) {
			disciplina = entry.getValue();
			
			if(disciplina[1].equals("VENCIDO"))
				total = total + 1;
		}
		
		return total;
	}

	/* REGRAS DE VERIFICAÇÃO */
	
	/* Verifica se o aluno deve ser jubilado. 
	* Se o aluno tiver CR menor que 4.0 e se possuir quatro ou mais reprovações em uma mesma disciplina, retorna true.
	*/
	public boolean verificaAlunoJubilamento() throws IOException {
		
		Map<Integer, String[]> hist = imp.retornaHistoricoAluno();
		
		String[] valor;
		Map<String, Integer> agg = new HashMap<String, Integer>();
		// loop que agrupa as disciplinas que o aluno já foi reprovado e a frequência com que aconteceu
		// essas informações são jogadas em um hashmap
		for(int i=1; i <= hist.size();i++) {
			valor = hist.get(i);
			// Se já existir essa Key no MAP, soma mais um na frequencia de reprovação. 
			if(agg.containsKey(valor[0]) == true){
				if (valor[1].equals("REPROVADO")) {
					int freq = agg.get(valor[0]) + 1;
					agg.put(valor[0], freq );
				}
			}
			
			// Se não existir a Key no MAP, insere com 1 reprovação de frequencia.
			else {
				if (valor[1].equals("REPROVADO"))
					agg.put(valor[0], 1);
			}
		}
		
		// Loop para verificar se o aluno deve ser jubilado baseado nas disciplinas reprovadas
		// levando em conta a frequência de reprovação (4 vezes ou mais) e o cr (inferior a 4)
		for (Map.Entry<String, Integer> entry : agg.entrySet()) {
			int totalReprovacoes = entry.getValue();
			
			if (totalReprovacoes >= 4) {	
				if (cr < 4.0)
					return true;
			}	
		}
		
		return false;
	}

	/*
	* Verifica se o aluno deve apresentar plano de integralização
	* Se o periodo atual que o aluno estiver matriculado for inferior ao período de integralização, retorna falso.
	*/
	public boolean verificaPlanoIntegralizacao() {
		if (periodoAtual < periodoIntegralizacao)
			return false;
		
		return true;
	}
	
	/*
	* Verifica se o aluno tem CR maior que 5.0 nos períodos de integralização
	* Se em período de integralização possuir CR inferior a 5.0, retorna falso.
	*/
	public boolean verificaCrIntegralizacao() throws IOException{
		
		if (periodoAtual < periodoIntegralizacao)
			return false;
				
		Map<Integer, Double> notas = imp.retornaCrPeriodos();
		
		for(Map.Entry<Integer, Double> crPer : notas.entrySet()) {
			if(crPer.getKey() != periodoAtual && crPer.getKey() >= periodoIntegralizacao && crPer.getValue() < notaMinimaIntegralizacao) 
				return false;
			
		}
			
		
		return true;
	}

	/*
	* Verifica se o aluno está cursando pelo menos 3 disciplinas
	* Se total de disciplinas do curso subtraido do total de disciplinas vencidas for menor que tres, retorna true.
	* Se estiver com a situação "CURSANDO" em pelo menos 3 disciplinas, retorna true.
	*/
	public boolean verificaDisciplinasCursando() throws IOException{
		
		int disciplinasVencidas = calculaDisciplinasVencidas();
		if ((totalDisciplinasCurso - disciplinasVencidas) <= 3)
			return true;
		
		int total = 0;
		String[] valor;
		
		Map<Integer, String[]> hist = imp.retornaHistoricoAluno();
		
		// loop que gera um contador de quantas disciplinas o aluno está cursando
		for(int i=1; i<=hist.size();i++) {
			valor = hist.get(i);
			if (valor[1].equals("CURSANDO"))
					total = total + 1;
		}
		
		if (total >= 3)
			return true;

		return false;
	}
	
	/*
	 * Verifica se o aluno tem condições de se formar dentro do prazo regular
	 * Se já estiver no nono período em diante, retorna falso.
	 * Se a razão de disciplinas restantes pela quantidade de periodos restantes for maior que a razão de total de disciplinas pela quantidade de periodos regular
	 * Então retorna falso.
	*/
	public boolean verificaCondicaoPrazoRegular() throws IOException{
		if (periodoAtual >= 9)
			return false;
		
		int totalDisciplinasVencidas = calculaDisciplinasVencidas();
		int totalDisciplinasRestantes = totalDisciplinasCurso - totalDisciplinasVencidas;
		int periodosRestantes = (qtdPeriodosRegular - periodoAtual) + 1;
		
		if ( (totalDisciplinasRestantes/periodosRestantes) > Math.ceil(totalDisciplinasCurso/qtdPeriodosRegular) )
			return false;
			
		
		return true;
	}

	/*
	* Verifica se o CR do aluno é maior que 7.
	* se for maior, retorna true
	*/
	public boolean verificaCrMaior(double cr) throws IOException {
	
		if (cr >= 7.0)
			return true;
	
		return false;
	}
	
	/*
	* Verifica todas as regras desenvolvidas e retorna uma string com o resultado
	*/
	public String verificaTodasAsRegras(Regras aluno) throws IOException {
		String linha;
		linha = "MATRICULA: "+ aluno.matricula+"<br>";
		linha = linha + "CR maior que 7: " + aluno.verificaCrMaior(aluno.cr)+"<br>";
		linha = linha +"Cursando pelo menos 3 disciplinas: " + aluno.verificaDisciplinasCursando()+"<br>";
		linha = linha +"integralização está ok: " + aluno.verificaCrIntegralizacao()+"<br>";
		linha = linha + "Deveria ser jubilado: " + aluno.verificaAlunoJubilamento()+"<br>";
		linha = linha + "Condição de se formar no prazo regular: " + aluno.verificaCondicaoPrazoRegular()+"<br>";
		linha = linha + "Aluno deve apresentar plano de integralizacao: "+aluno.verificaPlanoIntegralizacao()+"<br>";
		
		return linha;
		
		
	}
}
