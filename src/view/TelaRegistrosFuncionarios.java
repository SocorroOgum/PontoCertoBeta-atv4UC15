package view;

import ponto.certo.PontoController;
import ponto.certo.RegistroPonto;
import ponto.certo.Usuario;
import ponto.certo.UsuarioController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tela para administradores visualizarem registros e banco de horas dos funcionários
 * Exibe informações em formato de tabela para fácil visualização
 */
public class TelaRegistrosFuncionarios extends JFrame {

    private JTable tabelaRegistros;
    private JComboBox<String> comboFuncionarios;
    private JButton btnVoltar;
    private JButton btnFiltrar;

    // Campos de UI gerados pelo NetBeans utilizados em initComponents
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotalHoras;
    private javax.swing.JLabel lblTotalExtras;
    private javax.swing.JComboBox<String> comboMes;
    private javax.swing.JSpinner comboAno;
    
    private DefaultTableModel modeloTabela;
    private PontoController pontoController;
    private UsuarioController usuarioController;

    public TelaRegistrosFuncionarios() {
        configurarAcesso();
        initComponents();
        inicializarFiltros();
        carregarFuncionarios();
        atualizarTabela();
        setLocationRelativeTo(null);
    }
    
    /**
     * Configura acesso - apenas administradores podem acessar esta tela
     */
    private void configurarAcesso() {
        usuarioController = UsuarioController.getInstance();
        pontoController = PontoController.getInstance();
        
        if (!usuarioController.isUsuarioLogadoAdmin()) {
            JOptionPane.showMessageDialog(this, "Acesso negado! Apenas administradores podem acessar esta tela.", "Erro", JOptionPane.ERROR_MESSAGE);
            voltarParaPrincipal();
        }
    }
    
    /**
     * Carrega lista de funcionários no combo box
     */
    private void carregarFuncionarios() {
        List<Usuario> funcionarios = usuarioController.getTodosFuncionarios();

        comboFuncionarios.removeAllItems();
        comboFuncionarios.addItem("Todos os funcionários");
        
        for (Usuario funcionario : funcionarios) {
            String item = funcionario.getNome() + " (" + funcionario.getLogin() + ")";
            comboFuncionarios.addItem(item);
        }
        
        if (comboFuncionarios.getItemCount() > 1) {
            comboFuncionarios.setSelectedIndex(0);
        }
    }
    
    /**
     * Configura estrutura da tabela de registros
     */
    private void configurarTabela() {
        String[] colunas = {
            "Funcionário", "Data", "Entrada", "Saída", "Horas Trabalhadas", "Horas Extras", "Status"
        };
        
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela apenas para visualização
            }
        };
        
        tabelaRegistros.setModel(modeloTabela);
        tabelaRegistros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Configurar largura das colunas
        tabelaRegistros.getColumnModel().getColumn(0).setPreferredWidth(150); // Funcionário
        tabelaRegistros.getColumnModel().getColumn(1).setPreferredWidth(100); // Data
        tabelaRegistros.getColumnModel().getColumn(2).setPreferredWidth(80);  // Entrada
        tabelaRegistros.getColumnModel().getColumn(3).setPreferredWidth(80);  // Saída
        tabelaRegistros.getColumnModel().getColumn(4).setPreferredWidth(120); // Horas Trabalhadas
        tabelaRegistros.getColumnModel().getColumn(5).setPreferredWidth(100); // Horas Extras
        tabelaRegistros.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status
        
        // Centralizar dados
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < tabelaRegistros.getColumnCount(); i++) {
            tabelaRegistros.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Garantir que a tabela seja visível
        tabelaRegistros.setVisible(true);
    }

    /**
     * Inicializa combos de filtros (Mês com opção "Todos os meses" e Ano atual)
     */
    private void inicializarFiltros() {
        // Preenche combo de meses com opção "Todos os meses" + meses 1..12
        String[] meses = new String[] {
            "Todos os meses",
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };
        comboMes.setModel(new javax.swing.DefaultComboBoxModel<>(meses));
        comboMes.setSelectedIndex(0);

        // Configura spinner do ano com valor padrão do ano atual
        int anoAtual = java.time.LocalDate.now().getYear();
        comboAno.setModel(new javax.swing.SpinnerNumberModel(anoAtual, 2000, anoAtual + 5, 1));
    }
    
    /**
     * Atualiza tabela com registros do banco
     */
    private void atualizarTabela() {
        if (modeloTabela == null) {
            configurarTabela();
        }
        
        modeloTabela.setRowCount(0); // Limpar tabela
        
        String funcionarioSelecionado = (String) comboFuncionarios.getSelectedItem();
        int indiceMes = comboMes.getSelectedIndex();
        int anoSelecionado = (Integer) comboAno.getValue();
        
        // Construir linhas a partir de registros do controlador
        java.util.List<Object[]> registrosLinhas = new java.util.ArrayList<>();

        boolean todosMeses = indiceMes == 0; // Índice 0 => "Todos os meses"

        if (funcionarioSelecionado != null && !funcionarioSelecionado.equals("Todos os funcionários")) {
            String login = extrairLogin(funcionarioSelecionado);

            java.util.List<RegistroPonto> registrosUsuario;
            if (todosMeses) {
                registrosUsuario = pontoController.getRegistrosUsuario(login);
            } else {
                int mesSelecionado = indiceMes; // 1..12
                registrosUsuario = pontoController.getRegistrosUsuarioPorMes(login, mesSelecionado, anoSelecionado);
            }

            Map<LocalDate, java.util.List<RegistroPonto>> porDia = registrosUsuario.stream()
                    .collect(Collectors.groupingBy(r -> r.getDataHora().toLocalDate()));

            porDia.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        Object[] linha = criarLinhaTabela(entry.getKey(), entry.getValue());
                        modeloTabela.addRow(linha);
                        registrosLinhas.add(linha);
                    });
        } else {
            System.out.println("Filtrando por TODOS os usuários" + (todosMeses ? " (todos os meses)" : ""));

            java.util.List<RegistroPonto> registros;
            if (todosMeses) {
                registros = pontoController.getTodosRegistros();
            } else {
                int mesSelecionado = indiceMes; // 1..12
                registros = pontoController.getRegistrosPorMes(mesSelecionado, anoSelecionado);
            }

            Map<String, Map<LocalDate, java.util.List<RegistroPonto>>> porUsuarioEDia = registros.stream()
                    .collect(Collectors.groupingBy(RegistroPonto::getUsuarioLogin,
                            Collectors.groupingBy(r -> r.getDataHora().toLocalDate())));

            porUsuarioEDia.forEach((login, mapaPorDia) -> {
                mapaPorDia.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> {
                            Object[] linha = criarLinhaTabela(entry.getKey(), entry.getValue());
                            modeloTabela.addRow(linha);
                            registrosLinhas.add(linha);
                        });
            });
        }

        // Atualizar resumo
        atualizarResumo(registrosLinhas);
        
        // Garantir que a tabela seja visível
        tabelaRegistros.repaint();

        if (modeloTabela.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum registro encontrado para os filtros selecionados.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Cria uma linha da tabela com informações do dia
     */
    private Object[] criarLinhaTabela(LocalDate data, List<RegistroPonto> registrosDia) {
        String nomeFuncionario = "";
        String entrada = "";
        String saida = "";
        Duration horasTrabalhadas = Duration.ZERO;
        String status = "Incompleto";
        
        // Encontrar entrada e saída
        for (RegistroPonto registro : registrosDia) {
            if (registro.getTipo().equals("ENTRADA")) {
                entrada = formatarHora(registro.getDataHora());
            } else if (registro.getTipo().equals("SAIDA")) {
                saida = formatarHora(registro.getDataHora());
            }
            // Pegar nome do funcionário do primeiro registro
            if (nomeFuncionario.isEmpty()) {
                nomeFuncionario = registro.getUsuarioLogin();
            }
        }
        
        // Calcular horas trabalhadas
        horasTrabalhadas = calcularHorasDia(registrosDia);
        
        // Determinar status
        if (!entrada.isEmpty() && !saida.isEmpty()) {
            status = "Completo";
        } else if (!entrada.isEmpty()) {
            status = "Sem saída";
        }
        
        Duration horasExtras = calcularHorasExtras(horasTrabalhadas);
        
        return new Object[]{
            nomeFuncionario,
            formatarData(data),
            entrada,
            saida,
            formatarDuracao(horasTrabalhadas),
            formatarDuracao(horasExtras),
            status
        };
    }
    
    /**
     * Calcula horas trabalhadas no dia
     */
    private Duration calcularHorasDia(List<RegistroPonto> registrosDia) {
        LocalDateTime entrada = null;
        Duration total = Duration.ZERO;
        
        for (RegistroPonto registro : registrosDia) {
            if (registro.getTipo().equals("ENTRADA")) {
                entrada = registro.getDataHora();
            } else if (registro.getTipo().equals("SAIDA") && entrada != null) {
                total = total.plus(Duration.between(entrada, registro.getDataHora()));
                entrada = null;
            }
        }
        
        return total;
    }
    
    /**
     * Calcula horas extras (acima de 8 horas)
     */
    private Duration calcularHorasExtras(Duration horasTrabalhadas) {
        Duration jornadaPadrao = Duration.ofHours(8);
        if (horasTrabalhadas.compareTo(jornadaPadrao) > 0) {
            return horasTrabalhadas.minus(jornadaPadrao);
        }
        return Duration.ZERO;
    }
    
    /**
     * Atualiza resumo com totais do mês
     */
    private void atualizarResumo(List<Object[]> registros) {
        Duration totalHoras = Duration.ZERO;
        Duration totalExtras = Duration.ZERO;
        
        for (Object[] registro : registros) {
            // Somar horas trabalhadas (coluna 4)
            if (registro[4] != null) {
                String horasTrabalhadas = registro[4].toString();
                totalHoras = totalHoras.plus(parseDuration(horasTrabalhadas));
            }
            
            // Somar horas extras (coluna 5)
            if (registro[5] != null) {
                String horasExtras = registro[5].toString();
                totalExtras = totalExtras.plus(parseDuration(horasExtras));
            }
        }
        
        // Atualizar labels
        lblTotalHoras.setText(formatDuration(totalHoras));
        lblTotalExtras.setText(formatDuration(totalExtras));
        
        System.out.println("Resumo atualizado - Total Horas: " + formatDuration(totalHoras) + 
                          ", Total Extras: " + formatDuration(totalExtras));
    }
    
    /**
     * Formata data para exibição
     */
    private String formatarData(LocalDate data) {
        return String.format("%02d/%02d/%04d", data.getDayOfMonth(), data.getMonthValue(), data.getYear());
    }
    
    /**
     * Formata hora para exibição
     */
    private String formatarHora(LocalDateTime dataHora) {
        return String.format("%02d:%02d", dataHora.getHour(), dataHora.getMinute());
    }
    
    /**
     * Formata duração para exibição
     */
    private String formatarDuracao(Duration duracao) {
        long horas = duracao.toHours();
        long minutos = duracao.toMinutesPart();
        return String.format("%02dh %02dm", horas, minutos);
    }
    
    /**
     * Extrai login do texto do combo box
     */
    private String extrairLogin(String texto) {
        int inicio = texto.lastIndexOf("(") + 1;
        int fim = texto.lastIndexOf(")");
        return texto.substring(inicio, fim);
    }
    
    /**
     * Converte string de duração para Duration (HH:MM)
     */
    private Duration parseDuration(String duration) {
        if (duration == null || duration.isEmpty() || duration.equals("00:00")) {
            return Duration.ZERO;
        }
        
        try {
            String[] parts = duration.split(":");
            if (parts.length == 2) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                return Duration.ofHours(hours).plusMinutes(minutes);
            }
        } catch (Exception e) {
            System.out.println("Erro ao converter duração: " + duration);
        }
        
        return Duration.ZERO;
    }
    
    /**
     * Formata Duration para string HH:MM
     */
    private String formatDuration(Duration duration) {
        if (duration == null || duration.isZero()) {
            return "00:00";
        }
        
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * Volta para tela principal
     */
    private void voltarParaPrincipal() {
        TelaPrincipal telaPrincipal = new TelaPrincipal();
        telaPrincipal.setVisible(true);
        this.dispose();
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
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        comboFuncionarios = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        comboMes = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        comboAno = new javax.swing.JSpinner();
        btnFiltrar = new javax.swing.JButton();
        btnVoltar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaRegistros = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblTotalHoras = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblTotalExtras = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Registros de Funcionários");

        jPanel1.setBackground(new java.awt.Color(0, 102, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Registros de Funcionários");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        jLabel2.setText("Funcionário:");

        comboFuncionarios.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Mês:");

        comboMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("Ano:");

        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnVoltar.setText("Voltar");
        btnVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoltarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboFuncionarios, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboMes, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboAno, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnFiltrar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVoltar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(comboFuncionarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(comboMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(comboAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFiltrar)
                    .addComponent(btnVoltar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        // Configurar tabela corretamente
        configurarTabela();
        // Garantir que a tabela apareça dentro do scroll
        jScrollPane1.setViewportView(tabelaRegistros);
        
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumo do Mês"));

        jLabel5.setText("Total de Horas:");

        lblTotalHoras.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalHoras.setText("00:00");

        jLabel7.setText("Horas Extras:");

        lblTotalExtras.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalExtras.setText("00:00");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotalHoras)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotalExtras)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblTotalHoras)
                    .addComponent(jLabel7)
                    .addComponent(lblTotalExtras))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {
        atualizarTabela();
    }

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {
        voltarParaPrincipal();
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new TelaRegistrosFuncionarios().setVisible(true));
    }
}