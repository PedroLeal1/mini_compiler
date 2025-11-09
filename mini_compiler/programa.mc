# === KEYWORDS (Q7)

1 a
1.0 .1
@
!
int x              # KEYWORD:int  IDENTIFIER:x
float y            # KEYWORD:float  IDENTIFIER:y
print              # KEYWORD:print
if x >= 10         # KEYWORD:if  IDENTIFIER:x  REL_OPERATOR:>=  NUMBER:10
else               # KEYWORD:else

# === IDENTIFICADORES (Q1)
a b c _z Var1      # IDENTIFIER:a  IDENTIFIER:b  IDENTIFIER:c  IDENTIFIER:_z  IDENTIFIER:Var1

# === ATRIBUIÇÃO (Q3) + NÚMEROS (Q6)
x = 123            # IDENTIFIER:x  ASSIGNMENT:=  NUMBER:123
y = .5             # IDENTIFIER:y  ASSIGNMENT:=  NUMBER:.5
a = 123.456        # IDENTIFIER:a  ASSIGNMENT:=  NUMBER:123.456

# === PARÊNTESES (Q5) + OPERADORES ARITMÉTICOS (Q2)
(a+b)*2            # LPAREN:(  IDENTIFIER:a  MATH_OPERATOR:+  IDENTIFIER:b  RPAREN:)  MATH_OPERATOR:*  NUMBER:2
x=x+1              # IDENTIFIER:x  ASSIGNMENT:=  IDENTIFIER:x  MATH_OPERATOR:+  NUMBER:1
x=x-2              # IDENTIFIER:x  ASSIGNMENT:=  IDENTIFIER:x  MATH_OPERATOR:-  NUMBER:2
x=x*3              # IDENTIFIER:x  ASSIGNMENT:=  IDENTIFIER:x  MATH_OPERATOR:*  NUMBER:3
x=x/4              # IDENTIFIER:x  ASSIGNMENT:=  IDENTIFIER:x  MATH_OPERATOR:/  NUMBER:4

# === OPERADORES RELACIONAIS (Q4)
x==10              # IDENTIFIER:x  REL_OPERATOR:==  NUMBER:10
x!=11              # IDENTIFIER:x  REL_OPERATOR:!=  NUMBER:11
x>9                # IDENTIFIER:x  REL_OPERATOR:>   NUMBER:9
x>=9               # IDENTIFIER:x  REL_OPERATOR:>=  NUMBER:9
x<20               # IDENTIFIER:x  REL_OPERATOR:<   NUMBER:20
x<=20              # IDENTIFIER:x  REL_OPERATOR:<=  NUMBER:20

# === SEM ESPAÇOS (delimitação correta)
123+45             # NUMBER:123  MATH_OPERATOR:+  NUMBER:45
6*7                # NUMBER:6    MATH_OPERATOR:*  NUMBER:7
(8+9)*.5           # LPAREN:(  NUMBER:8  MATH_OPERATOR:+  NUMBER:9  RPAREN:)  MATH_OPERATOR:*  NUMBER:.5

# === COMENTÁRIO DE BLOCO (Q8) – deve ser ignorado
/* bloco
comentário */

# === FIM
print              # KEYWORD:print

