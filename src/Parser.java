import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;


public class Parser {

    private ArrayList<Symbol> all_sym;

    private GetSym sym_parser;

    private int lev;
    private int dx;
    private int default_dx;
    private StringBuffer qtree = null;
    private ArrayList<BlockItem> table;
    private ArrayList<CodeItem> code;
    private ASTNode root;

    Parser(){
        sym_parser = new GetSym();

        // get all parsed symbols
        all_sym = new ArrayList<>();
        while(!sym_parser.isEnd()){
            all_sym.add(sym_parser.getWord());
        }
        print_symbols();
    }

    Parser(File file) throws FileNotFoundException {
        sym_parser = new GetSym(file);

        // get all parsed symbols
        all_sym = new ArrayList<>();
        while(!sym_parser.isEnd()){
            all_sym.add(sym_parser.getWord());
        }
    }

    {
        default_dx = 3;

        lev = 0;
        dx = default_dx;

        table = new ArrayList<>();

        root = new ASTNode("root");

        code = new ArrayList<>();

    }

    /**
     * main function
     * @param args array of arguments
     *             [1] pl/0 input file path
     *             [2] output file path
     *
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException{

        // String file_path = "/Users/chuyang/Documents/大三春/编译原理/实验/PL0_code/PL0_code.in";
        // File file = new File(file_path);

        String file_path = args[0];
        String output_path = args[1];
        File input_file = new File(file_path);

        Parser parser = new Parser(input_file);

        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;

        try{


            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element rootElement = doc.createElementNS("https://com.chuyang.compiler_principles/parser", "ParserOutput");
            doc.appendChild(rootElement);

            Element symbolTable = doc.createElement("SymbolTable");
            rootElement.appendChild(symbolTable);
            ArrayList<Symbol> all_symbols = parser.get_symbol_table();
            int id = 0;

            for(Symbol symbol: all_symbols){
                System.out.println(symbol.SYM);

                Element symbolElement = doc.createElement("Symbol");
                symbolElement.setAttribute("id", (id++) + "");

                Element symElement = doc.createElement("sym");
                symElement.appendChild(doc.createTextNode(symbol.SYM));
                symbolElement.appendChild(symElement);

                Element idElement = doc.createElement("id");
                idElement.appendChild(doc.createTextNode(symbol.ID));
                symbolElement.appendChild(idElement);

                Element numElement = doc.createElement("num");
                numElement.appendChild(doc.createTextNode(symbol.NUM + ""));
                symbolElement.appendChild(numElement);

                symbolTable.appendChild(symbolElement);
            }

            parser.parse();

            Element blockTable = doc.createElement("BlockTable");
            rootElement.appendChild(blockTable);
            ArrayList<BlockItem> all_blockitems = parser.get_block_table();
            id = 0;

            for(BlockItem item: all_blockitems){
                Element blockElement = doc.createElement("BlockItem");
                blockElement.setAttribute("id", (id++) + "");

                Element nameElement = doc.createElement("name");
                nameElement.appendChild(doc.createTextNode(item.name));
                blockElement.appendChild(nameElement);

                Element kindElement = doc.createElement("kind");
                kindElement.appendChild(doc.createTextNode(item.kind));
                blockElement.appendChild(kindElement);

                Element valueElement = doc.createElement("value");
                valueElement.appendChild(doc.createTextNode("" + item.value));
                blockElement.appendChild(valueElement);

                Element levelElement = doc.createElement("level");
                levelElement.appendChild(doc.createTextNode("" + item.level));
                blockElement.appendChild(levelElement);

                Element addressElement = doc.createElement("address");
                addressElement.appendChild(doc.createTextNode("" + item.address));
                blockElement.appendChild(addressElement);

                blockTable.appendChild(blockElement);
            }


            Element codeTable = doc.createElement("codeTable");
            rootElement.appendChild(codeTable);
            ArrayList<CodeItem> all_codes = parser.get_code_table();
            id = 0;

            for(CodeItem code: all_codes){
                Element codeElement = doc.createElement("CodeItem");
                codeElement.setAttribute("id", (id++) + "");

                Element opElement = doc.createElement("f");
                opElement.appendChild(doc.createTextNode(code.op));
                codeElement.appendChild(opElement);

                Element levelElement = doc.createElement("l");
                levelElement.appendChild(doc.createTextNode(code.l + ""));
                codeElement.appendChild(levelElement);

                Element mElement = doc.createElement("m");
                mElement.appendChild(doc.createTextNode(code.m + ""));
                codeElement.appendChild(mElement);

                codeTable.appendChild(codeElement);
            }

            Element forest = doc.createElement("forest");
            String latex_tree = parser.get_tree().toString();
            forest.appendChild(doc.createTextNode(latex_tree));
            rootElement.appendChild(forest);

            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // send DOM to file
            tr.transform(new DOMSource(rootElement),new StreamResult(new FileOutputStream(output_path)));

        } catch(Exception e){
            e.printStackTrace();
        }


//        Parser parser = new Parser();
//        parser.parse();
//
//        parser.print_table();
//        parser.print_code();

    }

    public ArrayList<Symbol> get_symbol_table(){
        return all_sym;
    }

    public StringBuffer get_tree(){
        return qtree;
    }

    public ArrayList<CodeItem> get_code_table(){
        return code;
    }

    public void print_symbols(){
        for(Symbol sym: all_sym){
            System.out.println(sym.SYM);
        }
    }

    public void print_table(){
        for(BlockItem bi: table){
            System.out.println("name: " + bi.name + ", kind: " +bi.kind + ", level: " + bi.level +", value: " + bi.value + ", address: " + bi.address);
        }
    }

    public  void print_code(){
        for(CodeItem ci: code){
            System.out.println("f: " + ci.op + ", l: " + ci.l + ", m: " + ci.m);
        }
    }

    public void traverse(ASTNode root, StringBuffer qtree){
        qtree.append(" [" + root.name );
        for(ASTNode node : root.children){
            traverse(node, qtree);
        }
        qtree.append("]");
    }

    public ArrayList<BlockItem> get_block_table(){
        return table;
    }

    public void parse(){
        this.block(root);
        qtree = new StringBuffer("");
        traverse(root, qtree);
        System.out.println(qtree);
    }

    private boolean hasToken(){
        return !all_sym.isEmpty();
    }



    private Symbol nextToken(){
        if(all_sym.isEmpty()) throw new RuntimeException("Symbol list is empty");
        return all_sym.get(0);
    }

    private Symbol getNextToken(){
        if(all_sym.isEmpty()) throw new RuntimeException("Symbol list is empty");
        Symbol result = all_sym.get(0);
        all_sym.remove(0);
        return result;
    }

    private BlockItem find_table(String name){
        for(BlockItem b: table){
            if(b.name.equals(name)) return b;
        }
        return null;
    }

    /**
     * generated code
     * @param op
     * @param level
     * @param modifier
     */
    private void emit(String op, int level, int modifier){

        CodeItem new_code = new CodeItem();
        new_code.op = op;
        new_code.l = level;
        new_code.m = modifier;

        code.add(new_code);

    }


    private void block(ASTNode parent){

        Symbol token = null;

        ASTNode block = new ASTNode("block");
        parent.add_child(block);

        try {

            int space = 0;
            int ctemp = code.size();
            emit("JMP", 0, 0);

            if (nextToken().SYM.equals("intsym")) {

                ASTNode var_declaration = new ASTNode("var-declaration");

                block.add_child(var_declaration);

                getNextToken(); // consume "const"
                var_declaration.add_child(new ASTNode("var"));

                // repeat if encounter ","

                var_declaration(var_declaration);

                ASTNode var_declarations = new ASTNode("{\",\"~ident}");
                var_declaration.add_child(var_declarations);

                while(nextToken().SYM.equals("commasym")){
                    getNextToken(); // consume ","
                    var_declarations.add_child(new ASTNode("{,}"));
                    var_declaration(var_declarations);
                    space++;
                }

                // var declaration must ended with ";"
                assert nextToken().SYM.equals("semicolomsym");
                getNextToken();
                var_declaration.add_child(new ASTNode("{;}"));

                space++;

            }

            if (nextToken().SYM.equals("constsym")) {

                ASTNode const_declaration = new ASTNode("const-declaration");
                block.add_child(const_declaration);

                getNextToken(); // consume "const"
                const_declaration.add_child(new ASTNode("const"));

                // repeat if encounter ","

                const_declaration(const_declaration);

                ASTNode const_declarations = new ASTNode("{\",\"~ident~\"=\"~number}");
                const_declaration.add_child(const_declarations);

                while (nextToken().SYM.equals("commasym")){
                    getNextToken(); // consume ","
                    const_declarations.add_child(new ASTNode("{,}"));
                    const_declaration(const_declarations);
                    space++;
                }

                // const declaration must ended with ";"
                assert getNextToken().SYM.equals("semicolomsym");
                const_declaration.add_child(new ASTNode("{;}"));
                space++;
            }

            // n (n>=0) procedures
            while (nextToken().SYM.equals("procsym")) {

                getNextToken();

                ASTNode procedure_declaration = new ASTNode("procedure-declaration");
                block.add_child(procedure_declaration);
                procedure_declaration.add_child(new ASTNode("procedure"));

                procedure_declaration(procedure_declaration);

            }

            code.get(ctemp).m = code.size();
            emit("INC", 0, space + 3);
            statement(block);
        }
        catch(RuntimeException e){
            e.printStackTrace();
            System.out.println("/***** Current block parsing terminated *****/");
        }
    }

    /**
     * parse var declaration.
     * update block table.
     */
    private void var_declaration(ASTNode parent){

        ASTNode ident = new ASTNode("ident");
        parent.add_child(ident);

        Symbol token = getNextToken();

        // the token type following var/int  must be identsym.
        assert token.SYM.equals("identsym");

        BlockItem block_item = new BlockItem();

        block_item.kind = "variable";
        block_item.name = token.ID;
        block_item.level = lev;
        block_item.address = dx;

        dx++;

        // System.out.println(block_item.kind + " " + block_item.name + " " + block_item.level + " " + block_item.address);

        table.add(block_item);

        ident.add_child(new ASTNode(block_item.name));

    }

    /**
     * parse const declaration.
     * update block table.
     */
    private void const_declaration(ASTNode parent){

        Symbol const_name = getNextToken();

        // in const declaration, the second token must be a ident
        assert const_name.SYM.equals("identsym");

        ASTNode ident = new ASTNode("ident");
        parent.add_child(ident);

        Symbol token = getNextToken();

        // the third token must be equal symbol
        assert token.SYM.equals("eqlsym");
        parent.add_child(new ASTNode("{=}"));

        token = getNextToken();

        // the forth token must be a number
        assert token.SYM.equals("numbersym");

        ASTNode number = new ASTNode("number");
        parent.add_child(number);

        BlockItem block_item = new BlockItem();
        block_item.kind = "constant";
        block_item.value = token.NUM;
        block_item.name = const_name.ID;

        System.out.println(block_item.kind + " " + block_item.name + " " + block_item.value);

        table.add(block_item);

        ident.add_child(new ASTNode(block_item.name));
        number.add_child(new ASTNode("" + block_item.value));

    }

    /**
     * parse procedure declaration.
     */
    private void procedure_declaration(ASTNode parent){

        Symbol token = getNextToken();

        // in procedure declaration, the second token must be a ident
        assert token.SYM.equals("identsym");

        ASTNode ident = new ASTNode("ident");
        parent.add_child(ident);
        ident.add_child(new ASTNode(token.ID));

        BlockItem block_item = new BlockItem();
        block_item.kind = "procedure";
        block_item.name = token.ID;
        block_item.level = lev;
        block_item.address = code.size();

        // System.out.println("procedure name: " + block_item.name);

        table.add(block_item);

        token = getNextToken();

        // the third token must be ";"
        assert token.SYM.equals("semicolomsym");

        parent.add_child(new ASTNode("{;}"));

        // enter a new block
        lev++;
        int old_dx = dx;
        dx = default_dx;

        block(parent);
        emit("OPR", 0, 0); // exit

        dx = old_dx;
        lev--;

        token = getNextToken();

        assert token.SYM.equals("semicolomsym");

        parent.add_child(new ASTNode("{;}"));

    }

    /**
     * parse statement.
     * Note statement can be empty.
     */
    private void statement(ASTNode parent){

        ASTNode statement = new ASTNode("statement");
        parent.add_child(statement);

        if(nextToken().SYM.equals("identsym")){
            // ident ":=" expression
            Symbol token = getNextToken();
            BlockItem fr = find_table(token.ID);
            assert fr != null;

            ASTNode ident = new ASTNode("ident");
            statement.add_child(ident);
            ident.add_child(new ASTNode(token.ID));

            assert nextToken().SYM.equals("becomessym");
            getNextToken();

            statement.add_child(new ASTNode("{:=}"));

            expression(statement);

            emit("STO", lev - fr.level, fr.address);

        }else if(nextToken().SYM.equals("callsym")){
            // "call" ident
            getNextToken();

            statement.add_child(new ASTNode("call"));
            assert nextToken().SYM.equals("identsym");
            Symbol token = getNextToken();

            BlockItem fr = find_table(token.ID);

            ASTNode ident = new ASTNode("ident");
            statement.add_child(ident);
            ident.add_child(new ASTNode(token.ID));

            emit("CAL", lev, fr.address);

        }else if(nextToken().SYM.equals("beginsym")){
            // "begin" statement { ";" statement } "end"
            getNextToken();

            statement.add_child(new ASTNode("begin"));

            ASTNode statements = new ASTNode("{ \";\" statement }");
            statement.add_child(statements);
            statement(statements);
            while(nextToken().SYM.equals("semicolomsym")){
                getNextToken();

                statements.add_child(new ASTNode("{;}"));

                statement(statements);
            }
            assert nextToken().SYM.equals("endsym");
            statement.add_child(new ASTNode("end"));
            getNextToken();
        }else if(nextToken().SYM.equals("ifsym")){
            // "if" condition "then" statement ["else" statement]

            getNextToken();

            statement.add_child(new ASTNode("if"));

            condition(statement);
            assert nextToken().SYM.equals("thensym");
            getNextToken();

            statement.add_child(new ASTNode("then"));

            int ctemp = code.size();
            emit("JPC", 0, 0);

            statement(statement);

            code.get(ctemp).m = table.size();

            if(nextToken().SYM.equals("elsesym")){
                // TODO: ?
                code.get(ctemp).m++;
                ctemp = code.size();
                emit("JMP", 0, 0);

                getNextToken();

                statement.add_child(new ASTNode("else"));

                statement(statement);

                code.get(ctemp).m = table.size();
            }
        }else if(nextToken().SYM.equals("whilesym")){
            // "while" condition "do" statement
            int cx1 = code.size();

            getNextToken();

            statement.add_child(new ASTNode("while"));

            condition(statement);

            int cx2 = code.size();
            emit("JPC", 0, 0);

            assert nextToken().SYM.equals("dosym");
            getNextToken();

            statement.add_child(new ASTNode("do"));
            statement(statement);

            emit("JMP", 0, cx1);
            code.get(cx2).m = table.size();

        }else if(nextToken().SYM.equals("readsym")){
            // TODO: read
            // "read" ident
            getNextToken();

            statement.add_child(new ASTNode("read"));

            assert nextToken().SYM.equals("lparentsym");
            getNextToken();

            statement.add_child(new ASTNode("("));

            assert nextToken().SYM.equals("identsym");
            Symbol token = getNextToken();

            BlockItem fr = find_table(token.ID);
            assert fr != null;

            ASTNode ident = new ASTNode("ident");
            statement.add_child(ident);
            ident.add_child(new ASTNode(token.ID));

            ASTNode idents = new ASTNode("{\",\" ident}");
            statement.add_child(idents);

            emit("SIO", 0, 1);
            emit("STO", lev - fr.level, fr.address);

            while(nextToken().SYM.equals("commasym")){
                idents.add_child(new ASTNode(","));
                getNextToken();
                assert nextToken().SYM.equals("identsym");
                token = getNextToken();
                ASTNode new_ident = new ASTNode("ident");
                idents.add_child(new_ident);
                new_ident.add_child(new ASTNode(token.ID));
            }

            assert nextToken().SYM.equals("rparentsym");
            getNextToken();
            statement.add_child(new ASTNode(")"));

        }else if(nextToken().SYM.equals("writesym")){
            // TODO: write
            // "write" expression
            getNextToken();

            statement.add_child(new ASTNode("write"));

            assert nextToken().SYM.equals("lparentsym");
            getNextToken();

            statement.add_child(new ASTNode("("));

            expression(statement);

            ASTNode expressions = new ASTNode("{\",\" expression}");
            statement.add_child(expressions);

            while(nextToken().SYM.equals("commasym")){
                getNextToken();
                expressions.add_child(new ASTNode(","));
                expression(expressions);
            }

            assert nextToken().SYM.equals("rparentsym");
            getNextToken();
            statement.add_child(new ASTNode(")"));
        }

    }

    /**
     * parse expression.
     * expression ::= [ "+" | "-"] term { ("+" | "-") term}
     */
    private void expression(ASTNode parent){

        ASTNode expression = new ASTNode("expression");
        parent.add_child(expression);

        Symbol addop;

        // get next token if it equals "+" or "-"
        if(nextToken().SYM.equals("plussym") || nextToken().SYM.equals("minussym")) {
            addop = getNextToken();


            expression.add_child(new ASTNode(addop.SYM.equals("plussym") ? "+" : "-"));

            term(expression);

            if(addop.SYM.equals("minussym")){
                emit("OPR", 0, 1); // 1 for NEG
            }

        }else{
            term(expression);
        }

        ASTNode terms = new ASTNode("{~(\"+\"~|~\"-\")~term}");
        expression.add_child(terms);

        while(nextToken().SYM.equals("plussym") || nextToken().SYM.equals("minussym")){
            // consume "+" or "-"
            addop = getNextToken();

            terms.add_child(new ASTNode(addop.SYM.equals("plussym") ? "+" : "-"));

            term(terms);

            if (addop.SYM.equals("plussym")){
                emit("OPR", 0, 2); // 2 for ADD
            }else{
                emit("OPR", 0, 3); // 3 for SUB
            }
        }

    }

    /**
     * parse term.
     * term ::= factor {("*" | "/") factor}
     */
    private void term(ASTNode parent){

        ASTNode term = new ASTNode("term");
        parent.add_child(term);

        factor(term);

        ASTNode factors = new ASTNode("{(\"*\"~|~\"/\")~factor}");
        parent.add_child(factors);

        while(nextToken().SYM.equals("multsym") || nextToken().SYM.equals("slashsym")){
            // consume "*" or "/"
            Symbol op = getNextToken();

            factors.add_child(new ASTNode(op.SYM.equals("multsym") ? "*" : "/"));
            factor(factors);
            if(op.SYM.equals("multsym")){
                emit("OPR", 0, 4); // 4 for MUL
            }else{
                emit("OPR", 0, 5); // 5 for DIV
            }
        }

    }

    /**
     * parse factor.
     * factor ::= ident | number | "(" expression ")"
     */
    private void factor(ASTNode parent){

        ASTNode factor = new ASTNode("factor");
        parent.add_child(factor);


        if(nextToken().SYM.equals("identsym")) {
            Symbol id = getNextToken();
            BlockItem fr = find_table(id.ID);

            assert fr != null;

            emit("LOD", lev - fr.level, fr.address);

            ASTNode ident = new ASTNode("ident");
            factor.add_child(ident);
            ident.add_child(new ASTNode(id.ID));
        }
        else if(nextToken().SYM.equals("numbersym")){
            Symbol num = getNextToken();
            emit("LIT", 0, num.NUM);

            ASTNode number = new ASTNode("number");
            factor.add_child(number);
            number.add_child(new ASTNode("" + num.NUM));
        }
        else if(nextToken().SYM.equals("lparentsym")){
            // consume "(" token
            getNextToken();

            getNextToken();
            factor.add_child(new ASTNode("("));

            expression(parent);
            assert nextToken().SYM.equals("rparentsym");

            // consume ")" token
            getNextToken();

            factor.add_child(new ASTNode(")"));
        }
        else{
            throw new RuntimeException("Factor is not valid");
        }

    }

    /**
     * parse condition.
     * condition ::= "odd" expression | expression rel-op expression
     */
    private void condition(ASTNode parent){

        ASTNode condition = new ASTNode("condition");
        parent.add_child(condition);

        if(nextToken().SYM.equals("oddsym")){
            getNextToken();
            condition.add_child(new ASTNode("odd"));
            expression(condition);
            emit("ODD", 0, 0);
        }else{
            expression(condition);
            int r = relation_operation(condition) + 8;
            expression(condition);
            emit("OPR", 0, r);
        }

    }

    /**
     * parse relation operations.
     */
    private int relation_operation(ASTNode parent){

        if(nextToken().SYM.equals("eqlsym") || nextToken().SYM.equals("neqsym") || nextToken().SYM.equals("leqsym")
                || nextToken().SYM.equals("gtrsym") || nextToken().SYM.equals("geqsym") || nextToken().SYM.equals("lessym")){
            Symbol token = getNextToken();
            if(token.SYM.equals("eqlsym")) {
                parent.add_child(new ASTNode("$=$"));
                return 0;
            }
            if(token.SYM.equals("neqsym")) {
                parent.add_child(new ASTNode("$<>$"));
                return 1;
            }
            if(token.SYM.equals("leqsym")) {
                parent.add_child(new ASTNode("$<=$"));
                return 3;
            }
            if(token.SYM.equals("gtrsym")) {
                parent.add_child(new ASTNode("$>$"));
                return 4;
            }
            if(token.SYM.equals("geqsym")) {
                parent.add_child(new ASTNode("$>=$"));
                return 5;
            }
            if(token.SYM.equals("lessym")) {
                parent.add_child(new ASTNode("$<$"));
                return 2;
            }
        }
        throw new RuntimeException("relation operation is invalid");

    }

}


class BlockItem{

    public String name = "";
    public String kind = "";
    public int value = 0;
    public int level = 0;
    public int address = 0;

}

/**
 * f: op code
 * l: lexicographical level
 * a: modifier
 */
class CodeItem{

    public String op = "";
    public int l = 0;
    public int m = 0;

}

/**
 * Node of abstract syntax tree
 */
class ASTNode{
    public String name;
    ArrayList<ASTNode> children = new ArrayList<>();

    ASTNode(String name){
        this.name = name;
    }

    public void add_child(ASTNode child){
        children.add(child);
    }

}

class AST{
    ASTNode root = new ASTNode("Main procedure");
}
