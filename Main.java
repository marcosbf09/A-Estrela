

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

class Main {
    
    static final String CONFIG_FINAL = "1 5 9 13 2 6 10 14 3 7 11 15 4 8 12 0";
    static final String[] CONFIG_FINAL_STR = {"1", "5", "9", "13", "2", "6", "10", "14", "3", "7", "11", "15", "4", "8", "12", "0"};
    static final int[] CONFIG_FINAL_INT = {1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 4, 8, 12, 0};
    
    static final HashMap<String, Integer> CONFIG_HASH = new HashMap<>();
    

    class Estado implements Comparable<Estado> {

        public int custo = 0;
        public String elementos;
        public String[] vetorString;

        public int hS;
        public int fn;
        Estado pai = null;

        Estado(String elementos, Estado pai, int custo) {
            this.custo = custo;
            this.elementos = elementos;
            this.vetorString = elementos.split(" ");
            this.pai = pai;

        }

        Estado(String elementos) {
            this.elementos = elementos;
            this.vetorString = elementos.split(" ");
        }

        @Override
        public int compareTo(Estado o) {
            if (this.fn < o.fn) {
                return -1;
            }
            if (this.fn > o.fn) {
                return 1;
            }
            return 0;
        }

    }


    protected int Heuristica1(String[] config) {
        
       int cont=0;
       for(int i = 0; i < 16; i++){
           if(Integer.parseInt(config[i]) != CONFIG_FINAL_INT[i]){
               cont++;
           }
       }
       return cont; 
    }

    protected int Heuristica2(String[] config) {
        
        int retorno = 0;
        int[] vetor = new int[16];
 
        for(int i=0; i<config.length-1; i++){      //transformando vetor de strings em vetor de int
            vetor[i] = Integer.parseInt(config[i]);
        }

        for(int l = 1; l < 16; l++){
            if(vetor[l] != vetor[l-1] + 1){
                retorno++;
            }
        }
        
        return retorno - 1; 
    }

    protected int Heuristica3(String[] config) {
        
        int distanciaRet = 0;
    	int pos = -1;
    	for (int i = 0; i < 16; i++) {
    		String currentElment = config[i];
    		try{
    			pos = Integer.parseInt(currentElment);
    		}catch(Exception ex){ continue; }
    		if (CONFIG_FINAL_INT[i] != pos) {
    			int posInicial = CONFIG_HASH.get(currentElment);
    			distanciaRet += Math.abs((posInicial % 4) - (i % 4)) + Math.abs((posInicial / 4) - (i / 4));
    		}
    	}
    	return distanciaRet;
    
    }

    

    protected int Heuristica4(Estado config) {
        Double r1 = 0.1 * Heuristica1(config.vetorString);
        Double r2 = 0.1 * Heuristica2(config.vetorString);
        Double r3 = 0.8 * Heuristica3(config.vetorString);
        Double result = (r1 + r2 + r3);
        return result.intValue();
    }

    protected int Heuristica5(Estado config) {
        int h1, h2, h3;
        h1 = Heuristica1(config.vetorString);
        h2 = Heuristica2(config.vetorString);
        h3 = Heuristica3(config.vetorString);

        return Math.max(Math.max(h1, h2), h3);
    }
    
       
    protected int getPosicaoDoZero(String[] arrStr) {
        int i = 0;
        int pos = -1;
        for (i = 0; i < arrStr.length; i++) {
            try{
    			pos = Integer.parseInt(arrStr[i]);
            }catch (Exception ex) { continue; }
            if (pos == 0) {
                return i;
            }
        }
        return - 1;
    }

    protected String getVetorPosTroca(int zeroPos, int posTroca, String[] config) {
        String[] newConfig = config.clone();
        String aux = newConfig[posTroca];
        newConfig[posTroca] = newConfig[zeroPos];
        newConfig[zeroPos] = aux;
        String ret = "";
        for (int i = 0; i < newConfig.length; i++) {
            ret += newConfig[i] + " ";
        }
        return ret.trim();
    }

    protected ArrayList<Estado> getPossibilidadesPermuta(Estado estado) {
        int posTroca;
        String[] arrConfStr = estado.vetorString;
        int numPosicoesMatriz = 15;
        int zeroPosicao = this.getPosicaoDoZero(arrConfStr);
        int custoAtual = estado.custo + 1;

        ArrayList<Estado> filhos = new ArrayList<Estado>();
        ArrayList<Integer> lastColRight = new ArrayList<Integer>();
        lastColRight.addAll(Arrays.asList(3, 7, 11, 15));

        posTroca = zeroPosicao + 1;
        if (posTroca % 4 != 0) {
            Estado est = new Estado(getVetorPosTroca(zeroPosicao,
                    posTroca, arrConfStr), estado, custoAtual);

            filhos.add(est);
        }

        posTroca = zeroPosicao - 1;
        if ((posTroca >= 0)
                && (lastColRight.indexOf(posTroca) == -1)) {
            Estado est = new Estado(getVetorPosTroca(zeroPosicao,
                    posTroca, arrConfStr), estado, custoAtual);

            filhos.add(est);
        }

        posTroca = zeroPosicao - 4;
        if (posTroca >= 0) {
            Estado est = new Estado(getVetorPosTroca(zeroPosicao,
                    posTroca, arrConfStr), estado, custoAtual);

            filhos.add(est);

        }

        //Pega o Elemento a baixo
        posTroca = zeroPosicao + 4;
        if (posTroca <= numPosicoesMatriz) {
            Estado est = new Estado(getVetorPosTroca(zeroPosicao,
                    posTroca, arrConfStr), estado, custoAtual);

            filhos.add(est);
        }

        return filhos;
    }

    protected void AEstrela(String configInicial) {
        
        HashMap<String, Estado> cnjtA = new HashMap<>();
        HashMap<String, Estado> cnjtF = new HashMap<>();
        ArrayList<Estado> cnjtSuss = new ArrayList<>();
        Estado m = new Estado(configInicial);               //estado recebe jogo inicial
        PriorityQueue<Estado> Q = new PriorityQueue<>();    //lista de prioridades

        m.hS = Heuristica3(m.vetorString);                  
        m.fn = m.hS + 0;

        while ((m != null) && (!m.elementos.equals(CONFIG_FINAL))) {

            cnjtF.put(m.elementos, m);
            cnjtA.remove(m.elementos);

            cnjtSuss = getPossibilidadesPermuta(m);

            for (Estado filho : cnjtSuss) {
                Estado cnjAEst = cnjtA.get(filho.elementos);
                boolean cnjFEst = cnjtF.containsKey(filho.elementos);

                if (cnjAEst != null && filho.custo < cnjAEst.custo) {
                    cnjtA.remove(filho.elementos);
                    if (Q.contains(filho)) {
                        Q.remove(filho);
                    }
                }

                if (cnjAEst == null && cnjFEst == false) {
                    filho.hS = Heuristica3(m.vetorString);              
                    filho.fn = filho.custo + filho.hS;
                    cnjtA.put(filho.elementos, filho);
                    Q.add(filho);
                }
            }
            m = Q.remove();
        }
        
        //System.out.println(m.custo);
    }

    public static void main(String[] args) {
        
        //Runtime rt = Runtime.getRuntime();
        //long beforeUsedMem = rt.totalMemory() - rt.freeMemory();
        //long tempoIn = System.currentTimeMillis();
        
        //Casos Moodle
        String caso9Mov = "2 1 5 9 3 6 10 13 4 7 11 14 0 8 12 15";
        String caso15Mov = "6 5 13 0 1 7 9 14 2 8 10 15 3 4 11 12";
        String caso21Mov = "2 1 10 9 3 5 11 13 4 0 6 12 7 8 15 14";
        String caso25Mov = "2 1 5 0 7 9 10 13 6 4 3 15 8 11 12 14";
        String caso39Mov = "1 5 7 0 4 6 12 10 8 2 15 9 3 14 11 13";
        String casomovdesc = "9 13 12 8 0 5 7 14 1 11 15 4 6 10 2 3";
        
        //Casos Relatorio
        String casorelatorio1 = "5 13 6 10 1 7 2 9 4 3 15 14 8 0 11 12";
        String casorelatorio2 = "2 10 11 9 3 1 0 13 4 6 7 14 5 8 12 15";
        String casorelatorio3 = "5 9 13 10 2 6 14 15 1 4 7 12 0 3 11 8";
        String casorelatorio4 = "7 11 4 5 0 6 15 8 14 1 3 13 9 12 10 2";
        String casorelatorio5 = "5 10 9 14 7 3 13 6 1 15 0 12 8 2 4 11";
        String casorelatorio6 = "0 9 3 7 1 14 6 4 2 11 12 15 13 8 10 5";
        String casorelatorio7 = "3 9 0 7 2 1 6 5 11 13 4 12 8 14 15 10";
        String casorelatorio8 = "9 6 7 4 2 1 5 12 8 3 11 0 14 15 10 13";
        String casorelatorio9 = "2 9 4 5 0 7 11 12 14 6 3 13 1 8 15 10";
        String casorelatorio10 = "7 11 5 12 9 8 6 13 2 3 4 10 14 1 15 0";


        for (int i = 0; i < 16; i++) {
            CONFIG_HASH.put(CONFIG_FINAL_STR[i], i);
        }
        
              String input= "";
              try (Scanner sc = new Scanner(System.in)) {
                  input = sc.nextLine();
              }
        
        while(input.contains("  ")){
            input = input.replaceAll("  ", " ");
        }
        
        while(input.contains("   ")){
            input = input.replaceAll("   ", " ");
        }
        Main main = new Main();
        main.AEstrela(input); 
        
        //long tempoTotal = System.currentTimeMillis() - tempoIn;
        //System.out.println("Tempo gasto: " + tempoTotal);
        
        //long afterUsedMem = rt.totalMemory() - rt.freeMemory();
        //long actualMemUsed = afterUsedMem - beforeUsedMem;
        //System.out.println("Total memory usage in Kbytes: " + (actualMemUsed / 1024));
        
    }
}


