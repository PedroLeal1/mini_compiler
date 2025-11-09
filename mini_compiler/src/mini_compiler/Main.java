// Grupo:
// Pedro Henrique Leal Vieira RGM: 43620779
// Ruy Gerôncio da Silva Neto RGM: 34152253

package mini_compiler;

import lexical.Scanner;
import syntactic.Parser;
import syntactic.SyntacticException;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner("mini_compiler/programa_ckp2_erro.txt");
		Parser parser = new Parser(sc);

		try {
			parser.programa();
		} catch (SyntacticException e) {
			System.out.println("Erro sintático: " + e.getMessage());
		}
	}
}
