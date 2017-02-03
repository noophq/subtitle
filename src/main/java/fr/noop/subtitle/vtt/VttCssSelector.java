package fr.noop.subtitle.vtt;

import lombok.Builder;

import java.util.List;

/**
 * Created by jdvorak on 03/02/2017.
 */
@Builder
public class VttCssSelector {
    private String pseudo;
    private String elem;
    private String id;
    private List<String> classes;
    private List<String> attrs;


    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("::").append(pseudo);
        bld.append('(');
        if (elem != null) {
            bld.append(elem);
        }
        if (id != null) {
            bld.append('#').append(id);
        }
        if (classes != null) {
            for (String cls : classes) {
                bld.append('.').append(cls);
            }
        }
        if (attrs != null) {
            for (String attr : attrs) {
                bld.append(attr);
            }
        }
        bld.append(')');
        return bld.toString();
    }
}
