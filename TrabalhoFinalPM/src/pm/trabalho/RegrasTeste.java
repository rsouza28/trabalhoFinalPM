package pm.trabalho;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import junit.framework.TestCase;

class RegrasTeste extends TestCase {
	static int periodoEsperado;
	static int disciplinasVencidas;
	static double crEsperado;
	static String matriculaEsperada;
	static String caminhoArquivo; 

	@Test

	public void testaRegrasClass() throws IOException {
		
		
		/*
		 * Verifica regras de um aluno do segundo periodo
		 * 
		 */
		
		Regras tPeriodo = new Regras("C:/Users/rsouza/Documents/teste_segundo_periodo.pdf");
		periodoEsperado = 7;
		disciplinasVencidas = 4; 
		
		/*
		 * Testa se todas as regras de premissa do projeto estao ok.
		 */
		assertTrue(tPeriodo.verificaCondicaoPrazoRegular());
		assertFalse(tPeriodo.verificaAlunoJubilamento());
		assertFalse(tPeriodo.verificaPlanoIntegralizacao());
		assertFalse(tPeriodo.verificaCrIntegralizacao());
		assertTrue(tPeriodo.verificaDisciplinasCursando());
		
		/*
		 * Testa se a fun��o verifica se o numero passado por par�metro � maior ou igual a 7 
		 */
		assertFalse(tPeriodo.verificaCrMaior(6.9));
		assertTrue(tPeriodo.verificaCrMaior(7.0));
		assertTrue(tPeriodo.verificaCrMaior(7.1));
		
		/*
		 * Testa se o per�odo de integraliza��o do aluno est� de acordo com as regras
		 * pedir integraliza��o no 7� per�odo, o aluno que tem 12 semestres (a partir de 2014)
		 * pedir integraliza��o no 12� per�odo, o aluno que tem 14 semestres (antes de 2014)
		 */
		assertEquals(periodoEsperado, tPeriodo.retornaPeriodoIntegralizacao(12)); 
		assertNotEquals(periodoEsperado, tPeriodo.retornaPeriodoIntegralizacao(14));
		
		/*
		 * Testa a quantidade de disciplinas que o aluno teste j� venceu
		 * tem 4 aprova��es no primeiro periodo
		 */
		assertEquals(disciplinasVencidas, tPeriodo.calculaDisciplinasVencidas());
		assertNotEquals(disciplinasVencidas+1, tPeriodo.calculaDisciplinasVencidas());
		
		
	}
	
	@Test
	
	public void testaImportClass() throws IOException{
		
		/*
		 * Importa um arquivo de teste, de um aluno do s�timo per�odo que j� foi aprovado em 32 disciplinas.
		 */
		caminhoArquivo = "C:/Users/rsouza/Documents/teste_setimo_periodo.pdf";
		Import tImp = new Import(caminhoArquivo);
		periodoEsperado = 7;
		disciplinasVencidas = 32; 
		
		/*
		 * Testa se o objeto import retornou nulo.
		 */
		assertNotNull(tImp);
		
		/*
		 * Testa se o programa est�o lendo o CR geral e a matr�cula corretamente
		 * o CR geral do aluno � 7.8000 e sua matricula � 20142210001 
		 */
		
		matriculaEsperada = "20142210001";
		crEsperado = 7.80000;
		
		assertEquals(crEsperado, tImp.retornaCrGeral());
		assertNotEquals(crEsperado+0.000001, tImp.retornaCrGeral());
		assertEquals(matriculaEsperada, tImp.retornaMatricula());
		assertNotEquals("11656267721", tImp.retornaMatricula());
		
		/*
		 * Testa se o ano de ingresso do aluno est� de acordo.
		 * � calculado de acordo com os 4 primeiros numeros da matr�cula do aluno
		 * A matricula do aluno neste teste � 20142210001
		 */
		assertEquals(2014, tImp.retornaAnoIngresso(matriculaEsperada));
		assertNotEquals(2015, tImp.retornaAnoIngresso(matriculaEsperada));
		
		/*
		 * Testa se o prazo maximo pra intergralizacao est� retornando ok.
		 * 12 semestres pra quem entrou em 2014 ou depois
		 * 14 semestres pra quem entrou antes de 2014
		 */
		assertEquals(12, tImp.retornaPrazoMaximoIntegralizacao(2014));
		assertEquals(14, tImp.retornaPrazoMaximoIntegralizacao(2012));
		assertNotEquals(14, tImp.retornaPrazoMaximoIntegralizacao(2014));
		
		/*
		 * Testa o hist�rico do aluno nas disciplinas.
		 * 1->TIN0107-VENCIDO
		 * 10->TME0015-VENCIDO
		 * 28->TIN0171-REPROVADO
		 */
		String[] historico = tImp.retornaHistoricoAluno().get(1);
		String disciplinaEsperada = "TIN0107";
		String situacaoEsperada = "VENCIDO";
		
		assertEquals(disciplinaEsperada, historico[0]);
		assertEquals(situacaoEsperada, historico[1]);
		
		historico = tImp.retornaHistoricoAluno().get(10);
		disciplinaEsperada = "TME0015";
		situacaoEsperada = "VENCIDO";
		
		assertEquals(disciplinaEsperada, historico[0]);
		assertEquals(situacaoEsperada, historico[1]);
		
		historico = tImp.retornaHistoricoAluno().get(28);
		disciplinaEsperada = "TIN0171";
		situacaoEsperada = "VENCIDO";
		
		assertEquals(disciplinaEsperada, historico[0]);
		assertNotEquals(situacaoEsperada, historico[1]);
		
		/*
		 * Testa o CR do aluno nos sete periodos.
		 * 1-7.2545; 2-8.1525; 3-8.2500; 4-8.2500
		 * 5-6.2500; 6-7.3500 7-0.0000
		 */
		double crPeriodo = tImp.retornaCrPeriodos().get(2);
		crEsperado = 8.1525;
		assertEquals(crEsperado, crPeriodo);
		
		crPeriodo = tImp.retornaCrPeriodos().get(7);
		crEsperado = 0.0000;
		assertEquals(crEsperado, crPeriodo);
		crEsperado = 8.2500;
		assertNotEquals(crEsperado, crPeriodo);
		
		/*
		 * O arquivo teste usado, o aluno � do s�timo per�odo. Logo, deve retornar igual quando o periodo esperado for 7 e n�o igual quando for 3 
		 */
		periodoEsperado = 7;
		assertEquals(periodoEsperado, tImp.retornaPeriodoAtual());
		periodoEsperado = 3;
		assertNotEquals(periodoEsperado, tImp.retornaPeriodoAtual());
		
		/*
		 * Importa outro arquivo de teste, do terceiro periodo.
		 * O aluno cursou a disciplina TME0101 duas vezes. Sendo reprovado na primeira e aprovado na segunda.
		 * Por isso, a �ltima situa��o dele nesta disciplina deve ser VENCIDO.
		 */
		caminhoArquivo = "C:/Users/rsouza/Documents/teste_terceiro_periodo.pdf";
		String situacao;
		tImp = new Import(caminhoArquivo);
		disciplinaEsperada = "TME0101";
		situacaoEsperada = "VENCIDO";
		situacao = tImp.ultimaSituacaoDisciplinas().get("TME0101");
		assertEquals(situacaoEsperada, situacao);
		
		assertEquals(situacaoEsperada, tImp.situacaoDiscplina("TME0101"));
		
		/*
		* Verifica se � optativa
		* TIN0107 � uma disciplina obrigat�ria
		* TME0125 � uma disciplina eletiva
		* TIN0164 e TIN0165 s�o optativas
		*/
		
		String disciplina = "TIN0107"; 
		assertFalse(tImp.verificaOptativa(disciplina));
		disciplina = "TME0125"; 
		assertFalse(tImp.verificaOptativa(disciplina));
		disciplina = "TIN0164"; 
		assertTrue(tImp.verificaOptativa(disciplina));
		disciplina = "TIN0165"; 
		assertTrue(tImp.verificaOptativa(disciplina));
		
		/*
		 * Solicita a situa��o do aluno na disciplina optativa TIN0164, que n�o foi realizada pelo aluno
		 * Testa a disciplina TME0125 que � eletiva
		 */
		disciplina = "TIN0164"; 
		assertEquals("N�O REALIZADO", tImp.situacaoOptativa(disciplina));
		
		disciplina = "TME0125"; 
		assertEquals("N�O � OPTATIVA", tImp.situacaoOptativa(disciplina));
		
		/*
		 * Testa se a disciplina � eletiva. Todas as disciplinas come�adas com TIN n�o s�o eletivas.
		 * As com o c�digo HTD0058, TME0015, TME0112, TME0113, TME0114, TME0115, TME0101 tamb�m n�o s�o eletivas
		 */
		
		disciplina = "TIN0107";
		assertFalse(tImp.verificaEletiva(disciplina));
		disciplina = "TIN0164";
		assertFalse(tImp.verificaEletiva(disciplina));
		disciplina = "TME0015";
		assertFalse(tImp.verificaEletiva(disciplina));
		disciplina = "TME0115";
		assertFalse(tImp.verificaEletiva(disciplina));
		disciplina = "TME0125";
		assertTrue(tImp.verificaEletiva(disciplina));
		disciplina = "HTD0059";
		assertTrue(tImp.verificaEletiva(disciplina));
		
		/*
		 * Testa a situa��o do aluno nas disciplinas eletivas
		 * uma � optativa, outra � obrigat�ria e a ultima n�o foi realizada
		 */
		disciplina = "TIN0107"; 
		assertEquals("N�O � ELETIVA", tImp.situacaoEletiva(disciplina));
		disciplina = "TIN0164"; 
		assertEquals("N�O � ELETIVA", tImp.situacaoEletiva(disciplina));
		disciplina = "HTD0059"; 
		assertEquals("N�O REALIZADO", tImp.situacaoEletiva(disciplina));
		
	}
	
	@Test
	public void testaPaintClass() throws IOException{
		PaintSVG tPaint = new PaintSVG(false);
		
		/*
		 * Testa se a situa��o for vencido, se vai retornar a cor verde (#00FF00)
		 */
		tPaint.alteraCor("VENCIDO");
		String corDesejada = "#00FF00";
		String corEsperada = PaintSVG.novaCor;
		assertEquals(corDesejada, corEsperada);
		assertNotEquals("#FF0000", corEsperada);
		
		/*
		 * Testa se a situa��o for reprovado, se vai retornar a cor vermelha (#000000)
		 */
		tPaint.alteraCor("REPROVADO");
		corDesejada = "#FF0000";
		corEsperada = PaintSVG.novaCor;
		assertEquals(corDesejada, corEsperada);
		assertNotEquals("#00FF00", corEsperada);
		
		/*
		 * Testa se a situa��o for cursando ou trancado, se vai retornar a cor padr�o (#FFFFFF)
		 */
		tPaint.alteraCor("CURSANDO");
		corDesejada = "#ffffff";
		corEsperada = PaintSVG.novaCor;
		assertEquals(corDesejada, corEsperada);
		assertNotEquals("#00FF00", corEsperada);
		
		tPaint.alteraCor("TRANCADO");
		corDesejada = "#ffffff";
		corEsperada = PaintSVG.novaCor;
		assertEquals(corDesejada, corEsperada);
		assertNotEquals("#FF0000", corEsperada);
		
	
	}
	
	
}
	

