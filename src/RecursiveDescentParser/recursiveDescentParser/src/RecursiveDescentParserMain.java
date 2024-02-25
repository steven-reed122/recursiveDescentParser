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
                    String token = tokenIterator.next();
                    System.out.println(token);
                    parser.expr();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }

        
    }
	//This function will parse text from the file and print out the tokens
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
                    if (checkIsProcedure(line, index)) {
                        index += 9; // Skip 'procedure'
                        token = "[PROCEDURE]";
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
                    } else if (checkIsEnd(line, index)) {
                        index += 3; // Skip 'end'
                        token = "[END]";
                    } else {
                        token = handleIdent();
                    }
                    break;
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
				case ';': index++; token = "[SEMI]"; break;
				case '\t': break;

                // Continue with other cases...
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
        private boolean checkIsProcedure(String line, int i) {
            if(line.length() > i + 8)
            {
                char ch2 = line.charAt(i+1);
                char ch3 = line.charAt(i+2);
                char ch4 = line.charAt(i+3);
                char ch5 = line.charAt(i+4);
                char ch6 = line.charAt(i+5);
                char ch7 = line.charAt(i+6);
                char ch8 = line.charAt(i+7);
                char ch9 = line.charAt(i+8);
                if(ch2 == 'r' && ch3 == 'o' && ch4 == 'c' && ch5 == 'e'
                        && ch6 == 'd' && ch7 == 'u' && ch8 == 'r' && ch9 =='e') {
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
        private boolean checkIsEnd(String line, int i) {
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
                System.out.println(nextToken);
                lineNum++;
            } else {
                nextToken = null; // or handle end of tokens
            }
        }
    
        private void error() {
            System.err.println("Error at line "+ lineNum);
            System.exit(1);
        }
    
        void expr() {
            System.out.println("Enter <expr>");
            System.out.println(nextToken);
            term();
            while (nextToken == "[ADD_OP]" || nextToken == "[SUB_OP]"){
                lex();
                term();
            }
            System.out.println("Exit <expr>");
        }
    
        private void term() {
            System.out.println("Enter <term>");
            System.out.println(nextToken);
            factor();
            while (nextToken == "[MUL_OP]" || nextToken == "[DIV_OP]"){
            lex();
            factor();
            }
            System.out.println("Exit <term>");
        }
    
        private void factor() {
            System.out.println("Enter <factor>");
            System.out.println(nextToken);  
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
