import beans.Token;
import parser.TokenParser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by antonskripacev on 23.03.17.
 */
public class Andian {
    public static void main(String[] args) throws IOException {
        /*TODO
        1) Из командной строки получаем название файла(последний! аргумент)
        2) Парсим токены из него в таблицу
        3) Далее эту последовательность парсим(одновременно транслируем в трехадресный код)
        */

        if(args.length > 0) {
            File f = new File(args[args.length - 1]);

            if(f.exists()) {
                LinkedList<Token> listTokens = new TokenParser().parse(f);

                for(Token t : listTokens) {
                    System.out.println("token " + (t.getType().getValue().equals("") ? t.getValue() : t.getType().getValue())  + " parse at line " + t.getLine());
                }
                //TODO then activate grammar parser
            } else {
                System.err.println("File " + args[args.length - 1] + " doesn't exist" );
            }
        } else {
            System.err.println("Please specify a path to the main file");
        }
    }
}
