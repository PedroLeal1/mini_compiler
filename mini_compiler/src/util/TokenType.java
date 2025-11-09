package util;

public enum TokenType {
	// Identificadores e números
	IDENTIFIER,
	NUMBER,

	// Operadores
	MATH_OPERATOR,   // +, -, *, /
	REL_OPERATOR,    // >, <, >=, <=, ==, !=
	ASSIGNMENT,      // =

	// Delimitadores
	LPAREN,          // (
	RPAREN,          // )
	COLON,           // :
	SEMICOLON,       // ;

	// Palavras-chave
	KEYWORD,

	// Fim de arquivo
	EOF
}