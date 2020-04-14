import sun.jvm.hotspot.debugger.cdbg.Sym;

import java.util.Scanner;


public class GetSym {

    private StringBuffer line;
    private Scanner scanner = new Scanner(System.in);
    {
        line = new StringBuffer(0);
        scanner.useDelimiter("");
    }

    public static void main(String[] args){

        GetSym sym_parser = new GetSym();

        while(true){
            sym_parser.getWord();
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

    private void readLine(){
        line = new StringBuffer(scanner.nextLine());
        line.append('\n');
    }

    private char nextChar(){
        if(line.length() == 0) readLine();
        return line.charAt(0);
    }

    private void restoreChar(char ch){
        line.insert(ch, 0);
    }

    /**
     * Check whether char c is a whitespace.
     * @param c char c.
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
    public void getWord(){

        String SYM = null;
        String ID = null;
        int NUM = 0;

        char blank_char[] = new char[]{'\n', '\t', ' ', '\r'};

        char ch = getChar();

        // Filter whitespaces
        while(isWhitespace(ch)) ch = getChar();

        // recognize idnet
        // idnet ::= letter {letter | digit}
        if(Character.isLetter(ch)){

            String ident = "" + ch;
            while(Character.isLetter(nextChar()) || Character.isDigit(nextChar())) {
                ch = getChar();
                ident += ch;
            }

            // ensure the length of ident is less than 10 characters
            if(ident.length() > 10)
                throw new RuntimeException("Length of the idnet must <= 10!");

            // if idnet is a symbol
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
            ch = getChar();

            while(Character.isDigit(nextChar())){
                number += ch;
                ch = getChar();
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
            throw new RuntimeException("Input Symbol not valid");
        }

        System.out.println("SYM: " + SYM + ", ID: " + ID + ", NUM: " + NUM);

    }

}


