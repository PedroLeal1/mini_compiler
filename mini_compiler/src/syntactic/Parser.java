package syntactic;

import lexical.Scanner;
import lexical.Token;
import util.TokenType;

public class Parser {

    private final Scanner scanner;
    private Token current;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        advance(); // lê o primeiro token
    }

    private void advance() {
        current = scanner.nextToken();
    }

    private void eat(TokenType expected) {
        if (current != null && current.getType() == expected) {
            advance();
        } else {
            String found = (current == null)
                    ? "null"
                    : current.getType() + " ('" + current.getText() + "')";
            throw new SyntacticException("Esperado " + expected + " mas encontrado " + found);
        }
    }

    public void programa() {
        // <PROGRAMA> ::= INICIO <DECLS> CODIGO <COMANDOS> FIMPROG

        // Espera a palavra-chave INICIO
        if (current.getText().equals("INICIO")) {
            advance();
        } else {
            throw new SyntacticException("Esperado 'INICIO', encontrado: " + current.getText());
        }

        // Depois vem as declarações
        decls();

        // Espera a palavra-chave CODIGO
        if (current.getText().equals("CODIGO")) {
            advance();
        } else {
            throw new SyntacticException("Esperado 'CODIGO', encontrado: " + current.getText());
        }

        // Parte de comandos
        comandos();

        // Espera o FIMPROG
        if (current.getText().equals("FIMPROG")) {
            advance();
        } else {
            throw new SyntacticException("Esperado 'FIMPROG', encontrado: " + current.getText());
        }

        // Verifica se acabou certinho
        if (current.getType() != TokenType.EOF) {
            throw new SyntacticException("Símbolos após o fim do programa: " + current.getText());
        }

        System.out.println("Programa sintaticamente correto!");
    }

    private void decls() {
        // <DECLS> ::= DECLS <DECLARACOES> FIMDECLS

        if (!current.getText().equals("DECLS")) {
            throw new SyntacticException("Esperado 'DECLS', encontrado: " + current.getText());
        }
        advance(); // consome DECLS

        // Pelo menos uma declaração obrigatória
        if (current.getType() != TokenType.IDENTIFIER) {
            throw new SyntacticException("Esperado identificador na primeira declaração, encontrado: " + current.getText());
        }
        declaracao();

        // Declarações adicionais começam sempre com IDENTIFIER
        while (current.getType() == TokenType.IDENTIFIER) {
            declaracao();
        }

        // Se saiu do while e não é FIMDECLS, então o erro é claro:
        if (!current.getText().equals("FIMDECLS")) {
            throw new SyntacticException("Esperado 'FIMDECLS' após declarações, encontrado: " + current.getText());
        }

        advance(); // consome FIMDECLS
    }

    private void declaracao() {
        // <DECLARACOES> ::= ID ':' <TIPO>

        if (current.getType() != TokenType.IDENTIFIER) {
            throw new SyntacticException("Esperado identificador na declaração, encontrado: " + current.getText());
        }
        advance(); // consome ID

        if (current.getType() != TokenType.COLON) {
            throw new SyntacticException("Esperado ':' após identificador, encontrado: " + current.getText());
        }
        advance(); // consome ':'

        // TIPO ::= INT | FLOAT
        if (current.getText().equals("INT") || current.getText().equals("FLOAT")) {
            advance(); // consome o tipo
        } else {
            throw new SyntacticException("Esperado tipo (INT ou FLOAT), encontrado: " + current.getText());
        }
    }


    private void comandos() {
        // <COMANDOS> ::= { <COMANDO> }
        while (!current.getText().equals("FIMPROG")) {
            comando(); // chama o método que vai decidir o tipo de comando
        }
    }

    private void comando() {
        // Decide o tipo de comando com base no token atual
        switch (current.getText()) {
            case "LEIA":
                advance(); // consome LEIA
                if (current.getType() != TokenType.IDENTIFIER) {
                    throw new SyntacticException("Esperado identificador após 'LEIA', encontrado: " + current.getText());
                }
                advance(); // consome o identificador
                break;

            case "ESCREVA":
                advance(); // consome ESCREVA
                if (current.getType() != TokenType.LPAREN) {
                    throw new SyntacticException("Esperado '(' após 'ESCREVA', encontrado: " + current.getText());
                }
                advance(); // consome '('
                if (current.getType() != TokenType.IDENTIFIER) {
                    throw new SyntacticException("Esperado identificador dentro de ESCREVA(), encontrado: " + current.getText());
                }
                advance(); // consome ID
                if (current.getType() != TokenType.RPAREN) {
                    throw new SyntacticException("Esperado ')' após identificador em ESCREVA(), encontrado: " + current.getText());
                }
                advance(); // consome ')'
                break;

            case "SE":
                advance(); // consome SE
                expressao(); // (implementaremos no próximo passo)
                if (!current.getText().equals("ENTAO")) {
                    throw new SyntacticException("Esperado 'ENTAO' após expressão do SE, encontrado: " + current.getText());
                }
                advance(); // consome ENTAO

                if (!current.getText().equals("BLOCO")) {
                    throw new SyntacticException("Esperado 'BLOCO' após ENTAO, encontrado: " + current.getText());
                }
                advance(); // consome BLOCO

                // comandos dentro do bloco
                while (!current.getText().equals("FIMBLOCO")) {
                    comando();
                }

                advance(); // consome FIMBLOCO
                break;

            default:
                // Caso contrário, deve ser uma atribuição ID = EXPRESSÃO
                if (current.getType() != TokenType.IDENTIFIER) {
                    throw new SyntacticException("Comando inválido: esperado LEIA, ESCREVA, SE ou ID, encontrado: " + current.getText());
                }
                advance(); // consome ID

                if (current.getType() != TokenType.ASSIGNMENT) {
                    throw new SyntacticException("Esperado '=' após identificador, encontrado: " + current.getText());
                }
                advance(); // consome '='

                expressao(); // (implementaremos no próximo passo)
                break;
        }
    }
    private void expressao() {
        termo();

        // Enquanto for operador matemático, relacional ou lógico, continua
        while (current.getType() == TokenType.MATH_OPERATOR ||
                current.getType() == TokenType.REL_OPERATOR ||
                (current.getType() == TokenType.KEYWORD &&
                        (current.getText().equals("E") || current.getText().equals("OU")))) {

            advance(); // consome o operador
            termo();   // lê o próximo termo
        }
    }
    private void termo() {
        if (current.getType() == TokenType.IDENTIFIER || current.getType() == TokenType.NUMBER) {
            advance(); // consome ID ou número
        } else if (current.getType() == TokenType.LPAREN) {
            advance(); // consome '('
            expressao(); // expressão dentro dos parênteses
            if (current.getType() != TokenType.RPAREN) {
                throw new SyntacticException("Esperado ')' para fechar expressão, encontrado: " + current.getText());
            }
            advance(); // consome ')'
        } else {
            throw new SyntacticException("Expressão inválida, token inesperado: " + current.getText());
        }
    }


    // Próximos passos:
    // Aqui vamos implementar os métodos da gramática:
    // programa(), blocoDeclaracoes(), declaracao(), blocoComandos(), comando(), etc.
}
