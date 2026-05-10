import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Fila de pedidos aguardando processamento */
    static Fila<Pedido> filaPedidos = new Fila<>();
    /** Pilha de produtos mais recentemente pedidos */
    static Pilha<Produto> pilhaProdutosRecentes = new Pilha<>();
        
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null 
     */
    static Produto localizarProduto() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].hashCode() == idProduto) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        return produto;   
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	String descricao;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.equals(descricao)) {
        		produto = produtosCadastrados[i];
        		localizado = true;
    		}
        }
        
        return produto;
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }
    
    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {
    	
    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	int quantidade;
    	
    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		quantidade = lerOpcao("Quantos itens desse produto serão incluídos no pedido?", Integer.class);
        		pedido.incluirProduto(produto, quantidade);
        	}
        }
    	
        return pedido;
    }
    
    /**
     * Finaliza um pedido, momento no qual ele deve ser armazenado em uma fila de pedidos.
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {
    	if (pedido == null) {
    		System.out.println("Nenhum pedido para finalizar.");
    		return;
    	}

        // Enfileira o pedido finalizado para processamento posterior
        filaPedidos.enfileirar(pedido);

    	// Para cada item do pedido, inclui o produto na pilha de produtos recentes
    	ItemDePedido[] itens = pedido.getItensDoPedido();
    	for (int i = 0; i < itens.length; i++) {
    		if (itens[i] != null) {
    			pilhaProdutosRecentes.empilhar(itens[i].getProduto());
    		}
    	}

        System.out.println("Pedido finalizado e colocado na fila de processamento.");
    }

    /**
     * Processa todos os pedidos finalizados, gravando-os em arquivo ao encerrar a aplicação.
     */
    public static void salvarPedidosPendentes() {
        if (filaPedidos.vazia()) {
            System.out.println("Nenhum pedido pendente para gravar.");
            return;
        }

        try (java.io.FileWriter fw = new java.io.FileWriter("pedidos.txt", true)) {
            while (!filaPedidos.vazia()) {
                Pedido pedido = filaPedidos.desenfileirar();
                fw.write(pedido.toString() + System.lineSeparator());
            }
            System.out.println("Pedidos pendentes gravados em pedidos.txt");
        } catch (java.io.IOException e) {
            System.out.println("Erro ao gravar os pedidos em arquivo: " + e.getMessage());
        }
    }
    
    public static void listarProdutosPedidosRecentes() {
    	if (pilhaProdutosRecentes.vazia()) {
    		System.out.println("Nenhum produto recente disponível.");
    		return;
    	}

    	Integer k = lerOpcao("Quantos produtos mais recentes deseja visualizar?", Integer.class);
    	if (k == null || k <= 0) {
    		System.out.println("Número inválido.");
    		return;
    	}

    	try {
    		Pilha<Produto> topK = pilhaProdutosRecentes.subPilha(k);
    		System.out.println("Produtos mais recentes:");
    		while (!topK.vazia()) {
    			Produto p = topK.desempilhar();
    			System.out.println(p.toString());
    		}
    	} catch (IllegalArgumentException e) {
    		System.out.println("Erro: " + e.getMessage());
    	}
    }

    /** Teste rápido da classe Pilha: insere dígitos únicos da matrícula e imprime a pilha */
    static void testarPilhaMatricula() {
    	System.out.println("Digite sua matrícula (apenas dígitos):");
    	String matricula = teclado.nextLine();
    	Pilha<Integer> pilhaMat = new Pilha<>();
    	boolean[] vistos = new boolean[10];
    	for (int i = 0; i < matricula.length(); i++) {
    		char c = matricula.charAt(i);
    		if (Character.isDigit(c)) {
    			int d = c - '0';
    			if (!vistos[d]) {
    				pilhaMat.empilhar(d);
    				vistos[d] = true;
    			}
    		}
    	}

    	System.out.println("Conteúdo da pilha (do topo para baixo):");
    	while (!pilhaMat.vazia()) {
    		System.out.println(pilhaMat.desempilhar());
    	}
    }

    /** Teste preliminar da fila: insere os caracteres do primeiro e do segundo nome */
    static void testarFilaCaracteres() {
        cabecalho();
        System.out.println("Teste preliminar da fila de caracteres");

        Fila<Character> fila = new Fila<>();

        System.out.print("Digite o seu primeiro nome: ");
        String primeiroNome = teclado.nextLine().trim();
        System.out.print("Digite o seu segundo nome: ");
        String segundoNome = teclado.nextLine().trim();

        for (int i = 0; i < primeiroNome.length(); i++) {
            char caractere = primeiroNome.charAt(i);
            if (!Character.isWhitespace(caractere)) {
                fila.enfileirar(caractere);
            }
        }

        for (int i = 0; i < segundoNome.length(); i++) {
            char caractere = segundoNome.charAt(i);
            if (!Character.isWhitespace(caractere)) {
                fila.enfileirar(caractere);
            }
        }

        System.out.println("Conteúdo atual da fila:");
        fila.imprimir();

        System.out.print("Digite um caractere para contar na fila: ");
        String entrada = teclado.nextLine();
        char caractereContado = entrada.isEmpty() ? '\0' : entrada.charAt(0);
        System.out.println("Ocorrências de '" + caractereContado + "' antes de desenfileirar: " + fila.contarOcorrencias(caractereContado));

        if (!fila.vazia()) {
            Character removido = fila.desenfileirar();
            System.out.println("Primeiro caractere desenfileirado: " + removido);
        }

        System.out.println("Conteúdo da fila após um desenfileiramento:");
        fila.imprimir();
        System.out.println("Ocorrências de '" + caractereContado + "' depois de desenfileirar: " + fila.contarOcorrencias(caractereContado));
    }
    
	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);

        // Teste inicial da fila com os nomes do usuário (Tarefa 1)
        testarFilaCaracteres();
        Pedido pedido = null;
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> {
				finalizarPedido(pedido);
				pedido = null;
			}
                case 6 -> listarProdutosPedidosRecentes();
            }
            pausa();
        }while(opcao != 0);       

		salvarPedidosPendentes();

        teclado.close();    
    }
}
