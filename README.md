VIAOBRA – Sistema de Viabilidade de Obras

VIAOBRA é um sistema desktop para análise de viabilidade econômica e financeira de obras de qualquer porte ou segmento, como residenciais, industriais, infraestrutura, ferrovias, prédios, aeroportos e projetos personalizados.

O sistema foi desenvolvido para funcionar 100% offline, utilizando apenas tecnologias gratuitas, com foco em clareza, controle e análise estratégica de custos e receitas ao longo do tempo.

Objetivo do Projeto

O objetivo do VIAOBRA é permitir que engenheiros, gestores, construtores ou analistas avaliem se um projeto é financeiramente viável antes da execução, considerando:

Custos diretos e indiretos

Receitas previstas

Etapas da obra

Cenários de risco

Indicadores financeiros clássicos

Tudo isso de forma local, sem dependência de serviços externos ou licenças pagas.

Principais Funcionalidades

Cadastro de projetos de obra

Definição de prazo, impostos, contingência e taxa de desconto

Lançamento de itens de custo e receita

Suporte a itens pontuais ou recorrentes

Organização da obra por etapas (WBS) com distribuição percentual do CAPEX

Simulação de cenários com variação de custos, receitas e atrasos

Cálculo automático de indicadores financeiros:

CAPEX

OPEX

Receita total

Lucro

VPL (Valor Presente Líquido)

TIR (Taxa Interna de Retorno – aproximação)

Payback

Interface gráfica clara e objetiva

Persistência local com banco de dados SQLite

Tecnologias Utilizadas

Java 21

JavaFX 21

Maven

SQLite

JDBC

Todas as tecnologias utilizadas são gratuitas e open source.

Requisitos para Execução

JDK 21 ou superior

Maven instalado

Sistema operacional Windows, Linux ou macOS

Como Executar o Projeto

No diretório raiz do projeto, execute:

mvn clean javafx:run


O aplicativo será iniciado automaticamente.

Estrutura do Projeto

src/main/java

Lógica da aplicação

Controllers JavaFX

Modelos e serviços

src/main/resources

Arquivos FXML

Estilos CSS

pom.xml

Configuração do projeto Maven

Estado Atual do Projeto

Interface gráfica funcional

Estrutura de dados implementada

Base preparada para persistência completa

Sistema pronto para evolução

Possíveis Evoluções Futuras

Exportação de relatórios em PDF

Geração de gráficos financeiros (Curva S, fluxo de caixa)

Empacotamento como executável (.exe)

Múltiplos projetos salvos e histórico

Comparação entre cenários

Relatórios gerenciais avançados

Licença

Este projeto utiliza apenas tecnologias gratuitas e pode ser adaptado ou expandido conforme a necessidade do usuário.

<img width="1912" height="1017" alt="image" src="https://github.com/user-attachments/assets/05a1f8c3-cc29-49e6-b081-245ea1bd47fc" />
