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
            System.out.println(parser.getProgram());
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }   
    }

    public static class tokenIterator implements Iterator<String> {
    private String text;
    private Queue<String> tokens;
    int index = 0;
    int oldIndex = 0;
    boolean isIntLiteralOrIdent = false;
    
    public tokenIterator(String text) {
        this.text = text;
        this.tokens = new LinkedList<>();
        parseTokens();
    }
    
    private void parseTokens() {
        while (index < text.length()) {
            isIntLiteralOrIdent = false;
            while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
                index++;
            }
            if (index < text.length()) {
                StringBuilder tokenBuilder = new StringBuilder();
                char ch = text.charAt(index);
                
                switch (ch) {
                    case 'i':
                        if (checkIsIf(text, index)) {
                            tokenBuilder.append("[IF]");
                            index += 2; // Skip 'if'
                        } else {
                            tokenBuilder.append(handleIdent());
                            tokens.offer(text.substring(oldIndex, index));
                        }
                        break;
                    case 'p':
                        if (checkIsProgram(text, index)) {
                            tokenBuilder.append("[PROGRAM]");
                            index += 7; // Skip 'program'
                        } else {
                            tokenBuilder.append(handleIdent());
                            isIntLiteralOrIdent = true;
                        }
                        break;
                    case 'e':
                        if (checkIsEnd_Something(text, index)) {
                            if (checkIsEnd_If(text, index)) {
                                tokenBuilder.append("[END_IF]");
                                index += 6; // Skip 'end_if'
                            }
                            else if (checkIsEnd_Loop(text, index)) {
                                tokenBuilder.append("[END_LOOP]");
                                index += 8; // Skip 'end_loop'
                            } 
                            else if (checkIsEnd_Program(text, index)) {
                                tokenBuilder.append("[END_PROGRAM]");
                                index += 11; // Skip 'end_program'
                        } 
                        else {
                            tokenBuilder.append(handleIdent());
                            isIntLiteralOrIdent = true;
                        }
                        break;
                    }
                    case 'l':
                        if (checkIsLoop(text, index)) {
                            tokenBuilder.append("[LOOP]");
                            index += 4; // Skip 'end_loop'
                        } else {
                            tokenBuilder.append(handleIdent());
                            isIntLiteralOrIdent = true;
                        }
                        break;
                    case '=':
                        if (checkIsEEGEOrLE(text, index)) {
                            tokenBuilder.append("[EE]");
                            index += 2; // Skip '=='
                        } else {
                            tokenBuilder.append("[ASSIGN]");
                            index++; // Skip '='
                        }
                        break;
                    case '<':
                        if (checkIsEEGEOrLE(text, index)) {
                            tokenBuilder.append("[LE]");
                            index += 2; // Skip '<='
                        } else {
                            tokenBuilder.append("[LT]");
                            index++; // Skip '<'
                        }
                        break;
                    case '>':
                        if (checkIsEEGEOrLE(text, index)) {
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
                            isIntLiteralOrIdent = true;
                        } else if (Character.isLetter(ch)) {
                            tokenBuilder.append(handleIdent());
                            isIntLiteralOrIdent = true;
                        } else {
                            index++; // Skip unknown character
                            tokenBuilder.append("[UNKNOWN]");
                        }
                        break;
                }
                tokens.offer(tokenBuilder.toString());
                if (isIntLiteralOrIdent) {
                    tokens.offer(text.substring(oldIndex, index));
                }
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
        oldIndex = index;
        while (index < text.length() && Character.isDigit(text.charAt(index))) {
            index++;
        }
        if (index < text.length() && Character.isLetter(text.charAt(index))) {
            // Error: Integer literal followed by a letter
            throw new IllegalArgumentException("Error: Integer literal followed by a letter at position " + index);
        }
        return "[INT_CONST]";
    } 
    private String handleIdent() {
        oldIndex = index;
        while (index < text.length() && (Character.isLetterOrDigit(text.charAt(index)))) {
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
                char ch2 = line.charAt(i+1);
                if(ch2 == 'f') {
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
        private String nextNextToken;
        private String intConstOrIdent;
        private int lineNum = 0;
        private String program = "";

        public String getProgram() {
            return program;
        }

        public recursiveDescentParser(Queue<String> tokens) {
            this.tokens = tokens;
            nextToken = tokens.poll();
        }

        private void lex() {
            if (tokens.size() > 0) {
                nextToken = tokens.poll();
                nextNextToken = tokens.peek();
                if(nextNextToken != null) {
                    if (nextNextToken.charAt(0) != '[') {
                        intConstOrIdent = tokens.poll();
                    }
                }
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
                program += "public static void main(String[] args) { \n";
                lineNum++;
                statements();
            } else {
                error(); // Expected PROGRAM token
            }
            program += "}\n";
            System.out.println("Exit <program>");
        }
        

        private void statements() {
            System.out.println("Enter <statements>");
            while (true) {
                lex();
                if (nextToken.equals("[END_PROGRAM]")) {
                    lineNum++;
                    break;
                }
                statement();
            }
            System.out.println("Exit <statements>");
        }

        private void statement() {
            System.out.println("Enter <statement>");
            lineNum++;
            program += "\t";
            if (nextToken.equals("[IDENT]")) {
                program += "int "+intConstOrIdent;
                lex();
                assignment();
            } else if (nextToken.equals("[IF]")) {
                program += "if (";
                lex();
                conditional();
            } else if (nextToken.equals("[LOOP]")) {
                program += "for (";
                lex();
                System.out.println(nextToken);
                System.out.println(intConstOrIdent);
                loop();
            } else {
                error();
            }
            program += "\n";
            System.out.println("Exit <statement>");
        }

        private void assignment() {
            System.out.println("Enter <assignment>");
            if (nextToken.equals("[ASSIGN]")) {
                program += " = ";
                lex();
                expr();
                if(!nextToken.equals("[SEMI]")) {
                    error();
                }
                program += ";";
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
            if (nextToken.equals ("[IDENT]")) {
                program += intConstOrIdent;
                lex();
            }
            else {
                error();
            }
            if (nextToken.equals("[LT]") || nextToken.equals("[GT]") || nextToken.equals("[LE]") || 
                    nextToken.equals("[GE]") || nextToken.equals("[EE]")) {
                switch (nextToken) {
                    case "[LT]":
                        program += " < ";
                        break;
                    case "[GT]":
                        program += " > ";
                        break;
                    case "[LE]":
                        program += " <= ";
                        break;
                    case "[GE]":
                        program += " >= ";
                        break;
                    case "[EE]":
                        program += " == ";
                        break;
                    default:
                        error();
                }
                lex();
                expr();
                program += ")";
            }
            System.out.println("Exit <logicalExpression>");
        }

        private void conditional_statements() {
            System.out.println("Enter <conditional_statements>");
            program += " {\n";
            while (true) {
                lex();
                if (nextToken.equals("[END_IF]")) {
                    lineNum++;
                    program += "\t}\n";
                    break;
                }
                program += "\t";
                statement();
            }
            System.out.println("Exit <conditional_statements>");
        }

        private void loop() {
            System.out.println("Enter <loop>");
            if(nextToken.equals("[LP]")) {
                lex();
                loop_condition();
                loop_statements();
                System.out.println("Exit <loop>");
                }
            else {
                error();
            }
        }

        private void loop_condition() {
            System.out.println("Enter <loop_condition>");
            if (nextToken.equals("[IDENT]")) {
                lex();
                program += "int " + intConstOrIdent;
                String tempVariable = intConstOrIdent;
                loop_assignment();
                if (nextToken.equals("[COLON]")) {
                    program += tempVariable + " <";
                    lex();
                    expr();
                    program += " ; ";
                    program += tempVariable + "++)";
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
                program += " = ";
                lex();
                expr();
                program += "; ";
            }
            else {
                    error();
            }
            System.out.println("Exit <loop_assignment>");
        }

        private void loop_statements() {
            System.out.println("Enter <loop_statements>");
            program += " {\n";
            while (true) {
                lex();
                if (nextToken.equals("[END_LOOP]")) {
                    lineNum++;
                    program += "\t}\n";
                    break;
                }
                program += "\t";
                statement();
            }
            System.out.println("Exit <loop_statements>");
        }
    
        private void expr() {
            System.out.println("Enter <expr>");
            term();
            while (nextToken.equals("[ADD_OP]") || nextToken.equals("[SUB_OP]")){
                switch (nextToken) {
                    case "[ADD_OP]":
                        program += " + ";
                        break;
                    case "[SUB_OP]":
                        program += " - ";
                        break;
                    default:
                        error();
                }
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
                switch (nextToken) {
                    case "[MUL_OP]":
                        program += " * ";
                        break;
                    case "[DIV_OP]":
                        program += " / ";
                        break;
                    case "[MOD_OP]":
                        program += " % ";
                        break;
                    default:
                        error();
                }
                lex();
                factor();
            }
            System.out.println("Exit <term>");
        }
    
        private void factor() {
            System.out.println("Enter <factor>");
            if (nextToken.equals("[IDENT]") || nextToken.equals("[INT_CONST]")) {
                lex();
                program += intConstOrIdent;
            }
            else {
                if (nextToken.equals("[LP]")) {
                    program += "(";
                    lex();
                    expr();
                    if (nextToken.equals("[RP]")) {
                        program += ")";
                        lex();
                    }
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
