/**
 * 
 */
package pm.trabalho;

import java.io.IOException;


/**
 * @author rsouza
 *
 */
public class GradeCurricular {

	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Regras michel= new Regras("C:/Users/rsouza/Documents/historico michel.pdf");
		

		System.out.print("MATRICULA: "+ michel.matricula+"\r\n");
		System.out.println("CR maior que 7: " + michel.verificaCrMaior(michel.cr));
		System.out.println("Cursando pelo menos 3 disciplinas: " + michel.verificaDisciplinasCursando());
		System.out.println("integralização está ok: " + michel.verificaCrIntegralizacao());
		System.out.println("Deveria ser jubilado: " + michel.verificaAlunoJubilamento());
		System.out.println("Condição de se formar no prazo regular: " + michel.verificaCondicaoPrazoRegular());
		System.out.println("Aluno deve apresentar plano de integralizacao: "+michel.verificaPlanoIntegralizacao());
		
		PaintSVG paintMichel = new PaintSVG();
		paintMichel.exportaGradeColorida();
		
		
		Regras rodrigo = new Regras();
		
		System.out.print("MATRICULA: "+rodrigo.matricula+"\r\n");
		System.out.println("CR maior que 7: " + rodrigo.verificaCrMaior(rodrigo.cr));
		System.out.println("Cursando pelo menos 3 disciplinas: " + rodrigo.verificaDisciplinasCursando());
		System.out.println("integralização está ok: " + rodrigo.verificaCrIntegralizacao());
		System.out.println("Deveria ser jubilado: " + rodrigo.verificaAlunoJubilamento());
		System.out.println("Condição de se formar no prazo regular: " + rodrigo.verificaCondicaoPrazoRegular());
		System.out.println("Aluno deve apresentar plano de integralizacao: "+rodrigo.verificaPlanoIntegralizacao());
		
		PaintSVG paintRodrigo = new PaintSVG();
		paintRodrigo.exportaGradeColorida();

	
		
		

	}

}
