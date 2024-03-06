package rdp.rdp.src;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class RecursiveDescentParserMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //System.out.println("Enter name of file to be parsed"); // Corrected line
        String fileName = "newtestfile.txt";
        fileReader(fileName);
        scanner.close();
    }

    public static void fileReader(String fileName) {
        File file = new File(fileName);
        String text = "";
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                text += line + "\n";
            }
            tokenIterator tokenIterator = new tokenIterator(text);
            Queue<String> tokens = tokenIterator.getTokens();
            Queue<String> tokensCopy = new LinkedList<>(tokens);
            while (tokens.size() > 0) {
                System.out.println(tokens.poll());
            }
            recursiveDescentParser parser = new recursiveDescentParser(tokensCopy);
            parser.program();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }   
    }

    public static class tokenIterator implements Iterator<String> {
    private String line;
    private Queue<String> tokens;
    int index = 0;
    
    public tokenIterator(String line) {
        this.line = line;
        this.tokens = new LinkedList<>();
        parseTokens();
    }
    
    private void parseTokens() {
        while (index < line.length()) {
            while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
                index++;
            }
            if (index < line.length()) {
                StringBuilder tokenBuilder = new StringBuilder();
                char ch = line.charAt(index);
                
                switch (ch) {
                    case 'i':
                        if (checkIsIf(line, index)) {
                            tokenBuilder.append("[IF]");
                            index += 2; // Skip 'if'
                        } else if (checkIsInt(line, index)) {
                            tokenBuilder.append("[INT]");
                            index += 3; // Skip 'int'
                        } else {
                            tokenBuilder.append(handleIdent());
                        }
                        break;
                    case 'p':
                        if (checkIsProgram(line, index)) {
                            tokenBuilder.append("[PROGRAM]");
                            index += 7; // Skip 'program'
                        } else {
                            tokenBuilder.append(handleIdent());
                        }
                        break;
                    case 'e':
                        if (checkIsEnd_Something(line, index)) {
                            if (checkIsEnd_If(line, index)) {
                                tokenBuilder.append("[END_IF]");
                                index += 6; // Skip 'end_if'
                            }
                            else if (checkIsEnd_Loop(line, index)) {
                                tokenBuilder.append("[END_LOOP]");
                                index += 8; // Skip 'end_loop'
                            } 
                            else if (checkIsEnd_Program(line, index)) {
                                tokenBuilder.append("[END_PROGRAM]");
                                index += 11; // Skip 'end_program'
                        } 
                        else {
                            tokenBuilder.append(handleIdent());
                        }
                        break;
                    }
                    case 'l':
                        if (checkIsLoop(line, index)) {
                            tokenBuilder.append("[LOOP]");
                            index += 4; // Skip 'end_loop'
                        } else {
                            tokenBuilder.append(handleIdent());
                        }
                        break;
                    case '=':
                        if (checkIsEEGEOrLE(line, index)) {
                            tokenBuilder.append("[EE]");
                            index += 2; // Skip '=='
                        } else {
                            tokenBuilder.append("[ASSIGN]");
                            index++; // Skip '='
                        }
                        break;
                    case '<':
                        if (checkIsEEGEOrLE(line, index)) {
                            tokenBuilder.append("[LE]");
                            index += 2; // Skip '<='
                        } else {
                            tokenBuilder.append("[LT]");
                            index++; // Skip '<'
                        }
                        break;
                    case '>':
                        if (checkIsEEGEOrLE(line, index)) {
                            tokenBuilder.append("[GE]");
                            index += 2; // Skip '<='
                        } else {
                            tokenBuilder.append("[GT]");
                            index++; // Skip '<'
                        }
                        break;
                    case '+': index++; tokenBuilder.append("[ADD_OP]"); break;
                    case '-': index++; tokenBuilder.append("[SUB_OP]"); break;
                    case '*': index++; tokenBuilder.append("[MUL_OP]"); break;
                    case '/': index++; tokenBuilder.append("[DIV_OP]"); break;
                    case '%': index++; tokenBuilder.append("[MOD_OP]"); break;
                    case '(': index++; tokenBuilder.append("[LP]"); break;
                    case ')': index++; tokenBuilder.append("[RP]"); break;
                    case '{': index++; tokenBuilder.append("[LB]"); break;
                    case '}': index++; tokenBuilder.append("[RB]"); break;
                    case '|': index++; tokenBuilder.append("[OR]"); break;
                    case '&': index++; tokenBuilder.append("[AND]"); break;
                    case '!': index++; tokenBuilder.append("[NEG]"); break;
                    case ',': index++; tokenBuilder.append("[COMMA]"); break;
                    case ':': index++; tokenBuilder.append("[COLON]"); break;
                    case ';': index++; tokenBuilder.append("[SEMI]"); break;
                    case '\t': break;
                    case '\n': break;
                    default:
                        if (Character.isDigit(ch)) {
                            tokenBuilder.append(handleNum());
                        } else if (Character.isLetter(ch)) {
                            tokenBuilder.append(handleIdent());
                        } else {
                            index++; // Skip unknown character
                            tokenBuilder.append("[UNKNOWN]");
                        }
                        break;
                }
                tokens.offer(tokenBuilder.toString());
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !tokens.isEmpty();
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more tokens to parse.");
        }
        return tokens.poll();
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
    
    public Queue<String> getTokens() {
        return tokens;
    }
}
    
    
    public static class recursiveDescentParser {
        private Queue<String> tokens;
        private String nextToken;
        private int lineNum = 0;

        public recursiveDescentParser(Queue<String> tokens) {
            this.tokens = tokens;
            nextToken = tokens.poll();
        }

        private void lex() {
            if (tokens.size() > 0) {
                nextToken = tokens.poll();
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
            if (nextToken.equals("[PROGRAM]")) {
                lineNum++;
                statements();
            } else {
                error(); // Expected PROGRAM token
            }
            System.out.println("Exit <program>");
        }
        

        private void statements() {
            System.out.println("Enter <statements>");
            while (true) {
                lex();
                if (nextToken.equals("[END_PROGRAM]")) {
                    break;
                }
                statement();
            }
            System.out.println("Exit <statements>");
        }

        private void statement() {
            System.out.println("Enter <statement>");
            lineNum++;
            if (nextToken.equals("[IDENT]")) {
                lex();
                assignment();
            } else if (nextToken.equals("[IF]")) {
                lex();
                conditional();
            } else if (nextToken.equals("[LOOP]")) {
                lex();
                loop();
            } else {
                error();
            }
            System.out.println("Exit <statement>");
        }

        private void assignment() {
            System.out.println("Enter <assignment>");
            if (nextToken.equals("[ASSIGN]")) {
                lex();
                expr();
                if(!nextToken.equals("[SEMI]")) {
                    error();
                }
            }
            else {
                error();
            }
            System.out.println("Exit <assignment>");
        }

        private void conditional() {
            System.out.println("Enter <conditional>");
            lex();
            logicalExpression();
            conditional_statements();
        }

        private void logicalExpression() {
            System.out.println("Enter <logicalExpression>");
            expr();
            if (nextToken.equals("[LT]") || nextToken.equals("[GT]") || nextToken.equals("[LE]") || 
                    nextToken.equals("[GE]") || nextToken.equals("[EE]")) {
                lex();
                expr();
            }
            System.out.println("Exit <logicalExpression>");
        }

        private void conditional_statements() {
            System.out.println("Enter <conditional_statements>");
            while (true) {
                lex();
                if (nextToken.equals("[END_IF]")) {
                    break;
                }
                statement();
            }
            System.out.println("Exit <conditional_statements>");
        }

        private void loop() {
            System.out.println("Enter <loop>");
            lex();
            loop_condition();
            loop_statements();
            System.out.println("Exit <loop>");
        }

        private void loop_condition() {
            System.out.println("Enter <loop_condition>");
            if (nextToken.equals("[IDENT]")) {
                lex();
                loop_assignment();
                if (nextToken.equals("[COLON]")) {
                    lex();
                    expr();
                }
                else {
                    error();
                }
            }
            else {
                error();
            }
            System.out.println("Exit <loop_condition>");
        }

        private void loop_assignment() {
            System.out.println("Enter <loop_assignment>");
            if (nextToken.equals("[ASSIGN]")) {
                lex();
                expr();
            }
            else {
                    error();
                }
            System.out.println("Exit <loop_assignment>");
        }

        private void loop_statements() {
            System.out.println("Enter <loop_statements>");
            while (true) {
                lex();
                if (nextToken.equals("[END_LOOP]")) {
                    break;
                }
                statement();
            }
            System.out.println("Exit <loop_statements>");
        }
    
        private void expr() {
            System.out.println("Enter <expr>");
            term();
            while (nextToken.equals("[ADD_OP]") || nextToken.equals("[SUB_OP]")){
                lex();
                term();
            }
            System.out.println("Exit <expr>");
        }
    
        private void term() {
            System.out.println("Enter <term>");
            factor();
            while (nextToken.equals("[MUL_OP]") || nextToken.equals("[DIV_OP]") 
                    || nextToken.equals("[MOD_OP]")) {
            lex();
            factor();
            }
            System.out.println("Exit <term>");
        }
    
        private void factor() {
            System.out.println("Enter <factor>");
            if (nextToken.equals("[IDENT]") || nextToken.equals("[INT_CONST]"))
                lex();
            else {
                if (nextToken.equals("[LP]")) {
                    lex();
                    expr();
                    if (nextToken.equals("[RP]"))
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
