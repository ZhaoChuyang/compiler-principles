import java.io.EOFException;
import java.util.NoSuchElementException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;


public class GetSym {

    private StringBuffer line;
    private Scanner scanner;
    private boolean isEnd = false;

    {
        line = new StringBuffer(0);
        System.out.println("*-----Symbol Parsing Begin-----*");
    }

    GetSym(){
        scanner = new Scanner(System.in);
        scanner.useDelimiter("");
    }

    GetSym(File file) throws FileNotFoundException {
        scanner = new Scanner(file);
        scanner.useDelimiter("");
    }

    // Test Program
    public static void main(String[] args) throws FileNotFoundException{

        // /Users/chuyang/Downloads/demo.txt
        // /Users/chuyang/Documents/大三春/编译原理/实验/PL0_code/PL0_code.in
        File file = new File("/Users/chuyang/Downloads/demo.txt");
        GetSym sym_parser = new GetSym();


        int cnt = 0;
        while(!sym_parser.isEnd()){

            Symbol sym= sym_parser.getWord();
            System.out.print( sym.SYM + " " + sym.ID + " " + sym.NUM + ",\t");
        }

    }


    /**
     * Read a single character.
     * @return current read character.
     */
    private char getChar(){
        if(line.length() == 0) readLine();
        char ch = line.charAt(0);
        line.deleteCharAt(0);
        return ch;
    }

    /**
     * Read next line.
     */
    private void readLine(){
        // only read new line when current line has been consumed.
        while(line.length() == 0){
            line = new StringBuffer(scanner.nextLine());
            if(line.length() != 0) line.append('\n');
        }
    }

    /**
     * Peek the next char but don't consume it.
     * @return Next char to read.
     */
    private char nextChar(){
        if(line.length() == 0) readLine();
        return line.charAt(0);
    }

    public boolean isEnd(){
        try{
            // in case at the end of file only token '\n' left and the last token is EOF
            while(line.length() == 1){
                line = new StringBuffer(scanner.nextLine());
                line.append('\n');
            }
        } catch (NoSuchElementException e){
            System.out.println("*-----Symbol Parsing End-----*");
            return true;
        }
        return false;
    }

    /**
     * Check whether char c is a whitespace.
     * @param c
     * @return returns true if c is a whitespace otherwise returns false.
     */
    private boolean isWhitespace(char c){

        char blank_char[] = new char[]{'\n', '\t', ' ', '\r'};

        for(char blank: blank_char){
            if(c == blank) return true;
        }
        return false;
    }

    /**
     * Function to parse symbols.
     */
    public Symbol getWord(){

        String SYM = "";
        String ID = "";
        int NUM = 0;

        char blank_char[] = new char[]{'\n', '\t', ' ', '\r'};

        char ch = getChar();


        // Filter whitespaces
        while(isWhitespace(ch)) ch = getChar();

        // recognize ident
        // ident ::= letter {letter | digit}
        if(Character.isLetter(ch)){

            String ident = "" + ch;
            while(Character.isLetter(nextChar()) || Character.isDigit(nextChar())) {
                ch = getChar();
                ident += ch;
            }

            // ensure the length of ident is less than 10 characters
            if(ident.length() > 10)
                throw new RuntimeException("Variable length must <= 10!");

            // if ident is a symbol
            if(SymTable.is_symbol(ident)){
                SYM = SymTable.get_internal_name(ident);
            }else{
                SYM = "identsym";
                ID = ident;
            }

        }

        // recognize number
        // number ::= digit {digit}
        else if(Character.isDigit(ch)){

            String number = "" + ch;

            while(Character.isDigit(nextChar())){
                number += getChar();
            }

            SYM = "numbersym";
            NUM = Integer.parseInt(number);


        }else if(ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '(' || ch == ')' || +
                ch == ',' || ch == ';' || ch == '.'){
            SYM = SymTable.get_internal_name("" + ch);

        }else if(ch == '<'){
            String symbol = "" + ch;

            if(nextChar() == '=' || nextChar() == '>'){
                symbol += getChar();
            }

            SYM = SymTable.get_internal_name(symbol);

        }else if(ch == '>'){
            String symbol = "" + ch;

            if(nextChar() == '='){
                symbol += getChar();
            }

            SYM = SymTable.get_internal_name(symbol);

        }else if(ch == ':'){
            String symbol = "" + ch;

            if(nextChar() != '='){
                throw new RuntimeException(": can not be followed by any character other than =");
            }
            else symbol += getChar();

            SYM = SymTable.get_internal_name(symbol);

        }else{
            throw new RuntimeException("Input symbol not valid");
        }

        return new Symbol(SYM, ID, NUM);


    }

}

class Symbol{

    public String SYM;
    public String ID;
    public int NUM;

    {
        SYM = "";
        ID = "";
        NUM = 0;
    }

    Symbol(String SYM, String ID, int NUM){
        this.SYM = SYM;
        this.ID = ID;
        this.NUM = NUM;
    }

}


