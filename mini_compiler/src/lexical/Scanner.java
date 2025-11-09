package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import util.TokenType;

public class Scanner {
	private int state;
	private char[] sourceCode;
	private int pos;
	private int line = 1;
	private int column = 0;

	public Scanner(String filename) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			sourceCode = content.toCharArray();
			pos = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final java.util.Set<String> KEYWORDS = java.util.Set.of(
			"int", "float", "print", "if", "else"
	);

	public Token nextToken() {
		char currentChar;
		String content = "";
		state = 0;

		while (true) {
			if (isEoF()) {
				return null;
			}
			currentChar = nextChar();

			switch (state) {
				case 0:
					// Questão 8: Comentários

					if (currentChar == '#') {
						while (!isEoF() && currentChar != '\n' && currentChar != '\r') {
							currentChar = nextChar();
						}
						continue; // ignora o comentário
					}

					// comentário de varias linhas (/* ... */)
					if (currentChar == '/' && proximoCharSemConsumir() == '*') {
						nextChar(); // consome '*'
						boolean fechado = false;
						while (!isEoF()) {
							currentChar = nextChar();
							if (currentChar == '*' && proximoCharSemConsumir() == '/') {
								nextChar(); // consome '/'
								fechado = true;
								break;
							}
						}
						if (!fechado) {
							lexicalError("comentário de múltiplas linhas não fechado");
						}
						continue; // ignora comentário e segue
					}
					// ignorar espaços, tabulações e quebras de linha
					if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
						break;  // só ignora e volta para o while(true)
					}
					// questão 3/4: = vs ==
					if (currentChar == '=') {
						if (proximoCharSemConsumir() == '=') {
							nextChar(); // consome o segundo '='
							return new Token(TokenType.REL_OPERATOR, "==");
						}
						return new Token(TokenType.ASSIGNMENT, "=");
					}

					// questão 4: Operadores relacionais
					if (currentChar == '>' || currentChar == '<' || currentChar == '!') {
						if (proximoCharSemConsumir() == '=') {
							char first = currentChar;
							nextChar(); // consome o '='
							return new Token(TokenType.REL_OPERATOR, "" + first + "=");
						} else {
							// '!' sozinho não é válido
							if (currentChar == '!') {
								lexicalError("símbolo inválido '!'");
							}
							// retorna > ou <
							return new Token(TokenType.REL_OPERATOR, String.valueOf(currentChar));
						}
					}

					// Operadores matemáticos: + - * /
					if (isMathOperator(currentChar)) {
						return new Token(TokenType.MATH_OPERATOR, String.valueOf(currentChar));
					}

					// Parênteses
					if (currentChar == '(') {
						return new Token(TokenType.LPAREN, "(");
					}
					if (currentChar == ')') {
						return new Token(TokenType.RPAREN, ")");
					}

					//  Questão 6: início de NUMBER
					if (isDigit(currentChar) || (currentChar == '.' && isDigit(proximoCharSemConsumir()))) {
						content = "" + currentChar;
						state = 3; // ler o número no case 3
						break;
					}

					// identificador: (a-z | A-Z | _)(a-z | A-Z | _ | 0-9)*
					if (isLetter(currentChar) || currentChar == '_') {
						content += currentChar;
						state = 1;
						break;
					}
					// nada: caractere inválido
					lexicalError("caractere não permitido: '" + currentChar + "'");

				case 1:
					if (isLetter(currentChar) || isDigit(currentChar) || currentChar == '_') {
						content += currentChar;
						break;           // permanece no state 1
					} else {
						state = 2;       // finaliza identificador/keyword
					}

				case 2:
					back();
					if (KEYWORDS.contains(content)) {
						return new Token(TokenType.KEYWORD, content);
					}
					return new Token(TokenType.IDENTIFIER, content);

				case 3: // NUMBER
					// se o currentChar é dígito, acumula e continua no estado 3
					if (isDigit(currentChar)) {
						content += currentChar;
						state = 3;
						break;
					}

					// se o currentChar é '.',
					// e somente se houver dígito após o ponto
					if (currentChar == '.') {
						if (content.indexOf('.') == -1) {
							if (isDigit(proximoCharSemConsumir())) {
								content += currentChar; // adiciona o '.'
								state = 3;
								break;
							} else {
								lexicalError("número inválido (ponto sem dígitos)");
							}
						} else {
							// segundo ponto: ERRO
							lexicalError("número inválido (mais de um ponto decimal)");
						}
					}


					// chegou aqui currentChar n faz parte do número
					// se for letra ou '_' = ERRO
					if (isLetter(currentChar) || currentChar == '_') {
						lexicalError("número inválido (seguido de letra sem separador)");
					}

					// se n
					state = 4;
					break;

				case 4: //  parte final NUMBER
					back(); // devolve o currentChar que não pertence ao número
					if (content.endsWith(".")) {
						lexicalError("número inválido (termina com ponto)");
					}
					return new Token(TokenType.NUMBER, content);

			}
		}
	}

	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isMathOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}

	private boolean isRelOperator(char c) {
		return c == '>' || c == '<' || c == '=' || c == '!';
	}

	private char nextChar() {
		char c = sourceCode[pos++];
		if (c == '\n') {
			line++;
			column = 0;
		} else {
			column++;
		}
		return c;
	}
	// Q9 - tratamento de erros (linha e coluna)
	private void lexicalError(String message) {
		throw new RuntimeException("Erro Léxico [linha " + line + ", coluna " + column + "]: " + message);
	}

	private void back() {
		pos--;
	}

	private boolean isEoF() {
		return pos >= sourceCode.length;
	}

	// verificar o proximo char sem consumir
	private char proximoCharSemConsumir() {
		if (isEoF()) return '\0';
		return sourceCode[pos];
	}
}
