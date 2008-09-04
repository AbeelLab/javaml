/**
 * %HEADER%
 */
package devtools;

import net.sourceforge.taglets.simple.Logger;
import net.sourceforge.taglets.simple.Parameter;
import net.sourceforge.taglets.simple.inline.ClassNameInlineTaglet;

public class JMLSourceTaglet extends ClassNameInlineTaglet {

    public JMLSourceTaglet(String name) {
        super(name);
    }

    public String getOutput(Logger logger, Parameter parameter, String text) {
        String shortName = getClassName(logger, parameter, false);
        String name = getClassName(logger, parameter, true);
        String path = name.replaceAll("\\.", "/");
        return "<dl><dt><b>Source code:</b></dt><dd><a title=\"Java Machine Learning Library source code for "
                + shortName + "\" href=\"http://java-ml.svn.sourceforge.net/viewvc/java-ml/trunk/" + path
                + ".java?view=markup\">latest from SVN</a></dd></dl>";

    }
}
