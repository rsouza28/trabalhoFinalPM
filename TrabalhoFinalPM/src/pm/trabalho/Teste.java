/**
 * 
 */
package pm.trabalho;

import java.io.IOException;

/**
 * @author rsouza
 *
 */
public class Teste {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		/** Import imp = new Import();
		
		Map<Integer, String[]> hist = new HashMap<Integer, String[]>();
		
		hist = imp.retornaHistoricoAluno();
		
		for(Map.Entry<Integer, String[]> entry : hist.entrySet()) {
			String[] valor = entry.getValue();
			System.out.print(entry.getKey() + ": " +valor[0] + " - "+ valor[1]+"\r\n");
		}
		*/
		
		Regras regras = new Regras();
		
		System.out.print(Regras.matricula+"\r\n");
		System.out.println("CR maior que 7: " + regras.verificaCrMaior());
		System.out.println("Cursando pelo menos 3 disciplinas: " + regras.verificaDisciplinasCursando());
		System.out.println("integralização está ok: " + regras.verificaCrIntegralizacao());
		System.out.println("Deveria ser jubilado: " + regras.verificaAlunoJubilamento());
		System.out.println("Condição de se formar no prazo regular: " + regras.verificaCondicaoPrazoRegular());
		System.out.println("Aluno deve apresentar plano de integralizacao: "+regras.verificaPlanoIntegralizacao());

	}

}
