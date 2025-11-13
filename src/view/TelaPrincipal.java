package view;

import ponto.certo.UsuarioController;
import ponto.certo.PontoController;
import ponto.certo.RegistroPonto;
import javax.swing.JOptionPane;
import java.util.List;

/**
 * Tela Principal do Sistema Ponto Certo
 * Interface principal após login, com opções de registro de ponto
 * 
 * @author MaiconZ
 */
public class TelaPrincipal extends javax.swing.JFrame {

    /**
     * Construtor da tela principal
     * Inicializa componentes e atualiza interface com dados do usuário
     */
    public TelaPrincipal() {
        initComponents();
        atualizarInterface();
    }
    
    /**
     * Atualiza a interface com informações do usuário logado
     * Exibe nome do usuário, último registro e controles apropriados
     */
    private void atualizarInterface() {
        UsuarioController usuarioController = UsuarioController.getInstance();
        jLabelUsuario.setText("Usuário: " + usuarioController.getUsuarioLogado().getNome());
        
        // Verificar se é administrador
        boolean isAdmin = usuarioController.isUsuarioLogadoAdmin();
        
        // Mostrar/ocultar botões baseado no tipo de usuário
        jButtonCadastrar.setVisible(isAdmin);
        jButtonVerRegistrosAdmin.setVisible(isAdmin);
        
        // Administradores não precisam registrar entrada/saída
        jButtonEntrada.setVisible(!isAdmin);
        jButtonSaida.setVisible(!isAdmin);
        
        // Atualizar último registro (apenas para funcionários comuns)
        if (!isAdmin) {
            PontoController pontoController = PontoController.getInstance();
            RegistroPonto ultimoRegistro = pontoController.getUltimoRegistro(
                usuarioController.getUsuarioLogado().getLogin()
            );
            
            if (ultimoRegistro != null) {
                jLabelUltimoRegistro.setText("Último registro: " + ultimoRegistro.getTipo() + 
                    " em " + ultimoRegistro.getDataHoraFormatada());
            } else {
                jLabelUltimoRegistro.setText("Nenhum registro encontrado");
            }
        } else {
            jLabelUltimoRegistro.setText("Administrador - Sem registro de ponto");
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonEntrada = new javax.swing.JButton();
        jButtonSaida = new javax.swing.JButton();
        jButtonCadastrar = new javax.swing.JButton();
        jButtonSair = new javax.swing.JButton();
        jLabelUsuario = new javax.swing.JLabel();
        jLabelUltimoRegistro = new javax.swing.JLabel();
        jButtonVerRegistros = new javax.swing.JButton();
        jButtonVerRegistrosAdmin = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Ponto Certo - Sistema de Ponto");

        jPanel1.setBackground(new java.awt.Color(0, 102, 153));

        jButtonEntrada.setBackground(new java.awt.Color(0, 204, 102));
        jButtonEntrada.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButtonEntrada.setForeground(new java.awt.Color(255, 255, 255));
        jButtonEntrada.setText("Registrar Entrada");
        jButtonEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEntradaActionPerformed(evt);
            }
        });

        jButtonSaida.setBackground(new java.awt.Color(255, 51, 51));
        jButtonSaida.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButtonSaida.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSaida.setText("Registrar Saída");
        jButtonSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaidaActionPerformed(evt);
            }
        });

        jButtonCadastrar.setBackground(new java.awt.Color(255, 204, 0));
        jButtonCadastrar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonCadastrar.setForeground(new java.awt.Color(0, 0, 153));
        jButtonCadastrar.setText("Cadastrar Funcionário");
        jButtonCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCadastrarActionPerformed(evt);
            }
        });

        jButtonSair.setBackground(new java.awt.Color(102, 102, 102));
        jButtonSair.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonSair.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSair.setText("Sair");
        jButtonSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSairActionPerformed(evt);
            }
        });

        jLabelUsuario.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabelUsuario.setForeground(new java.awt.Color(255, 255, 255));
        jLabelUsuario.setText("Usuário:");

        jLabelUltimoRegistro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabelUltimoRegistro.setForeground(new java.awt.Color(255, 255, 255));
        jLabelUltimoRegistro.setText("Último registro:");

        jButtonVerRegistros.setBackground(new java.awt.Color(51, 153, 255));
        jButtonVerRegistros.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonVerRegistros.setForeground(new java.awt.Color(255, 255, 255));
        jButtonVerRegistros.setText("Ver Meus Registros");
        jButtonVerRegistros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerRegistrosActionPerformed(evt);
            }
        });

        jButtonVerRegistrosAdmin.setBackground(new java.awt.Color(0, 153, 102));
        jButtonVerRegistrosAdmin.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonVerRegistrosAdmin.setForeground(new java.awt.Color(255, 255, 255));
        jButtonVerRegistrosAdmin.setText("Ver Registros Funcionários");
        jButtonVerRegistrosAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerRegistrosAdminActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Light", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 51));
        jLabel1.setText("Sistema de Ponto");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonSaida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonVerRegistros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonVerRegistrosAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonCadastrar, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(jButtonSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelUsuario)
                            .addComponent(jLabel1)
                            .addComponent(jLabelUltimoRegistro))))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabelUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelUltimoRegistro)
                .addGap(30, 30, 30)
                .addComponent(jButtonEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonVerRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonVerRegistrosAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonCadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonSair, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Evento do botão Registrar Entrada
     * Registra ponto de entrada do funcionário
     */
    private void jButtonEntradaActionPerformed(java.awt.event.ActionEvent evt) {
        PontoController pontoController = PontoController.getInstance();
        UsuarioController usuarioController = UsuarioController.getInstance();
        
        pontoController.registrarEntrada(usuarioController.getUsuarioLogado().getLogin());
        JOptionPane.showMessageDialog(this, "Entrada registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        atualizarInterface();
    }

    /**
     * Evento do botão Registrar Saída
     * Registra ponto de saída do funcionário
     */
    private void jButtonSaidaActionPerformed(java.awt.event.ActionEvent evt) {
        PontoController pontoController = PontoController.getInstance();
        UsuarioController usuarioController = UsuarioController.getInstance();
        
        pontoController.registrarSaida(usuarioController.getUsuarioLogado().getLogin());
        JOptionPane.showMessageDialog(this, "Saída registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        atualizarInterface();
    }

    /**
     * Evento do botão Cadastrar Funcionário
     * Navega para tela de cadastro (apenas administradores)
     */
    private void jButtonCadastrarActionPerformed(java.awt.event.ActionEvent evt) {
        TelaCadastroFuncionario telaCadastro = new TelaCadastroFuncionario();
        telaCadastro.setVisible(true);
        this.dispose();
    }

    /**
     * Evento do botão Sair
     * Realiza logout do sistema e retorna para tela de login
     */
    private void jButtonSairActionPerformed(java.awt.event.ActionEvent evt) {
        UsuarioController.getInstance().logout();
        TelaLoguin telaLogin = new TelaLoguin();
        telaLogin.setVisible(true);
        this.dispose();
    }

    private void jButtonVerRegistrosActionPerformed(java.awt.event.ActionEvent evt) {
        System.out.println("Botão Ver Meus Registros clicado!");
        PontoController pontoController = PontoController.getInstance();
        UsuarioController usuarioController = UsuarioController.getInstance();
        
        try {
            List<RegistroPonto> registros = pontoController.getRegistrosUsuario(
                usuarioController.getUsuarioLogado().getLogin()
            );
            
            System.out.println("Registros encontrados: " + registros.size());
            
            if (registros.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum registro encontrado!", "Informação", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Seus Registros:\n\n");
                for (RegistroPonto registro : registros) {
                    sb.append(registro.getTipo()).append(" - ")
                      .append(registro.getDataHoraFormatada()).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Registros", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar registros: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar registros: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Evento do botão Ver Registros Funcionários (apenas administradores)
     * Abre tela com registros de todos os funcionários em formato de tabela
     */
    private void jButtonVerRegistrosAdminActionPerformed(java.awt.event.ActionEvent evt) {
        System.out.println("Botão Ver Registros Funcionários clicado!");
        try {
            System.out.println("Criando TelaRegistrosFuncionarios...");
            TelaRegistrosFuncionarios telaRegistros = new TelaRegistrosFuncionarios();
            System.out.println("TelaRegistrosFuncionarios criada com sucesso!");
            
            System.out.println("Tornando tela visível...");
            telaRegistros.setVisible(true);
            System.out.println("Tela visível!");
            
            System.out.println("Fechando tela atual...");
            this.dispose();
            System.out.println("Tela atual fechada!");
        } catch (Exception e) {
            System.out.println("ERRO DETALHADO ao abrir tela de registros:");
            System.out.println("Mensagem: " + e.getMessage());
            System.out.println("Classe do erro: " + e.getClass().getSimpleName());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao abrir tela de registros: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButtonCadastrar;
    private javax.swing.JButton jButtonEntrada;
    private javax.swing.JButton jButtonSair;
    private javax.swing.JButton jButtonSaida;
    private javax.swing.JButton jButtonVerRegistros;
    private javax.swing.JButton jButtonVerRegistrosAdmin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelUltimoRegistro;
    private javax.swing.JLabel jLabelUsuario;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration                   
}