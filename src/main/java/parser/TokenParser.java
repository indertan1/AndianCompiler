package parser;

import beans.Token;
import beans.TokenEnum;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by antonskripacev on 23.03.17.
 */
public class TokenParser {
    private int line = 1;
    private int position = 1;
    private LinkedList<Token> list = new LinkedList<Token>();
    private BufferedReader bufferedReader;
    private int prevSymbol = -1;

    public LinkedList<Token> parse(File f) throws IOException {
        bufferedReader = new BufferedReader(new FileReader((f)));
        int symbol = 0;

        while(symbol != -1){

            if(prevSymbol != -1) {
                symbol = prevSymbol;
                prevSymbol = -1;
            } else {
                symbol = bufferedReader.read();
            }

            if(symbol == -1) {
                break;
            }

            /*
                ПРОПУСКАЕМ СИМВОЛ ПУСТОЙ СТРОКИ
             */
            if(symbol == '\n') {
                line++;
                position = 1;
                continue;
            }

            /*
                ПРОПУСКАЕМ ПРОБЕЛЫ
             */
            if(symbol == ' ') {
                position++;
                continue;
            }

            /*
                УДАЛЯЕМ КОММЕНТАРИИ
             */
            if(symbol == '@') {
                while((symbol = bufferedReader.read()) != '\n' && symbol != -1) {}
                continue;
            }

            Token token = tryToParse(symbol);

            if(token != null) {
                position++;
                list.addLast(token);
                continue;
            } else {
                System.err.println("unlegal token at line " + line + " position " + position);
            }

            position++;
        }

        bufferedReader.close();

        return list;
    }

    @Nullable
    private Token tryToParse(int symbol) throws IOException {
        Token token = null;

        if(Character.isDigit(symbol)) {
            token = tryToParseNumber(symbol);
        } else if(Character.isLetter(symbol)) {
            token = tryToParseID(symbol);
        } else if(symbol == '"') {
            token = tryToParseString(symbol);
        } else {
            token = tryToParseSign(symbol);
        }

        return token;
    }

    @Nullable
    private Token tryToParseString(int symbol) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append((char)symbol);

        symbol = bufferedReader.read();

        while(symbol != '"') {
            if(symbol == -1) return null;

            builder.append((char)symbol);
            symbol = bufferedReader.read();
        }

        builder.append((char)symbol);

        Token token = new Token(line, position);
        token.setType(TokenEnum.STRINGCONST);
        token.setValue(builder.toString());

        return token;
    }

    @Nullable
    private Token tryToParseSign(int symbol) {
        Token t = new Token(line, position);

        switch (symbol) {
            case '+':
                t.setType(TokenEnum.PLUS);
                break;
            case '-':
                t.setType(TokenEnum.MINUS);
                break;
            case '*':
                t.setType(TokenEnum.MULTIPLY);
                break;
            case '/':
                t.setType(TokenEnum.DIVIDE);
                break;
            case '&':
                t.setType(TokenEnum.AND);
                break;
            case '|':
                t.setType(TokenEnum.OR);
                break;
            case '.':
                t.setType(TokenEnum.DOT);
                break;
            case '>':
                t.setType(TokenEnum.GREATER);
                break;
            case ',':
                t.setType(TokenEnum.COMMA);
                break;
            case '<':
                t.setType(TokenEnum.LESS);
                break;
            case '!':
                t.setType(TokenEnum.NOT);
                break;
            case '{':
                t.setType(TokenEnum.OPENBRACE);
                break;
            case '}':
                t.setType(TokenEnum.CLOSEBRACE);
                break;
            case '(':
                t.setType(TokenEnum.OPENPAREN);
                break;
            case ')':
                t.setType(TokenEnum.CLOSEPAREN);
                break;
            case '[':
                t.setType(TokenEnum.OPENSQUARE);
                break;
            case ']':
                t.setType(TokenEnum.CLOSESQUARE);
                break;
            case ';':
                t.setType(TokenEnum.SEMICOLON);
                break;
            case ':':
                t.setType(TokenEnum.COLON);
                break;
            case '=':
                t.setType(TokenEnum.EQUAL);
                break;
            default:
                return null;
        }

        return t;
    }

    @Nullable
    private Token tryToParseNumber(int symbol) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append((char)symbol);

        boolean isDotPass = false;

        while((symbol = bufferedReader.read()) != -1 && (Character.isDigit(symbol) || symbol == '.')) {
            if(symbol == '.' && isDotPass == false) {
                isDotPass = true;
            } else if(symbol == '.' && isDotPass == true) {
                break;
            }

            builder.append((char)symbol);
        }

        if(symbol != -1) {
            prevSymbol = symbol;
        }


        if(builder.charAt(builder.length() - 1) == '.') {
            return null;
        }

        Token t = new Token(line, position);

        if(isDotPass) {
            t.setType(TokenEnum.DOUBLECONST);
            t.setValue(builder.toString());
        } else {
            t.setType(TokenEnum.INTCONST);
            t.setValue(builder.toString());
        }


        return t;
    }

    @Nullable
    private Token tryToParseID(int symbol) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append((char)symbol);

        symbol = bufferedReader.read();

        while(symbol != - 1) {
            if(Character.isLetterOrDigit(symbol)) {
                builder.append((char)symbol);
            } else {
                prevSymbol = symbol;
                break;
            }

            symbol = bufferedReader.read();
        }

        Token token = new Token(line, position);
        TokenEnum tokenEnum = TokenEnum.getEnumElementByValue(builder.toString());

        if(tokenEnum != null) {
            token.setType(tokenEnum);
            token.setValue("");
        } else {
            token.setType(TokenEnum.ID);
            token.setValue(builder.toString());
        }

        return token;
    }
}
