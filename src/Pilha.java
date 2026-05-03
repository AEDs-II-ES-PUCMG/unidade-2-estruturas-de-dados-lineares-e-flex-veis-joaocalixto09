import java.util.NoSuchElementException;

public class Pilha<E> {

	private Celula<E> topo;
	private Celula<E> fundo;

	public Pilha() {

		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela;

	}

	public boolean vazia() {
		return fundo == topo;
	}

	public void empilhar(E item) {

		topo = new Celula<E>(item, topo);
	}

	public E desempilhar() {

		E desempilhado = consultarTopo();
		topo = topo.getProximo();
		return desempilhado;

	}

	public E consultarTopo() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha!");
		}

		return topo.getItem();

	}

	/**
	 * Cria e devolve uma nova pilha contendo os primeiros numItens elementos
	 * do topo da pilha atual.
	 * 
	 * Os elementos são mantidos na mesma ordem em que estavam na pilha original.
	 * Caso a pilha atual possua menos elementos do que o valor especificado,
	 * uma exceção será lançada.
	 *
	 * @param numItens o número de itens a serem copiados da pilha original.
	 * @return uma nova instância de Pilha<E> contendo os numItens primeiros elementos.
	 * @throws IllegalArgumentException se a pilha não contém numItens elementos.
	 */
	public Pilha<E> subPilha(int numItens) {
		if (numItens < 0) {
			throw new IllegalArgumentException("numItens deve ser não-negativo");
		}
		// Colete os primeiros numItens elementos a partir do topo
		java.util.ArrayList<E> coletados = new java.util.ArrayList<>();
		Celula<E> atual = topo;
		while (atual != null && coletados.size() < numItens) {
			E item = atual.getItem();
			if (item == null) {
				// alcançamos o sentinela/fundo
				break;
			}
			coletados.add(item);
			atual = atual.getProximo();
		}
		if (coletados.size() < numItens) {
			throw new IllegalArgumentException("A pilha não contém esse número de itens");
		}
		Pilha<E> resultado = new Pilha<>();
		// Empilhar em ordem reversa dos coletados para preservar a ordem original
		for (int i = coletados.size() - 1; i >= 0; i--) {
			resultado.empilhar(coletados.get(i));
		}
		return resultado;
	}
}