
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Pedro Victor
 */
public class ProcessandoPalavras extends JFrame {

    /**
     * Creates new form ProcessandoPalavras
     */
    public ProcessandoPalavras() {
        initComponents();
        initComponentsExtras();
    }

    private void initComponentsExtras() {
        JLabel[] listajLabelPalavraAUX = {jLabelAbsract, jLabelAssert,
            jLabelBoolean, jLabelBreak, jLabelByte, jLabelCase,
            jLabelCatch, jLabelChar, jLabelClass, jLabelConst,
            jLabelContinue, jLabelDefault, jLabelDo, jLabelDouble, jLabelElse, 
            jLabelEnum, jLabelExtends, jLabelFinal, jLabelFinally, jLabelFloat,
            jLabelFor, jLabelGoto, jLabelIf, jLabelImplements, jLabelImport, 
            jLabelInstanceOf, jLabelInt, jLabelInterface, jLabelLong, 
            jLabelNative, jLabelNew, jLabelPackage, jLabelPrivate, jLabelProtected,
            jLabelPublic, jLabelReturn, jLabelShort, jLabelStatic, jLabelStrictfp, 
            jLabelSuper, jLabelSwitch, jLabelSynchronized, jLabelThis, jLabelThrow,
            jLabelThrows, jLabelTransient, jLabelTry, jLabelVoid, jLabelVolative, jLabelWhile};
            listajLabelPalavra = listajLabelPalavraAUX.clone();
            
            JLabel[] listajLabelCountAUX = {jLabelCount_Abstract, jLabelCount_Assert,
            jLabelCount_Boolean, jLabelCount_Break, jLabelCount_Byte,
            jLabelCount_Case, jLabelCount_Catch, jLabelCount_Char,
            jLabelCount_Class, jLabelCount_Const, jLabelCount_Continue,
            jLabelCount_Default, jLabelCount_Do, jLabelCount_Double,
            jLabelCount_Else, jLabelCount_Enum, jLabelCount_Extends,
            jLabelCount_Final, jLabelCount_Finally, jLabelCount_Float,
            jLabelCount_For, jLabelCount_Goto, jLabelCount_If,
            jLabelCount_Implements, jLabelCount_Import,
            jLabelCount_InstanceOf, jLabelCount_Int, jLabelCount_Interface,
            jLabelCount_Long, jLabelCount_Native, jLabelCount_New,
            jLabelCount_Package, jLabelCount_Private,
            jLabelCount_Protected, jLabelCount_Public, jLabelCount_Return,
            jLabelCount_Short, jLabelCount_Static, jLabelCount_Strictfp,
            jLabelCount_Super, jLabelCount_Switch,
            jLabelCount_Synchronized, jLabelCount_This, jLabelCount_Throw,
            jLabelCount_Throws, jLabelCount_Transient, jLabelCount_Try,
            jLabelCount_Void, jLabelCount_Volative, jLabelCount_While};
            listajLabelCount = listajLabelCountAUX.clone();
    }

    protected void iniciarAnalise(JCheckBox[] listaCheckBox, int quantidadeThreads, File diretorio) {
        List<String> palavrasParaPesquisar = new ArrayList<String>();
        for (JCheckBox jCheckBox : listaCheckBox) {
            if(jCheckBox.isSelected()){
                palavrasParaPesquisar.add(jCheckBox.getText());
            }
        }
        mapaBoxToLabel = new HashMap<JCheckBox, JLabel>();
        populateMap1(mapaBoxToLabel, listaCheckBox, listajLabelPalavra);
        mapaLabelToCount = new HashMap<JLabel, JLabel>();
        populateMap2(mapaLabelToCount, listajLabelPalavra, listajLabelCount);
        main(null); //iniciar form
        
        Thread thread = new Thread(new ThreadProcesso(palavrasParaPesquisar, diretorio, mapaBoxToLabel, mapaLabelToCount)); //no diretorio atual
        thread.start();
        
        iniciarAnaliseParte2(palavrasParaPesquisar, quantidadeThreads, diretorio, mapaBoxToLabel, mapaLabelToCount);
    }                
        
    private void iniciarAnaliseParte2(List<String> palavrasParaPesquisar, int quantidadeThreads, File diretorio, Map<JCheckBox, JLabel> mapaBoxToLabel1, Map<JLabel, JLabel> mapaLabelToCount1){
        File[] diretoriosList = listarSubdiretorios(diretorio);
        int numSubdir = diretoriosList.length;
        System.out.println("Numero de subdiretorios : " + numSubdir);
        if (numSubdir > 0) {
            System.out.println("São eles:");
            for (File dir : diretoriosList) {
                System.out.println("- " + dir.getName());
                Thread thread = new Thread(new ThreadProcesso(palavrasParaPesquisar, dir, mapaBoxToLabel1, mapaLabelToCount1));
                thread.start();
                iniciarAnaliseParte2(palavrasParaPesquisar, quantidadeThreads, dir, mapaBoxToLabel1, mapaLabelToCount1);
                //pesquisaPalavras(palavrasParaPesquisar, dir);
            }
        }
    }
        
        //aqui faz todo o processo de análise dos arquivos em diferentes threads.
        // - verificar todos os checkBox recebidos e ver quais estao marcados
        // - para cara box marcado, terei uma string correspondente a palavra para pesquisar
        // - em cada palavra que eu achar, incremento o label correspondente em +1
    
    private void populateMap1(Map<JCheckBox, JLabel> mapaBoxToLabel, JCheckBox[] listaCheckBox, JLabel[] listajLabelPalavra) {
        for (int i = 0; i < listaCheckBox.length; i++) {
            mapaBoxToLabel.put(listaCheckBox[i], listajLabelPalavra[i]);
        }
    }
    
    private void populateMap2(Map<JLabel, JLabel> mapaLabelToCount, JLabel[] listajLabelPalavra, JLabel[] listajLabelCount) {
        for (int i = 0; i < listajLabelPalavra.length; i++) {
            mapaLabelToCount.put(listajLabelPalavra[i], listajLabelCount[i]);
        }
    }
    
    private static File[] listarSubdiretorios(File diretorio) {
        // listando todos os subdiretorios de diretorio
        File diretoriosList[] = diretorio.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        if (diretoriosList == null) // TODO por algum motivo em alguns casos fica null essa lista
            diretoriosList = new File[0];
        return diretoriosList;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabelAbsract = new javax.swing.JLabel();
        jLabelAssert = new javax.swing.JLabel();
        jLabelBoolean = new javax.swing.JLabel();
        jLabelBreak = new javax.swing.JLabel();
        jLabelByte = new javax.swing.JLabel();
        jLabelCase = new javax.swing.JLabel();
        jLabelCatch = new javax.swing.JLabel();
        jLabelChar = new javax.swing.JLabel();
        jLabelClass = new javax.swing.JLabel();
        jLabelConst = new javax.swing.JLabel();
        jLabelContinue = new javax.swing.JLabel();
        jLabelDefault = new javax.swing.JLabel();
        jLabelDo = new javax.swing.JLabel();
        jLabelDouble = new javax.swing.JLabel();
        jLabelElse = new javax.swing.JLabel();
        jLabelEnum = new javax.swing.JLabel();
        jLabelExtends = new javax.swing.JLabel();
        jLabelFinal = new javax.swing.JLabel();
        jLabelFinally = new javax.swing.JLabel();
        jLabelFloat = new javax.swing.JLabel();
        jLabelFor = new javax.swing.JLabel();
        jLabelGoto = new javax.swing.JLabel();
        jLabelIf = new javax.swing.JLabel();
        jLabelImplements = new javax.swing.JLabel();
        jLabelImport = new javax.swing.JLabel();
        jLabelInstanceOf = new javax.swing.JLabel();
        jLabelInt = new javax.swing.JLabel();
        jLabelInterface = new javax.swing.JLabel();
        jLabelLong = new javax.swing.JLabel();
        jLabelNative = new javax.swing.JLabel();
        jLabelNew = new javax.swing.JLabel();
        jLabelPackage = new javax.swing.JLabel();
        jLabelPrivate = new javax.swing.JLabel();
        jLabelProtected = new javax.swing.JLabel();
        jLabelPublic = new javax.swing.JLabel();
        jLabelReturn = new javax.swing.JLabel();
        jLabelShort = new javax.swing.JLabel();
        jLabelStatic = new javax.swing.JLabel();
        jLabelStrictfp = new javax.swing.JLabel();
        jLabelSwitch = new javax.swing.JLabel();
        jLabelSynchronized = new javax.swing.JLabel();
        jLabelThis = new javax.swing.JLabel();
        jLabelThrow = new javax.swing.JLabel();
        jLabelThrows = new javax.swing.JLabel();
        jLabelTransient = new javax.swing.JLabel();
        jLabelTry = new javax.swing.JLabel();
        jLabelVoid = new javax.swing.JLabel();
        jLabelVolative = new javax.swing.JLabel();
        jLabelWhile = new javax.swing.JLabel();
        jLabelSuper = new javax.swing.JLabel();
        jLabelCount_Abstract = new javax.swing.JLabel();
        jLabelCount_Assert = new javax.swing.JLabel();
        jLabelCount_Byte = new javax.swing.JLabel();
        jLabelCount_Boolean = new javax.swing.JLabel();
        jLabelCount_Break = new javax.swing.JLabel();
        jLabelCount_Case = new javax.swing.JLabel();
        jLabelCount_Catch = new javax.swing.JLabel();
        jLabelCount_Char = new javax.swing.JLabel();
        jLabelCount_Class = new javax.swing.JLabel();
        jLabelCount_Const = new javax.swing.JLabel();
        jLabelCount_Continue = new javax.swing.JLabel();
        jLabelCount_Default = new javax.swing.JLabel();
        jLabelCount_Do = new javax.swing.JLabel();
        jLabelCount_Double = new javax.swing.JLabel();
        jLabelCount_Else = new javax.swing.JLabel();
        jLabelCount_Enum = new javax.swing.JLabel();
        jLabelCount_Int = new javax.swing.JLabel();
        jLabelCount_Interface = new javax.swing.JLabel();
        jLabelCount_Import = new javax.swing.JLabel();
        jLabelCount_InstanceOf = new javax.swing.JLabel();
        jLabelCount_Implements = new javax.swing.JLabel();
        jLabelCount_If = new javax.swing.JLabel();
        jLabelCount_For = new javax.swing.JLabel();
        jLabelCount_Goto = new javax.swing.JLabel();
        jLabelCount_Float = new javax.swing.JLabel();
        jLabelCount_Finally = new javax.swing.JLabel();
        jLabelCount_Final = new javax.swing.JLabel();
        jLabelCount_Extends = new javax.swing.JLabel();
        jLabelCount_Static = new javax.swing.JLabel();
        jLabelCount_Super = new javax.swing.JLabel();
        jLabelCount_Public = new javax.swing.JLabel();
        jLabelCount_Short = new javax.swing.JLabel();
        jLabelCount_Protected = new javax.swing.JLabel();
        jLabelCount_Return = new javax.swing.JLabel();
        jLabelCount_Private = new javax.swing.JLabel();
        jLabelCount_Package = new javax.swing.JLabel();
        jLabelCount_New = new javax.swing.JLabel();
        jLabelCount_Strictfp = new javax.swing.JLabel();
        jLabelCount_Synchronized = new javax.swing.JLabel();
        jLabelCount_Switch = new javax.swing.JLabel();
        jLabelCount_Native = new javax.swing.JLabel();
        jLabelCount_Long = new javax.swing.JLabel();
        jLabelCount_Volative = new javax.swing.JLabel();
        jLabelCount_Void = new javax.swing.JLabel();
        jLabelCount_While = new javax.swing.JLabel();
        jLabelCount_Try = new javax.swing.JLabel();
        jLabelCount_Transient = new javax.swing.JLabel();
        jLabelCount_Throws = new javax.swing.JLabel();
        jLabelCount_Throw = new javax.swing.JLabel();
        jLabelCount_This = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Voltar");

        jLabel1.setText("<html><b><u>Rank de palavras mais usadas...</b></us></html>");

        jLabelAbsract.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAbsract.setText("abstract :");

        jLabelAssert.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelAssert.setText("assert:");

        jLabelBoolean.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelBoolean.setText("boolean:");

        jLabelBreak.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelBreak.setText("break:");

        jLabelByte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelByte.setText("byte:");

        jLabelCase.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCase.setText("case:");

        jLabelCatch.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCatch.setText("catch:");

        jLabelChar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelChar.setText("char:");

        jLabelClass.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelClass.setText("class:");

        jLabelConst.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelConst.setText("const:");

        jLabelContinue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelContinue.setText("continue:");

        jLabelDefault.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDefault.setText("default:");

        jLabelDo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDo.setText("do:");

        jLabelDouble.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDouble.setText("double:");

        jLabelElse.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelElse.setText("else:");

        jLabelEnum.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelEnum.setText("enum:");

        jLabelExtends.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelExtends.setText("extends:");

        jLabelFinal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFinal.setText("final:");

        jLabelFinally.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFinally.setText("finally:");

        jLabelFloat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFloat.setText("float:");

        jLabelFor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFor.setText("for:");

        jLabelGoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelGoto.setText("goto:");

        jLabelIf.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelIf.setText("if:");

        jLabelImplements.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelImplements.setText("implements:");

        jLabelImport.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelImport.setText("import:");

        jLabelInstanceOf.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelInstanceOf.setText("instanceOf:");

        jLabelInt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelInt.setText("int:");

        jLabelInterface.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelInterface.setText("interface:");

        jLabelLong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLong.setText("long:");

        jLabelNative.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNative.setText("native:");

        jLabelNew.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNew.setText("new:");

        jLabelPackage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPackage.setText("package:");

        jLabelPrivate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPrivate.setText("private:");

        jLabelProtected.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelProtected.setText("protected:");

        jLabelPublic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPublic.setText("public:");

        jLabelReturn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelReturn.setText("return:");

        jLabelShort.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelShort.setText("short:");

        jLabelStatic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelStatic.setText("static:");

        jLabelStrictfp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelStrictfp.setText("strictfp:");

        jLabelSwitch.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSwitch.setText("switch:");

        jLabelSynchronized.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSynchronized.setText("synchronized:");

        jLabelThis.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelThis.setText("this:");

        jLabelThrow.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelThrow.setText("throw:");

        jLabelThrows.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelThrows.setText("throws:");

        jLabelTransient.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTransient.setText("transient:");

        jLabelTry.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTry.setText("try:");

        jLabelVoid.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVoid.setText("void:");

        jLabelVolative.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVolative.setText("volative:");

        jLabelWhile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWhile.setText("while:");

        jLabelSuper.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSuper.setText("super");

        jLabelCount_Abstract.setText("0");

        jLabelCount_Assert.setText("0");

        jLabelCount_Byte.setText("0");

        jLabelCount_Boolean.setText("0");

        jLabelCount_Break.setText("0");

        jLabelCount_Case.setText("0");

        jLabelCount_Catch.setText("0");

        jLabelCount_Char.setText("0");

        jLabelCount_Class.setText("0");

        jLabelCount_Const.setText("0");

        jLabelCount_Continue.setText("0");

        jLabelCount_Default.setText("0");

        jLabelCount_Do.setText("0");

        jLabelCount_Double.setText("0");

        jLabelCount_Else.setText("0");

        jLabelCount_Enum.setText("0");

        jLabelCount_Int.setText("0");

        jLabelCount_Interface.setText("0");

        jLabelCount_Import.setText("0");

        jLabelCount_InstanceOf.setText("0");

        jLabelCount_Implements.setText("0");

        jLabelCount_If.setText("0");

        jLabelCount_For.setText("0");

        jLabelCount_Goto.setText("0");

        jLabelCount_Float.setText("0");

        jLabelCount_Finally.setText("0");

        jLabelCount_Final.setText("0");

        jLabelCount_Extends.setText("0");

        jLabelCount_Static.setText("0");

        jLabelCount_Super.setText("0");

        jLabelCount_Public.setText("0");

        jLabelCount_Short.setText("0");

        jLabelCount_Protected.setText("0");

        jLabelCount_Return.setText("0");

        jLabelCount_Private.setText("0");

        jLabelCount_Package.setText("0");

        jLabelCount_New.setText("0");

        jLabelCount_Strictfp.setText("0");

        jLabelCount_Synchronized.setText("0");

        jLabelCount_Switch.setText("0");

        jLabelCount_Native.setText("0");

        jLabelCount_Long.setText("0");

        jLabelCount_Volative.setText("0");

        jLabelCount_Void.setText("0");

        jLabelCount_While.setText("0");

        jLabelCount_Try.setText("0");

        jLabelCount_Transient.setText("0");

        jLabelCount_Throws.setText("0");

        jLabelCount_Throw.setText("0");

        jLabelCount_This.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelAssert)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelCount_Assert, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelDouble)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelCount_Double, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelAbsract)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelCount_Abstract, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelBoolean)
                                    .addComponent(jLabelBreak)
                                    .addComponent(jLabelByte)
                                    .addComponent(jLabelCase)
                                    .addComponent(jLabelCatch)
                                    .addComponent(jLabelChar)
                                    .addComponent(jLabelClass)
                                    .addComponent(jLabelConst))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabelCount_Class, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelCount_Const, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelCount_Char, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelCount_Catch, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelCount_Case, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelCount_Byte, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabelCount_Boolean, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelCount_Break, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelContinue)
                                    .addComponent(jLabelDefault)
                                    .addComponent(jLabelDo))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelCount_Do, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_Default, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_Continue, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelFloat)
                                    .addComponent(jLabelFinally)
                                    .addComponent(jLabelFinal)))
                            .addComponent(jLabelInterface)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(jLabelExtends))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(jLabelEnum))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addComponent(jLabelElse))
                                    .addComponent(jLabelIf)
                                    .addComponent(jLabelGoto)
                                    .addComponent(jLabelFor)
                                    .addComponent(jLabelImplements)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabelImport))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelInt)
                                    .addComponent(jLabelInstanceOf))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabelCount_If, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelCount_Implements, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelCount_Goto, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelCount_For, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelCount_Float, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelCount_Finally, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelCount_Extends, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_Final, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabelCount_Else, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelCount_Interface, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_Int, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_InstanceOf, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_Import, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabelCount_Enum, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelSuper)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelSynchronized)
                                    .addComponent(jLabelSwitch)
                                    .addComponent(jLabelShort)
                                    .addComponent(jLabelStrictfp)
                                    .addComponent(jLabelStatic)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabelNew)
                                            .addGap(3, 3, 3))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabelReturn)
                                                .addComponent(jLabelPublic)
                                                .addComponent(jLabelProtected)
                                                .addComponent(jLabelPrivate)
                                                .addComponent(jLabelPackage)
                                                .addComponent(jLabelNative))
                                            .addGap(6, 6, 6)))
                                    .addComponent(jLabelLong))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelCount_Synchronized, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_Switch, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_Super, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_Strictfp, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(2, 2, 2)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabelCount_Short, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabelCount_Static, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabelCount_Return, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabelCount_Public, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabelCount_Protected, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabelCount_Private, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabelCount_New, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelCount_Package, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabelCount_Long, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelCount_Native, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelThis)
                                    .addComponent(jLabelThrow)
                                    .addComponent(jLabelThrows)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabelVolative)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabelTry)
                                                .addComponent(jLabelVoid)
                                                .addComponent(jLabelTransient))
                                            .addComponent(jLabelWhile))))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 8, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelCount_Volative, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelCount_While, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelCount_Void, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelCount_Throws, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelCount_Transient, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelCount_This, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelCount_Throw, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelCount_Try, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabelAssert, jLabelBoolean, jLabelBreak, jLabelByte, jLabelCase, jLabelCatch, jLabelChar, jLabelClass, jLabelConst, jLabelContinue, jLabelDefault, jLabelDo, jLabelDouble});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabelElse, jLabelEnum, jLabelExtends, jLabelFinal, jLabelFinally, jLabelFloat, jLabelFor, jLabelGoto, jLabelIf, jLabelImplements, jLabelImport, jLabelInstanceOf, jLabelInt, jLabelInterface});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabelLong, jLabelNative, jLabelNew, jLabelPackage, jLabelPrivate, jLabelProtected, jLabelPublic, jLabelReturn, jLabelShort, jLabelStatic, jLabelStrictfp, jLabelSuper, jLabelSwitch, jLabelSynchronized});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabelThis, jLabelThrow, jLabelThrows, jLabelTransient, jLabelTry, jLabelVoid, jLabelVolative, jLabelWhile});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelCount_Long)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelCount_Native)
                                .addGap(246, 246, 246))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabelAbsract)
                                            .addComponent(jLabelCount_Abstract))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabelAssert)
                                            .addComponent(jLabelCount_Assert))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(jLabelCount_Break))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(jLabelBoolean)
                                                    .addComponent(jLabelCount_Boolean))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelBreak)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(jLabelByte)
                                                    .addComponent(jLabelCount_Byte))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(jLabelCase)
                                                    .addComponent(jLabelCount_Case))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(jLabelCatch)
                                                    .addComponent(jLabelCount_Catch))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(jLabelChar)
                                                    .addComponent(jLabelCount_Char))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabelClass)
                                            .addComponent(jLabelCount_Class))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabelConst)
                                            .addComponent(jLabelCount_Const))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabelContinue)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelDefault))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabelCount_Continue)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelCount_Default)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabelCount_Do)
                                            .addComponent(jLabelDo))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabelDouble)
                                            .addComponent(jLabelCount_Double)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabelElse)
                                            .addComponent(jLabelCount_Else))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabelEnum)
                                            .addComponent(jLabelCount_Enum))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelExtends)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelFinal)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelFinally)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelFloat)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelFor)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelGoto)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelIf)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelImplements)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelImport)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelInstanceOf)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabelInt)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelInterface))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabelLong)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelNative)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelNew)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelPackage)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelPrivate)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelProtected)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelPublic)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelReturn))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(100, 100, 100)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(jLabelCount_Void)
                                                    .addComponent(jLabelVoid))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(jLabelCount_Volative)
                                                    .addComponent(jLabelVolative))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(jLabelCount_While)
                                                    .addComponent(jLabelWhile))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelShort)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelStatic)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelStrictfp)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelSuper)
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabelSwitch)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelSynchronized))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(40, 40, 40)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGap(20, 20, 20)
                                                        .addComponent(jLabelCount_Final))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabelCount_Extends)
                                                        .addGap(26, 26, 26)
                                                        .addComponent(jLabelCount_Finally)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabelCount_Float)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabelCount_For)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabelCount_Goto)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabelCount_If)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelCount_Implements)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelCount_Import)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelCount_InstanceOf)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelCount_Int)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabelCount_Interface))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGap(20, 20, 20)
                                                        .addComponent(jLabelCount_Package))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabelCount_New)
                                                        .addGap(26, 26, 26)
                                                        .addComponent(jLabelCount_Private)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabelCount_Protected)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabelCount_Public)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabelCount_Return)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabelCount_Short)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelCount_Static)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelCount_Strictfp)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelCount_Super)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelCount_Switch)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabelCount_Synchronized))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(20, 20, 20)
                                                .addComponent(jLabelTransient)
                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(80, 80, 80)
                                    .addComponent(jLabelCount_Try))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabelCount_This)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabelCount_Throw))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(40, 40, 40)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(20, 20, 20)
                                            .addComponent(jLabelCount_Transient))
                                        .addComponent(jLabelCount_Throws))
                                    .addGap(20, 20, 20)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelThis)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(60, 60, 60)
                                        .addComponent(jLabelTry))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabelThrow)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelThrows)))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProcessandoPalavras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProcessandoPalavras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProcessandoPalavras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProcessandoPalavras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        new ProcessandoPalavras().setVisible(true);
  
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelAbsract;
    private javax.swing.JLabel jLabelAssert;
    private javax.swing.JLabel jLabelBoolean;
    private javax.swing.JLabel jLabelBreak;
    private javax.swing.JLabel jLabelByte;
    private javax.swing.JLabel jLabelCase;
    private javax.swing.JLabel jLabelCatch;
    private javax.swing.JLabel jLabelChar;
    private javax.swing.JLabel jLabelClass;
    private javax.swing.JLabel jLabelConst;
    private javax.swing.JLabel jLabelContinue;
    private javax.swing.JLabel jLabelCount_Abstract;
    private javax.swing.JLabel jLabelCount_Assert;
    private javax.swing.JLabel jLabelCount_Boolean;
    private javax.swing.JLabel jLabelCount_Break;
    private javax.swing.JLabel jLabelCount_Byte;
    private javax.swing.JLabel jLabelCount_Case;
    private javax.swing.JLabel jLabelCount_Catch;
    private javax.swing.JLabel jLabelCount_Char;
    private javax.swing.JLabel jLabelCount_Class;
    private javax.swing.JLabel jLabelCount_Const;
    private javax.swing.JLabel jLabelCount_Continue;
    private javax.swing.JLabel jLabelCount_Default;
    private javax.swing.JLabel jLabelCount_Do;
    private javax.swing.JLabel jLabelCount_Double;
    private javax.swing.JLabel jLabelCount_Else;
    private javax.swing.JLabel jLabelCount_Enum;
    private javax.swing.JLabel jLabelCount_Extends;
    private javax.swing.JLabel jLabelCount_Final;
    private javax.swing.JLabel jLabelCount_Finally;
    private javax.swing.JLabel jLabelCount_Float;
    private javax.swing.JLabel jLabelCount_For;
    private javax.swing.JLabel jLabelCount_Goto;
    private javax.swing.JLabel jLabelCount_If;
    private javax.swing.JLabel jLabelCount_Implements;
    private javax.swing.JLabel jLabelCount_Import;
    private javax.swing.JLabel jLabelCount_InstanceOf;
    private javax.swing.JLabel jLabelCount_Int;
    private javax.swing.JLabel jLabelCount_Interface;
    private javax.swing.JLabel jLabelCount_Long;
    private javax.swing.JLabel jLabelCount_Native;
    private javax.swing.JLabel jLabelCount_New;
    private javax.swing.JLabel jLabelCount_Package;
    private javax.swing.JLabel jLabelCount_Private;
    private javax.swing.JLabel jLabelCount_Protected;
    private javax.swing.JLabel jLabelCount_Public;
    private javax.swing.JLabel jLabelCount_Return;
    private javax.swing.JLabel jLabelCount_Short;
    private javax.swing.JLabel jLabelCount_Static;
    private javax.swing.JLabel jLabelCount_Strictfp;
    private javax.swing.JLabel jLabelCount_Super;
    private javax.swing.JLabel jLabelCount_Switch;
    private javax.swing.JLabel jLabelCount_Synchronized;
    private javax.swing.JLabel jLabelCount_This;
    private javax.swing.JLabel jLabelCount_Throw;
    private javax.swing.JLabel jLabelCount_Throws;
    private javax.swing.JLabel jLabelCount_Transient;
    private javax.swing.JLabel jLabelCount_Try;
    private javax.swing.JLabel jLabelCount_Void;
    private javax.swing.JLabel jLabelCount_Volative;
    private javax.swing.JLabel jLabelCount_While;
    private javax.swing.JLabel jLabelDefault;
    private javax.swing.JLabel jLabelDo;
    private javax.swing.JLabel jLabelDouble;
    private javax.swing.JLabel jLabelElse;
    private javax.swing.JLabel jLabelEnum;
    private javax.swing.JLabel jLabelExtends;
    private javax.swing.JLabel jLabelFinal;
    private javax.swing.JLabel jLabelFinally;
    private javax.swing.JLabel jLabelFloat;
    private javax.swing.JLabel jLabelFor;
    private javax.swing.JLabel jLabelGoto;
    private javax.swing.JLabel jLabelIf;
    private javax.swing.JLabel jLabelImplements;
    private javax.swing.JLabel jLabelImport;
    private javax.swing.JLabel jLabelInstanceOf;
    private javax.swing.JLabel jLabelInt;
    private javax.swing.JLabel jLabelInterface;
    private javax.swing.JLabel jLabelLong;
    private javax.swing.JLabel jLabelNative;
    private javax.swing.JLabel jLabelNew;
    private javax.swing.JLabel jLabelPackage;
    private javax.swing.JLabel jLabelPrivate;
    private javax.swing.JLabel jLabelProtected;
    private javax.swing.JLabel jLabelPublic;
    private javax.swing.JLabel jLabelReturn;
    private javax.swing.JLabel jLabelShort;
    private javax.swing.JLabel jLabelStatic;
    private javax.swing.JLabel jLabelStrictfp;
    private javax.swing.JLabel jLabelSuper;
    private javax.swing.JLabel jLabelSwitch;
    private javax.swing.JLabel jLabelSynchronized;
    private javax.swing.JLabel jLabelThis;
    private javax.swing.JLabel jLabelThrow;
    private javax.swing.JLabel jLabelThrows;
    private javax.swing.JLabel jLabelTransient;
    private javax.swing.JLabel jLabelTry;
    private javax.swing.JLabel jLabelVoid;
    private javax.swing.JLabel jLabelVolative;
    private javax.swing.JLabel jLabelWhile;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables
    private JLabel[] listajLabelPalavra;
    private JLabel[] listajLabelCount;
    private Map<JCheckBox, JLabel> mapaBoxToLabel;
    private Map<JLabel, JLabel> mapaLabelToCount;
}
