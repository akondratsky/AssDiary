package assdiary.model;

import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.stachek66.nlp.mystem.holding.Request;
import ru.stachek66.nlp.mystem.model.Info;
import scala.Option;
import scala.collection.JavaConversions;

import java.io.File;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Lemmatizator {

    private MyStem mystemAnalyzer;
    private ArrayList<String> lemms;

    public Lemmatizator() {
        mystemAnalyzer = new Factory("-igd --format json --weight")
                .newMyStem("3.0", Option.<File>empty())
                .get();
    }

    public ArrayList<String> lemmatizate(String string) {
        lemms = new ArrayList<>();
        try {
            final Iterable<Info> result =
                    JavaConversions.asJavaIterable(
                            mystemAnalyzer
                                    .analyze(Request.apply(string))
                                    .info()
                                    .toIterable());

            for (final Info info : result) {
                try {
                    lemms.add(info.lex().get());
                } catch (NoSuchElementException ex) { }

            }
        }catch (MyStemApplicationException ex) {
                ex.printStackTrace();
        }
        return lemms;
    }

}
