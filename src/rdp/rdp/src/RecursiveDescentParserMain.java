package rdp.rdp.src;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RecursiveDescentParserMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter name of file to be parsed"); // Corrected line
        String fileName = scanner.nextLine();
        scanner.close();
        fileReader(fileName);
    }

    public static void fileReader(String fileName) {
        File file = new File(fileName);
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                tokenIterator tokenIterator = new tokenIterator(line);
                while (tokenIterator.hasNext()) {
                    String token = tokenIterator.next();
                    System.out.println(token); // Assuming you want to print the tokens
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                tokenIterator tokenIterator = new tokenIterator(line);
                System.out.println("Parsing: " + line);
                recursiveDescentParser parser = new recursiveDescentParser(tokenIterator);
                while (tokenIterator.hasNext()) {
                    parser.expr();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        
    }
	//This function will parse text from the file and return the tokens one at a time
	public static class tokenIterator implements Iterator<String> {
        private String line;
        private int index = 0;
        private String token = null;
    
        public tokenIterator(String line) {
            this.line = line;
        }
    
        @Override
        public boolean hasNext() {
            while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
                index++;
            }
            return index < line.length();
        }
    
        // This function will return the next token in the line
        // It returns the token and moves the index to the next character
        // It will throw an exception if there are no more tokens to parse

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more tokens to parse.");
            }
    
            char ch = line.charAt(index);
            switch (ch) {
                case 'i':
                    if (checkIsIf(line, index)) {
                        index += 2; // Skip 'if'
                        token = "[IF]";
                    } else if (checkIsInt(line, index)) {
                        index += 3; // Skip 'int'
                        token = "[INT]";
                    } else {
                        token = handleIdent();
                    }
                    break;
                case 'w':
                    if (checkIsWhile(line, index)) {
                        index += 5; // Skip 'while'
                        token = "[WHILE]";
                    } else {
                        token = handleIdent();
                    }
                    break;
                case 'f':
                    if (checkIsFor(line, index)) {
                        index += 3; // Skip 'for'
                        token = "[FOR]";
                    } else {
                        token = handleNum();
                    }
                    break;
                case 'p':
                    if (checkIsProgram(line, index)) {
                        index += 7; // Skip 'procedure'
                        token = "[PROGRAM]";
                    } else {
                        token = handleIdent();
                    }
                    break;
                case 'r':
                    if (checkIsReturn(line, index)) {
                        index += 6; // Skip 'return'
                        token = "[RETURN]";
                    } else {
                        token = handleIdent();
                    }
                    break;
                case 'e':
                    if (checkIsElse(line, index)) {
                        index += 4; // Skip 'else'
                        token = "[ELSE]";
                    } else if (checkIsEnd_Something(line, index)) {
                        if (checkIsEnd_If(line, index)) {
                            index += 6; // Skip 'end_if'
                            token = "[END_IF]";
                        }
                        else if (checkIsEnd_Loop(line, index)) {
                            index += 8; // Skip 'end_loop'
                            token = "[END_LOOP]";
                        } 
                        else if (checkIsEnd_Program(line, index)) {
                            index += 11; // Skip 'end_program'
                            token = "[END_PROGRAM]";
                    } 
                    else {
                        token = handleIdent();
                    }
                    break;
                }
                case 'd':
                    if (checkIsDo(line, index)) {
                        index += 2; // Skip 'do'
                        token = "[DO]";
                    } else {
                        token = handleIdent();
                    }
                    break;
                case 'b':
                    if (checkIsBreak(line, index)) {
                        index += 5; // Skip 'break'
                        token = "[BREAK]";
                    } else {
                        token = handleIdent();
                    }
                    break;
                case 'l':
                    if (checkIsLoop(line, index)) {
                        index += 4; // Skip 'end_loop'
                        token = "[LOOP]";
                    } else {
                        token = handleIdent();
                    }
                    break;
                case '=':
                    if (checkIsEEGEOrLE(line, index)) {
                        index += 2; // Skip '=='
                        token = "[EE]";
                    } else {
                        index++; // Skip '='
                        token = "[ASSIGN]";
                    }
                    break;
                case '<':
                    if (checkIsEEGEOrLE(line, index)) {
                        index += 2; // Skip '<='
                        token = "[LE]";
                    } else {
                        index++; // Skip '<'
                        token = "[LT]";
                    }
                case '>':
                    if (checkIsEEGEOrLE(line, index)) {
                        index += 2; // Skip '<='
                        token = "[GE]";
                    } else {
                        index++; // Skip '<'
                        token = "[GT]";
                    }
                    break;
                case '+':
                    if (checkIsInc(line, index)) {
                        index += 2; // Skip '++'
                        token = "[INC]";
                    } else {
                        index++; // Skip '+'
                        token = "[ADD_OP]";
                    }
                    break;
                // Add cases for other characters following the same pattern
                case '-': index++; token = "[SUB_OP]"; break;
                case '*': index++; token = "[MUL_OP]"; break;
                case '/': index++; token = "[DIV_OP]"; break;
				case '%': index++; token = "[MOD_OP]"; break;
				case '(': index++; token = "[LP]"; break;
				case ')': index++; token = "[RP]"; break;
				case '{': index++; token = "[LB]"; break;
				case '}': index++; token = "[RB]"; break;
				case '|': index++; token = "[OR]"; break;
				case '&': index++; token = "[AND]"; break;
				case '!': index++; token = "[NEG]"; break;
				case ',': index++; token = "[COMMA]"; break;
                case ':': index++; token = "[COLON]"; break;
				case ';': index++; token = "[SEMI]"; break;
				case '\t': break;

                // Cases for numbers and identifiers
                default:
                    if (Character.isDigit(ch)) {
                        token = handleNum();
                    } else if (Character.isLetter(ch)) {
                        token = handleIdent();
                    } else {
                        index++; // Skip unknown character
                        token = "[UNKNOWN]";
                    }
                    break;
            }
            if (token == null) {
                throw new IllegalStateException("Token parsing failed.");
            }
        
            return token;
        }
    
        private String handleNum() {
            while (index < line.length() && Character.isDigit(line.charAt(index))) {
                index++;
            }
            if (index < line.length() && Character.isLetter(line.charAt(index))) {
                // You can choose to throw an exception or return an error token
                // Throwing an exception will halt the parsing with an error
                throw new IllegalArgumentException("Error: Integer literal followed by a letter at position " + index);
            }
            return "[INT_CONST]";
        } 
        private String handleIdent() {
            while (index < line.length() && (Character.isLetterOrDigit(line.charAt(index)))) {
                index++;
            }
            return "[IDENT]";
        }
	

        //The following functions will check if the next token is a keyword
        //It returns true if the next token is the ketword and false otherwise
        private boolean checkIsEEGEOrLE(String line, int i) {
            if(line.length() > i + 1)
            {
                char chNext = line.charAt(i+1);
                if(chNext == '=') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsInc(String line, int i) {
            if(line.length() > i + 1)
            {
                char chNext = line.charAt(i+1);
                if(chNext == '+') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsIf(String line, int i) {
            if(line.length() > i + 1)
            {
                char chNext = line.charAt(i+1);
                if(chNext == 'f') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsFor(String line, int i) {
            if(line.length() > i + 2)
            {
                char chNext = line.charAt(i+1);
                char chNextNext = line.charAt(i+2);
                if(chNext == 'o' && chNextNext == 'r') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsWhile(String line, int i) {
            if(line.length() > i + 4)
            {
                char ch2 = line.charAt(i+1);
                char ch3 = line.charAt(i+2);
                char ch4 = line.charAt(i+3);
                char ch5 = line.charAt(i+4);
                if(ch2 == 'h' && ch3 == 'i' && ch4 == 'l' && ch5 == 'e') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsProgram(String line, int i) {
            if(line.length() > i + 6)
            {
                char ch2 = line.charAt(i+1);
                char ch3 = line.charAt(i+2);
                char ch4 = line.charAt(i+3);
                char ch5 = line.charAt(i+4);
                char ch6 = line.charAt(i+5);
                char ch7 = line.charAt(i+6);
                if(ch2 == 'r' && ch3 == 'o' && ch4 == 'g' && ch5 == 'r'
                        && ch6 == 'a' && ch7 == 'm') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsReturn(String line, int i) {
            if(line.length() > i + 5)
            {
                char ch2 = line.charAt(i+1);
                char ch3 = line.charAt(i+2);
                char ch4 = line.charAt(i+3);
                char ch5 = line.charAt(i+4);
                char ch6 = line.charAt(i+5);
                if(ch2 == 'e' && ch3 == 't' && ch4 == 'u' && ch5 == 'r' && ch6 == 'n') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsInt(String line, int i) {
            if(line.length() > i + 2)
            {
                char chNext = line.charAt(i+1);
                char chNextNext = line.charAt(i+2);
                if(chNext == 'n' && chNextNext == 't') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsElse(String line, int i) {
            if(line.length() > i + 3)
            {
                char ch2 = line.charAt(i+1);
                char ch3 = line.charAt(i+2);
                char ch4 = line.charAt(i+3);
                if(ch2 == 'l' && ch3 == 's' && ch4 == 'e') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsDo(String line, int i) {
            if(line.length() > i + 1)
            {
                char ch2 = line.charAt(i+1);
                if(ch2 == 'o') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsBreak(String line, int i) {
            if(line.length() > i + 4)
            {
                char ch2 = line.charAt(i+1);
                char ch3 = line.charAt(i+2);
                char ch4 = line.charAt(i+3);
                char ch5 = line.charAt(i+4);
                if(ch2 == 'r' && ch3 == 'e' && ch4 == 'a' && ch5 == 'k') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsEnd_Something(String line, int i) {
            if(line.length() > i + 2)
            {
                char ch2 = line.charAt(i+1);
                char ch3 = line.charAt(i+2);
                if(ch2 == 'n' && ch3 == 'd') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsEnd_If(String line, int i) {
            if(line.length() > i + 5)
            {
                char ch4 = line.charAt(i+3);
                char ch5 = line.charAt(i+4);
                char ch6 = line.charAt(i+5);
                if(ch4 == '_' && ch5 == 'i' && ch6 == 'f') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsEnd_Loop(String line, int i) {
            if(line.length() > i + 7)
            {
                char ch4 = line.charAt(i+3);
                char ch5 = line.charAt(i+4);
                char ch6 = line.charAt(i+5);
                char ch7 = line.charAt(i+6);
                char ch8 = line.charAt(i+7);
                if(ch4 == '_' && ch5 == 'l' && ch6 == 'o' && ch7 == 'o' && ch8 == 'p') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsLoop(String line, int i) {
            if(line.length() > i + 3)
            {
                char ch2 = line.charAt(i+1);
                char ch3 = line.charAt(i+2);
                char ch4 = line.charAt(i+3);
                if(ch2 == 'o' && ch3 == 'o' && ch4 == 'p') {
                    return true;
                }
            }
            return false;
        }
        private boolean checkIsEnd_Program(String line, int i) {
            if(line.length() > i + 7)
            {
                char ch4 = line.charAt(i+3);
                char ch5 = line.charAt(i+4);
                char ch6 = line.charAt(i+5);
                char ch7 = line.charAt(i+6);
                char ch8 = line.charAt(i+7);
                char ch9 = line.charAt(i+8);
                char ch10 = line.charAt(i+9);
                char ch11 = line.charAt(i+10);
                if(ch4 == '_' && ch5 == 'p' && ch6 == 'r' && ch7 == 'o' && ch8 == 'g' && ch9 == 'r' && ch10 == 'a' && ch11 == 'm') {
                    return true;
                }
            }
            return false;
        }
    }
    
    public static class recursiveDescentParser {
        private tokenIterator tokenIterator;
        private String nextToken;
        private int lineNum = 1;

        public recursiveDescentParser(tokenIterator tokenIterator) {
            this.tokenIterator = tokenIterator;
            nextToken = tokenIterator.next();
        }

        private void lex() {
            if (tokenIterator.hasNext()) {
                nextToken = tokenIterator.next();
                lineNum++;
            } else {
                nextToken = null; // or handle end of tokens
            }
        }

        private void error() {
            System.err.println("Error at line "+ lineNum);
            System.exit(1);
        }

        private void program() {
            System.out.println("Enter <program>");
            if (nextToken == "[PROGRAM]") {
                lex();

            } 
            else {
                error();
            }
            System.out.println("Exit <program>");
        }

        private void statements() {
            System.out.println("Enter <statements>");
            statement();
            while (nextToken == "[SEMI]") {
                lex();
                statement();
            }
            System.out.println("Exit <statements>");
        }

        private void statement() {
            System.out.println("Enter <statement>");
            if (nextToken == "[IDENT]") {
                lex();
                if (nextToken == "[ASSIGN]") {
                    lex();
                    expr();
                } else {
                    error();
                }
            } else if (nextToken == "[IF]") {
                lex();
                expr();
                if (nextToken == "[THEN]") {
                    lex();
                    statements();
                } else {
                    error();
                }
            } else if (nextToken == "[LOOP]") {
                lex();
                statements();
            } else {
                error();
            }
            System.out.println("Exit <statement>");
        }
    
        void expr() {
            System.out.println("Enter <expr>");
            term();
            while (nextToken == "[ADD_OP]" || nextToken == "[SUB_OP]"){
                lex();
                term();
            }
            System.out.println("Exit <expr>");
        }
    
        private void term() {
            System.out.println("Enter <term>");
            factor();
            while (nextToken == "[MUL_OP]" || nextToken == "[DIV_OP]" || nextToken == "[MOD_OP]"){
            lex();
            factor();
            }
            System.out.println("Exit <term>");
        }
    
        private void factor() {
            System.out.println("Enter <factor>");
            if (nextToken == "[IDENT]" || nextToken == "[INT_CONST]")
                lex();
            else {
                if (nextToken == "[LP]") {
                    lex();
                    expr();
                    if (nextToken == "[RP]")
                        lex();
                    else
                        error();
                }
                else
                    error();
            }
            System.out.println("Exit <factor>");
        }    
    }
    
}
