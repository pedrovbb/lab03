
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Pedro Victor
 */
public class ThreadProcesso implements Runnable {

    private List<String> palavrasParaPesquisar;
    private File diretorio;
    private Map<JCheckBox, JLabel> mapaBoxToLabel1;
    private Map<JLabel, JLabel> mapaLabelToCount1;

    ThreadProcesso(List<String> palavrasParaPesquisar, File dir, Map<JCheckBox, JLabel> mapaBoxToLabel1, Map<JLabel, JLabel> mapaLabelToCount1) {
        this.palavrasParaPesquisar = palavrasParaPesquisar;
        this.diretorio = dir;
        this.mapaBoxToLabel1 = mapaBoxToLabel1;
        this.mapaLabelToCount1 = mapaLabelToCount1;
    }

    @Override
    public void run() {
        pesquisaPalavras(palavrasParaPesquisar, diretorio);
    }

    private void pesquisaPalavras(List<String> palavrasParaPesquisar, File diretorio) {
        File[] filesList = listarArquivos(diretorio); //lista os arquivos .java no diretorio atual
        int numArquivos = filesList.length;
        System.out.println("Numero de arquivos .java neste diretorio: " + numArquivos);
        if (numArquivos > 0) {
            for (File file : filesList) {
                leArquivo(file, palavrasParaPesquisar);
            }
        }
    }

    private static File[] listarArquivos(File diretorio) {
        // listando arquivos por filtro de extensao em uma pasta
        File filesList[] = diretorio.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".java");
            }
        });

        if (filesList == null) // TODO por algum motivo em alguns casos fica null essa lista
            filesList = new File[0];
        return filesList;
    }

//    private static File[] listarSubdiretorios(File diretorio) {
//        // listando todos os subdiretorios de diretorio
//        File diretoriosList[] = diretorio.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                return pathname.isDirectory();
//            }
//        });
//
//        if (diretoriosList == null) // TODO por algum motivo em alguns casos fica null essa lista
//            diretoriosList = new File[0];
//        return diretoriosList;
//    }
    private void leArquivo(File file, List<String> palavrasParaPesquisar) {
        //le aquivo procurando as palavras da lista, quando achar, descobre a label nome e incrementa a label count
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String linha;
            while ((linha = in.readLine()) != null) {
                String[] palavras = linha.split(" ");
                for (String palavra : palavras) {
                    if (palavrasParaPesquisar.contains(palavra.trim())) {
                        JLabel label = searchCheckBox(palavra);
                        mapaLabelToCount1.get(label).setText(String.valueOf(Integer.parseInt(mapaLabelToCount1.get(label).getText()) + 1));
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage() + "fuuuuuuuuu...");
        }
    }

    private JLabel searchCheckBox(String palavra) {
        Iterator<Entry<JCheckBox, JLabel>> it = mapaBoxToLabel1.entrySet().iterator();
        while (it.hasNext()) {
            Entry<JCheckBox, JLabel> pairs = it.next();
            if (pairs.getKey().getText().trim().equalsIgnoreCase(palavra.trim())) {
                return pairs.getValue(); //retorna a label
            }
        }
        System.out.println("Por vafor, nao entre aqui");
        return null;
    }
}
